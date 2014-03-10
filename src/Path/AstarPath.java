package Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.util.Constants;
import org.andengine.util.algorithm.path.ICostFunction;
import org.andengine.util.algorithm.path.IPathFinderMap;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.ManhattanHeuristic;

import GameSprite.MyAnimatedSprite;
import Scene.SceneManager.SceneManager;
import TMXmap.TMXmapLoader;
import android.util.FloatMath;
import android.util.Log;

import com.ronb.tiledgame.WorldActivity;

/**
 * This moves the character to the selected tile.
 * 
 * TODO: handle exit better
 * 
 * @author ron
 * 
 */
public class AstarPath {
	private static final int MAX_SEARCH_DEPTH = 10;
	// TMX variables
	private TMXTiledMap mTiledMap;
	private TMXmapLoader mTMXmapLoader;
	private TMXTile mFinalPosition;

	// Paths
	private org.andengine.util.algorithm.path.Path AStarPath;
	private MyAstarPathFinder<TMXLayer> mAStarPathFinder;
	private ManhattanHeuristic<TMXLayer> mHeuristic;
	private IPathFinderMap<TMXLayer> mPathFinderMap;
	private ICostFunction<TMXLayer> mCostCallback;

	protected int mWaypointIndex;
	private float TRAVEL_SPEED;
	private boolean mHasFinishedPath;

	// Sprites
	private MyAnimatedSprite mPlayerSprite;

	// Context
	private WorldActivity mContext;

	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public AstarPath(TMXTiledMap pTiledMap, TMXmapLoader pTMXLoader, WorldActivity pMain) {
		mTiledMap = pTiledMap;
		mTMXmapLoader = pTMXLoader;
		mHasFinishedPath = true;
		mWaypointIndex = 0;
		mContext = pMain;
		mPlayerSprite = SceneManager.mWorldScene.getPlayerSprite();
	}

	// ==========================================
	// PUBLIC METHODS
	// ==========================================

	/**
	 * This class initializes the values that are used to find the optimal path.
	 * This cannot be refreshed as it causes a large drop in performance
	 */
	public AstarPath generatePathMap() {
		// create the needed objects for the AStarPathFinder
		mAStarPathFinder = new MyAstarPathFinder<TMXLayer>();

		// no special heuristics needed
		mHeuristic = new ManhattanHeuristic<TMXLayer>();

		// define the block behavior
		mPathFinderMap = new IPathFinderMap<TMXLayer>() {

			private boolean mCollide;

			@Override
			public boolean isBlocked(final int pX, final int pY,
					final TMXLayer pTMXLayer) {
				/**
				 * this is where collisions happen and are detected
				 */
				mCollide = false;
				// NULL check. Used since not all tiles have properties
				if (pTMXLayer.getTMXTile(pX, pY)
						.getTMXTileProperties(mTiledMap) != null) {
					// get the tiles with collision property
					if (pTMXLayer.getTMXTile(pX, pY)
							.getTMXTileProperties(mTiledMap)
							.containsTMXProperty("COLLISION", "true"))
						mCollide = true;
				}

				if (mTMXmapLoader.getCollideTiles().contains(
						pTMXLayer.getTMXTile(pX, pY)))
					mCollide = true;

				return mCollide;
			}

		};

		// define how cost is determined. Cost is not used in this astar path
		mCostCallback = new ICostFunction<TMXLayer>() {

			@Override
			public float getCost(IPathFinderMap<TMXLayer> pPathFinderMap,
					int pFromX, int pFromY, int pToX, int pToY, TMXLayer pEntity) {
				return 0;
			}

		};
		return this;
	}

	/**
	 * This method moves the sprite to the designated position
	 */
	public void walkTo(TMXTile pFinalPosition) {
		if (mHasFinishedPath) {
			mHasFinishedPath = false; // this prevents overlapping paths when
										// the user double clicks. Used to
										// prevent stutter
			// player coordinates
			final float[] lPlayerCoordinates = mPlayerSprite
					.convertLocalToSceneCoordinates(
							mPlayerSprite.getWidth() / 2,
							mPlayerSprite.getHeight() / 2);
			// get the tile the center of the player are currently walking on
			TMXTile lPlayerPosition = SceneManager.mWorldScene.getTouchLayer()
					.getTMXTileAt(lPlayerCoordinates[Constants.VERTEX_INDEX_X],
							lPlayerCoordinates[Constants.VERTEX_INDEX_Y]);
			mFinalPosition = pFinalPosition;

			// sets the A* path from the player location to the touched location
			if (mPathFinderMap.isBlocked(pFinalPosition.getTileColumn(),
					pFinalPosition.getTileRow(),
					SceneManager.mWorldScene.getTouchLayer())) {
				pFinalPosition = getNextTile(lPlayerPosition, pFinalPosition);
			}

			// these are the parameters used to determine the tile column and
			// tile row
			int lFromCol = lPlayerPosition.getTileColumn();
			int lFromRow = lPlayerPosition.getTileRow();

			int lToCol = pFinalPosition.getTileColumn();
			int lToRow = pFinalPosition.getTileRow();
			boolean lAllowDiagonal = false;

			// find the path. This needs to be refreshed
			AStarPath = mAStarPathFinder.findPath(MAX_SEARCH_DEPTH,
					mPathFinderMap, 0, 0, mTiledMap.getTileColumns() - 1,
					mTiledMap.getTileRows() - 1,
					SceneManager.mWorldScene.getTouchLayer(), lFromCol,
					lFromRow, lToCol, lToRow, lAllowDiagonal, mHeuristic,
					mCostCallback);

			Log.i("AstarPath", "AstarPath  " + AStarPath);

			// only loads the path if the AStarPath is not null
			Path lPlayerPath = loadPathFound();
			Log.i("AstarPath", "lPlayerPath  " + lPlayerPath);
			if (lPlayerPath != null)
				moveSprite(lPlayerPath); // moves the sprite along the path
			else
				mHasFinishedPath = true; // if the path is null, the player has
											// not moved. Set the flag to true
											// allows input to affect the sprite
		} else {
			// update parameters
			mFinalPosition = pFinalPosition;
			mWaypointIndex = 0;
		}
	}

	/**
	 * Updates the path
	 */
	public void updatePath(TMXTile pFinalPosition) {
		// player coordinates
		final float[] lPlayerCoordinates = mPlayerSprite
				.convertLocalToSceneCoordinates(mPlayerSprite.getWidth() / 2,
						mPlayerSprite.getHeight() / 2);
		// get the tile the feet of the player are currently walking on
		TMXTile lPlayerPosition = SceneManager.mWorldScene.getTouchLayer()
				.getTMXTileAt(lPlayerCoordinates[Constants.VERTEX_INDEX_X],
						lPlayerCoordinates[Constants.VERTEX_INDEX_Y]);

		// sets the A* path from the player location to the touched location
		if (mPathFinderMap.isBlocked(pFinalPosition.getTileColumn(),
				pFinalPosition.getTileRow(),
				SceneManager.mWorldScene.getTouchLayer())) {
			pFinalPosition = getNextTile(lPlayerPosition, pFinalPosition);
		}

		// determine the tile locations
		int FromCol = lPlayerPosition.getTileColumn();
		int FromRow = lPlayerPosition.getTileRow();
		int ToCol = pFinalPosition.getTileColumn();
		int ToRow = pFinalPosition.getTileRow();
		// find the path. this needs to be refreshed
		AStarPath = mAStarPathFinder.findPath(MAX_SEARCH_DEPTH, mPathFinderMap,
				0, 0, mTiledMap.getTileColumns() - 1,
				mTiledMap.getTileRows() - 1,
				SceneManager.mWorldScene.getTouchLayer(), FromCol, FromRow,
				ToCol, ToRow, false, mHeuristic, mCostCallback);
		
		//loads the path with the astar specifications
		Path lPlayerPath = loadPathFound();
		//moves the sprite along the path
		if(lPlayerPath != null) {
			moveSprite(lPlayerPath);
		} else {
			//if the path is still null after the path manipulation then the path is finished
			mHasFinishedPath = true;
			mWaypointIndex = 0;
			//mPlayerSprite.stopAnimation();
			//AStarPath = null;
		}
	}
	
	// Load Path
	private Path loadPathFound() {
		if(AStarPath != null) {
			Path lCurrentPath = new Path(AStarPath.getLength());
			for(int i = 0; i < AStarPath.getLength(); i++) {
				lCurrentPath.to(AStarPath.getX(i) * WorldActivity.TILE_WIDTH, AStarPath.getY(i) * WorldActivity.TILE_HEIGHT);
			}
			return lCurrentPath;
		}
		return null;
	}
	
	// ==========================================
	// PRIVATE METHODS
	// ==========================================
	private boolean moveSprite(final Path pPath){
		mPlayerSprite.setPath(pPath);
		//Creates a shorter path to follow
		// create a new path with length 2 from current sprite position to next
		Path lShortPath = new Path(2);
		final Path lPath = pPath.deepCopy();
		//A path from the players location to the next tile in the sequence
		lShortPath.to(mPlayerSprite.getX(), mPlayerSprite.getY()).to(lPath.getCoordinatesX()[mWaypointIndex + 1],
				lPath.getCoordinatesY()[mWaypointIndex + 1]);
		TRAVEL_SPEED = WorldActivity.TOUCH_MOVEMENT_WALKING_SPEED;    
        // Register a new modifier to move the player sprite
		PathModifier lMoveModifier = new PathModifier(TRAVEL_SPEED, lShortPath , new PathModifier.IPathModifierListener(){           	

			@Override
			public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
			}

			@Override
			public void onPathWaypointStarted(
					PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {			

				// Keep the waypointIndex in a Global Var
				mWaypointIndex = pWaypointIndex;
			}

			@Override
			public void onPathWaypointFinished(	PathModifier pPathModifier, IEntity pEntity, int pWaypointIndex) {

			}

			@Override
			public void onPathFinished(PathModifier pPathModifier,IEntity pEntity) {
				//If the player sprite is not of the final tile of the path then refresh the astar path and do the 
				if(pPath.getCoordinatesX()[pPath.getSize() - 1] != mPlayerSprite.getX() || pPath.getCoordinatesY()[pPath.getSize() - 1] != mPlayerSprite.getY()){
					mHasFinishedPath = false;
					updatePath(mFinalPosition);
				}else{
					mHasFinishedPath = true;
					mWaypointIndex = 0;
					//mPlayerSprite.stopAnimation();
					AStarPath = null;
					mPlayerSprite.setPath(null);
				}
				
			}
        });
		
		lMoveModifier.setAutoUnregisterWhenFinished(true);
		mPlayerSprite.registerEntityModifier(lMoveModifier);
		return mHasFinishedPath;
    }

	/*
	 * Finds the next best tile if the final tile was blocked
	 */
	private TMXTile getNextTile(TMXTile pPlayerPosition, TMXTile pFinalPosition){
		List<TMXTile> playerAltTiles = new ArrayList<TMXTile>();
		List<TMXTile> removeAltTiles = new ArrayList<TMXTile>();//This is necessary to avoid concurrent errors
		List<Float> distanceAltTiles = new ArrayList<Float>();
		TMXTile playerAltTile = null;
		final int MAX_DISTANCE = 1;//This is the range it will search for the new tile
		
		//if the tile was blocked then get the next best one
		//finds the next walkable tile if blocked
		OUTERMOST: for (int i = 1; i <= MAX_DISTANCE; i++) {
			TMXLayer TMXMapLayer = SceneManager.mWorldScene.getTouchLayer();
			//Create a buffer on your map the same thickness as max distance, or reduce max distance
			//LEFT
			if(pFinalPosition.getTileColumn() - i >= 0)
				playerAltTiles.add(TMXMapLayer.getTMXTile(pFinalPosition.getTileColumn() - i, pFinalPosition.getTileRow()));
			//UP
			if(pFinalPosition.getTileRow() - i >= 0)
				playerAltTiles.add(TMXMapLayer.getTMXTile(pFinalPosition.getTileColumn(), pFinalPosition.getTileRow() - i));
			//RIGHT
			if(pFinalPosition.getTileColumn() + i <= SceneManager.mWorldScene.getTouchLayer().getTileColumns())
				playerAltTiles.add(TMXMapLayer.getTMXTile(pFinalPosition.getTileColumn() + i, pFinalPosition.getTileRow()));
			//DOWN
			if(pFinalPosition.getTileRow() + i <= SceneManager.mWorldScene.getTouchLayer().getTileRows())
				playerAltTiles.add(TMXMapLayer.getTMXTile(pFinalPosition.getTileColumn(), pFinalPosition.getTileRow() + i));

			for (TMXTile tmxTile : playerAltTiles) {
				//If tile is over a blocked tile or out of bounds then remove
				if (mPathFinderMap.isBlocked(tmxTile.getTileColumn(), tmxTile.getTileRow(), SceneManager.mWorldScene.getTouchLayer()) 
						|| tmxTile.getTileX() >= TMXMapLayer.getWidth()
						|| tmxTile.getTileY() >= TMXMapLayer.getHeight() 
						|| tmxTile.getTileX() < 0
						|| tmxTile.getTileY() < 0 ) {	
					removeAltTiles.add(tmxTile);
				}									
			}
			//If any of the above tiles went outside of the blocked tile then stop looping
			if(playerAltTiles.size() >= 1){
				break OUTERMOST;
			}
		}

		//Remove all offending tiles
		playerAltTiles.removeAll(removeAltTiles);
		
		for (TMXTile tmxTile2 : playerAltTiles) {
			//This is the distance formula for each tile from the player to the alternate tile
			distanceAltTiles.add(FloatMath.sqrt((float)((Math.pow(tmxTile2.getTileX() - pPlayerPosition.getTileX() , 2) 
					+ Math.pow(tmxTile2.getTileY() - pPlayerPosition.getTileY() , 2)))));						
		}

		//Gets the index of the smallest distance
		int tempIndex = distanceAltTiles.indexOf(Collections.min(distanceAltTiles));

		//The tile that was outside is the tile we move to
		playerAltTile = playerAltTiles.get(tempIndex);
		
		return playerAltTile;
	}
}
