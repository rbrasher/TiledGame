package Path;

import org.andengine.util.adt.list.ShiftList;
import org.andengine.util.adt.map.LongSparseArray;
import org.andengine.util.adt.queue.IQueue;
import org.andengine.util.adt.queue.SortedQueue;
import org.andengine.util.adt.queue.UniqueQueue;
import org.andengine.util.adt.spatial.bounds.util.IntBoundsUtils;
import org.andengine.util.algorithm.path.ICostFunction;
import org.andengine.util.algorithm.path.IPathFinderMap;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.AStarPathFinder;
import org.andengine.util.algorithm.path.astar.IAStarHeuristic;

public class MyAstarPathFinder<T> extends AStarPathFinder<T> {

	public Path findPath(int pMaxSearchDepth, final IPathFinderMap<T> pPathFinderMap, final int pXMin, final int pYMin, final int pXMax, final int pYMax, final T pEntity, final int pFromX, final int pFromY, final int pToX, final int pToY, final boolean pAllowDiagonal, final IAStarHeuristic<T> pAStarHeuristic, final ICostFunction<T> pCostFunction) {
		return this.findPath(pMaxSearchDepth, pPathFinderMap, pXMin, pYMin, pXMax, pYMax, pEntity, pFromX, pFromY, pToX, pToY, pAllowDiagonal, pAStarHeuristic, pCostFunction, Float.MAX_VALUE, null);
	}
	
	public Path findPath(int pMaxSearchDepth, final IPathFinderMap<T> pPathFinderMap, final int pXMin, final int pYMin, final int pXMax, final int pYMax, final T pEntity, final int pFromX, final int pFromY, final int pToX, final int pToY, final boolean pAllowDiagonal, final IAStarHeuristic<T> pAStarHeuristic, final ICostFunction<T> pCostFunction, final float pMaxCost, final IPathFinderListener<T> pPathFinderListener) {
		if(((pFromX == pToX) && (pFromY == pToY)) || pPathFinderMap.isBlocked(pFromX, pFromY, pEntity) || pPathFinderMap.isBlocked(pToX, pToY, pEntity)) {
			return null;
		}
		
		//TODO: Add a sequence that allows the player to move if he is on a blocked object
		
		//drag some fields to local variables
		final Node fromNode = new Node(pFromX, pFromY, pAStarHeuristic.getExpectedRestCost(pPathFinderMap, pEntity, pFromX, pFromY, pToX, pToY));
		
		final long fromNodeID = fromNode.mID;
		final long toNodeID = Node.calculateID(pToX, pToY);
		
		final LongSparseArray<Node> visitedNodes = new LongSparseArray<Node>();
		final LongSparseArray<Node> openNodes = new LongSparseArray<Node>();
		final IQueue<Node> sortedOpenNodes = new UniqueQueue<Node>(new SortedQueue<Node>(new ShiftList<Node>()));
		
		final boolean allowDiagonalMovement = pAllowDiagonal;
		
		//initializes algorithm
		openNodes.put(fromNodeID, fromNode);
		sortedOpenNodes.enter(fromNode);
		
		Node currentNode = null;
		int currentDepth = 0;
		
		//Search depth
		while(currentDepth < pMaxSearchDepth && openNodes.size() > 0) {
			//the first node in the open list is the one with the lowest cost
			currentNode = sortedOpenNodes.poll();
			final long currentNodeID = currentNode.mID;
			if(currentNodeID == toNodeID) {
				break;
			}
			
			visitedNodes.put(currentNodeID, currentNode);
			
			//Loop over all neighbors of this position
			for(int dX = -1; dX <= 1; dX++) {
				for(int dY = -1; dY <= 1; dY++) {
					if((dX == 0) && (dY == 0)) {
						continue;
					}
					
					if(!allowDiagonalMovement && (dX != 0) && (dY != 0)) {
						continue;
					}
					
					final int neighborNodeX = dX + currentNode.mX;
					final int neighborNodeY = dY + currentNode.mY;
					final long neighborNodeID = Node.calculateID(neighborNodeX, neighborNodeY);
					
					if(!IntBoundsUtils.contains(pXMin, pYMin, pXMax, pYMax, neighborNodeX, neighborNodeY) || pPathFinderMap.isBlocked(neighborNodeX, neighborNodeY, pEntity)) {
						continue;
					}
					
					if(visitedNodes.indexOfKey(neighborNodeID) >= 0) {
						continue;
					}
					
					Node neighborNode = openNodes.get(neighborNodeID);
					final boolean neighborNodeIsNew;
					//check if neighbor exists
					if(neighborNode == null) {
						neighborNodeIsNew = true;
						neighborNode = new Node(neighborNodeX, neighborNodeY, pAStarHeuristic.getExpectedRestCost(pPathFinderMap, pEntity, neighborNodeX, neighborNodeY, pToX, pToY));
					} else {
						neighborNodeIsNew = false;
					}
					
					//update cost of neighbor as cost of current plus step from current to neighbor
					final float costFromCurrentToNeighbor = pCostFunction.getCost(pPathFinderMap, currentNode.mX, currentNode.mY, neighborNodeX, neighborNodeY, pEntity);
					final float neighborNodeCost = currentNode.mCost + costFromCurrentToNeighbor;
					if(neighborNodeCost > pMaxCost) {
						//too expensive -> remove if it isn't a new node
						if(!neighborNodeIsNew) {
							openNodes.remove(neighborNodeID);
						}
					} else {
						neighborNode.setParent(currentNode, costFromCurrentToNeighbor);
						if(neighborNodeIsNew) {
							openNodes.put(neighborNodeID, neighborNode);
						} else {
							// remove so that re-insertion puts it to the correct spot.
							sortedOpenNodes.remove(neighborNode);
						}
						
						sortedOpenNodes.enter(neighborNode);
						
						if(pPathFinderListener != null) {
							pPathFinderListener.onVisited(pEntity, neighborNodeX, neighborNodeY);
						}
					}
					currentDepth = Math.max(currentDepth, neighborNode.setParent(currentNode));
				}
			}
		}
		
		//Clean up
		//TODO: we could just let GC do it's work
		visitedNodes.clear();
		openNodes.clear();
		sortedOpenNodes.clear();
		
		//check if a path was found
		if(currentNode.mID != toNodeID) {
			return null;
		}
		
		//calculate path length
		int length = 1;
		Node tmp = currentNode;
		while(tmp.mID != fromNodeID) {
			tmp = tmp.mParent;
			length++;
		}
		
		//trace back path
		final Path path = new Path(length);
		int index = length - 1;
		tmp = currentNode;
		while(tmp.mID != fromNodeID) {
			path.set(index, tmp.mX, tmp.mY);
			tmp = tmp.mParent;
			index--;
		}
		path.set(0, pFromX, pFromY);
		
		return path;
	}
	
	// ==========================================
	// INNER & ANONYMOUS CLASSES
	// ==========================================
	private static final class Node implements Comparable<Node> {
		// ======================================
		// CONSTANTS
		// ======================================
		
		
		// ======================================
		// FIELDS
		// ======================================
		
		//package
		Node mParent;
		
		final int mX;
		final int mY;
		final long mID;
		final float mExpectedRestCost;
		
		float mCost;
		float mTotalCost;
		
		private int mDepth;
		
		// ======================================
		// CONSTRUCTORS
		// ======================================
		public Node(final int pX, final int pY, final float pExpectedRestCost) {
			this.mX = pX;
			this.mY = pY;
			this.mExpectedRestCost = pExpectedRestCost;
			
			this.mID = Node.calculateID(pX, pY);
		}
		
		// ======================================
		// GETTER & SETTER
		// ======================================
		public void setParent(final Node pParent, final float pCost) {
			this.mParent = pParent;
			this.mCost = pCost;
			this.mTotalCost = pCost + this.mExpectedRestCost;
		}
		
		public int setParent(final Node pParent) {
			this.mDepth = pParent.mDepth + 1;
			this.mParent = pParent;
			
			return this.mDepth;
		}
		
		// ======================================
		// METHODS FOR/FROM SUPERCLASS/INTERFACES
		// ======================================
		@Override
		public int compareTo(final Node pNode) {
			final float diff = this.mTotalCost - pNode.mTotalCost;
			if(diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			} else {
				return 0;
			}
		}
		
		@Override
		public boolean equals(final Object pOther) {
			if(this == pOther) {
				return true;
			} else if (pOther == null) {
				return false;
			} else if (this.getClass() != pOther.getClass()) {
				return false;
			}
			return this.equals((Node)pOther);
		}
		
		@Override
		public String toString() {
			return this.getClass().getSimpleName() + " [x=" + this.mX + ", y=" + this.mY + "]";
		}
		
		// ======================================
		// METHODS
		// ======================================
		public static long calculateID(final int pX, final int pY) {
			return (((long)pX) << 32) | pY;
		}
		
		public boolean equals(final Node pNode) {
			return this.mID == pNode.mID;
		}
	}
}
