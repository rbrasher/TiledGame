package Util;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.util.color.Color;

import GameSprite.MyAnimatedSprite;
import Scene.SceneManager.SceneManager;
import Sequences.TMXMapSwitchSequence;
import TMXmap.TMXmapLoader;
import android.util.Log;

import com.ronb.tiledgame.WorldActivity;
import com.ronb.tiledgame.Enums.EDirections;

/**
 * This class controls all the IUpdateHandlers that are used in the game.
 * 
 * @author ron
 *
 */
public class UpdateLoops {

	// ==========================================
	// CONSTANTS
	// ==========================================
	protected static final String TAG = "UpdateLoops";
	
	//TODO: I don't like this class at all. I think the better method would be to update the relevant sections as the
	//player is walking on a path. So on path waypoint finish or start these could be updated
	
	
	protected TMXTile[] mSpriteTiles = new TMXTile[3];
	protected EDirections mSpriteDirection;
	
	/**
	 * Get sprite tiles. This is not as accurate as I would have liked
	 */
	public IUpdateHandler trackSpriteTiles(final MyAnimatedSprite pSprite, final TMXLayer pTMXLayer) {
		//used for debugging
		/*
		final Rectangle currentTileRectangle = new Rectangle(0, 0, WorldActivity.TILE_WIDTH, WorldActivity.TILE_HEIGHT, WorldActivity.mActivityContext.getVertexBufferObjectManager());
		currentTileRectangle.setColor(1, 0, 0, 0.25f);
		WorldActivity.mWorldScene.attachChild(currentTileRectangle);
		final Rectangle currentTileRectangle2 = new Rectangle(0, 0, WorldActivity.TILE_WIDTH, WorldActivity.TILE_HEIGHT, WorldActivity.mActivityContext.getVertexBufferObjectManager());
		currentTileRectangle2.setColor(1, 0, 0, 0.25f);
		WorldActivity.mWorldScene.attachChild(currentTileRectangle2);
		currentTileRectangle.setZIndex(5);
		currentTileRectangle2.setZIndex(5);
		*/
		
		IUpdateHandler lTileTrackerUpdater = new IUpdateHandler() {
			private float[] mSpriteLastPosition = {0, 0};

			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				float lX = pSprite.getX();
				float lY = pSprite.getY();
				final float lLastX = mSpriteLastPosition[WorldActivity.X_COORDINATE];
				final float lLastY = mSpriteLastPosition[WorldActivity.Y_COORDINATE];
				final float lTileShiftX = WorldActivity.TILE_WIDTH - 0.5f;
				final float lTileShiftY = WorldActivity.TILE_HEIGHT - 0.5f;
				//get the players current and next tile
				TMXTile lSpriteTile = pTMXLayer.getTMXTileAt(lX, lY);
				TMXTile lSpriteNextTile = lSpriteTile;
				if(Float.compare(lY, lLastY) == 0 && Float.compare(lX, lLastX) > 0) {
					//this shifts the sprite coordinate location. This is used to prevent the tracked tile from skipping to the next set of tiles when
					//moving down or to the right
					//If going right. The current tile is the tile the character is leaving and the next tile is the tile the sprite will land on
					lSpriteNextTile = pTMXLayer.getTMXTileAt(lX + WorldActivity.TILE_WIDTH, lY);
					lSpriteTile = pTMXLayer.getTMXTileAt(lX, lY);
					mSpriteDirection = EDirections.RIGHT;
					Log.d(TAG, "RIGHT");
				} else if (Float.compare(lY, lLastY) < 0 && Float.compare(lX, lLastX) < 0) {
					//if going left
					lX = pSprite.getX() + lTileShiftX;
					lSpriteNextTile = pTMXLayer.getTMXTileAt(lX - WorldActivity.TILE_WIDTH, lY);
					lSpriteTile = pTMXLayer.getTMXTileAt(lX, lY);
					mSpriteDirection = EDirections.LEFT;
					Log.d(TAG, "LEFT");
				} else if(Float.compare(lY, lLastY) > 0 && Float.compare(lX, lLastX) == 0) {
					//if going up
					lY = pSprite.getY() + lTileShiftY;
					lSpriteNextTile = pTMXLayer.getTMXTileAt(lX, lY - WorldActivity.TILE_HEIGHT);
					lSpriteTile = pTMXLayer.getTMXTileAt(lX, lY);
					mSpriteDirection = EDirections.UP;
					Log.d(TAG, "UP");
				} else if(Float.compare(lY, lLastY) > 0 && Float.compare(lX, lLastX) == 0) {
					//if going down
					lSpriteNextTile = pTMXLayer.getTMXTileAt(lX, lY + WorldActivity.TILE_HEIGHT);
					lSpriteTile = pTMXLayer.getTMXTileAt(lX, lY);
					mSpriteDirection = EDirections.DOWN;
					Log.d(TAG, "DOWN");
				} else if(Float.compare(lY, lLastY) == 0 && Float.compare(lX, lLastX) == 0) {
					mSpriteDirection = EDirections.NONE;
					Log.d(TAG, "NONE");
				}
				
				mSpriteTiles[0] = lSpriteTile;
				mSpriteTiles[1] = lSpriteNextTile;
				mSpriteTiles[2] = pTMXLayer.getTMXTileAt(pSprite.getX() + WorldActivity.TILE_WIDTH / 2, pSprite.getY() + WorldActivity.TILE_HEIGHT / 2);
				
				//used for debugging
				//currentTileRectangle.setPosition(lSpriteTile.getTileX(), lSpriteTile.getTileY());
				//currentTileRectangle2.setPosition(lSpriteNextTile.getTileX(), lSpriteNextTile.getTileY());
				
				//update the last position
				mSpriteLastPosition[0] = pSprite.getX();
				mSpriteLastPosition[1] = pSprite.getY();
			}

		};
		return lTileTrackerUpdater;
	}
	
	/**
	 * Get the tiles the sprite is walking on. Use index 0 for the tile the sprite is currently on.
	 * Use index 1 for the next tile the sprite will be on
	 * Use index 2 for the tile at the sprite's center coordinates
	 * 
	 * @return
	 */
	public TMXTile[] getSpriteTiles() {
		return mSpriteTiles;
	}
	
	/**
	 * Get the direction the sprite is facing
	 * 
	 * @return
	 */
	public EDirections getSpriteDirection() {
		return mSpriteDirection;
	}
	
	/**
	 * This is used to animate the player sprite.
	 * 
	 * @param pAnimatedSprite
	 * @return
	 */
	public IUpdateHandler animatePlayer(final MyAnimatedSprite pAnimatedSprite) {
		
		IUpdateHandler lAnimatePlayerUpdater = new IUpdateHandler() {
			
			private int ContinuousLoop = -1;
			private boolean mChangedDirection = true;
			private EDirections mLastDirection;
			
			@Override
			public void reset() {
				
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(pAnimatedSprite.getSpriteDirection() != mLastDirection)
					mChangedDirection = true;
				if(pAnimatedSprite.getSpriteDirection().equals(EDirections.DOWN) && mChangedDirection) {
					mChangedDirection = false;
					int DownAnimTiles[] = {WorldActivity.DOWN_CENTER_TILE - 1, WorldActivity.DOWN_CENTER_TILE, WorldActivity.DOWN_CENTER_TILE + 1, WorldActivity.DOWN_CENTER_TILE};
					pAnimatedSprite.animate(WorldActivity.WALK_ANIMATE_DURATION, DownAnimTiles, ContinuousLoop);
				} else if(pAnimatedSprite.getSpriteDirection().equals(EDirections.RIGHT) && mChangedDirection) {
					mChangedDirection = false;
					int RightAnimTiles[] = {WorldActivity.RIGHT_CENTER_TILE - 1, WorldActivity.RIGHT_CENTER_TILE, WorldActivity.RIGHT_CENTER_TILE + 1, WorldActivity.RIGHT_CENTER_TILE};
					pAnimatedSprite.animate(WorldActivity.WALK_ANIMATE_DURATION, RightAnimTiles, ContinuousLoop);
				} else if(pAnimatedSprite.getSpriteDirection().equals(EDirections.UP) && mChangedDirection) {
					mChangedDirection = false;
					int UpAnimTiles[] = {WorldActivity.UP_CENTER_TILE - 1, WorldActivity.UP_CENTER_TILE, WorldActivity.UP_CENTER_TILE + 1, WorldActivity.UP_CENTER_TILE};
					pAnimatedSprite.animate(WorldActivity.WALK_ANIMATE_DURATION, UpAnimTiles, ContinuousLoop);
				} else if(pAnimatedSprite.getSpriteDirection().equals(EDirections.LEFT) && mChangedDirection) {
					mChangedDirection = false;
					int LeftAnimTiles[] = {WorldActivity.LEFT_CENTER_TILE - 1, WorldActivity.LEFT_CENTER_TILE, WorldActivity.LEFT_CENTER_TILE + 1, WorldActivity.LEFT_CENTER_TILE};
					pAnimatedSprite.animate(WorldActivity.WALK_ANIMATE_DURATION, LeftAnimTiles, ContinuousLoop);
				} else if(pAnimatedSprite.getSpriteDirection().equals(EDirections.NONE)) {
					//end on the proper tile
					pAnimatedSprite.stopAnimation(EDirections.getSpriteDirection(pAnimatedSprite));
				}
				
				mLastDirection = pAnimatedSprite.getSpriteDirection();
			}

		};
		
		return lAnimatePlayerUpdater;
	}
	
	private ArrayList<TMXTile> mExitTiles;
	
	/**
	 * This shortens the player path if there is an exit in the path
	 */
	public IUpdateHandler exitPath(final MyAnimatedSprite pAnimatedSprite, final TMXmapLoader pTMXmapLoader, final WorldActivity pMain) {
		mExitTiles = pTMXmapLoader.getExitTiles();
		
		IUpdateHandler lExitPathUpdater = new IUpdateHandler() {
			private boolean mOneShot = false;
			
			@Override
			public void reset() {
				mOneShot = false;
			}

			@Override
			public void onUpdate(float pSecondsElapsed) {
				TMXTile[] lSpritePathTiles = pAnimatedSprite.getSpritePathTiles();
				TMXTile lTile = lSpritePathTiles[0];
				TMXTile lNextTile = lSpritePathTiles[1];
				if(lNextTile != null && lTile != null && pAnimatedSprite.getPath() != null) {
					ArrayList<TMXTile> lPlayerPathTiles = PathUtil.getPathTiles(pAnimatedSprite.getPath(), pTMXmapLoader.getTMXMapLayer(0));
					//if the players next tile is the same as the exit tile then start the map switch sequence. The boolean flag
					//is used to prevent repeatedly running the switching sequence
					for(int i = 0; i < mExitTiles.size(); i++) {
						if(mExitTiles.get(i).equals(lNextTile) && lPlayerPathTiles.contains(mExitTiles.get(i)) && !lTile.equals(lNextTile) && !mOneShot) {
							mOneShot = true;
							//disables the touch controls
							SceneManager.mWorldScene.setOnSceneTouchListener(null);
							pAnimatedSprite.clearEntityModifiers();
							new TMXMapSwitchSequence(pTMXmapLoader, pTMXmapLoader.getTMXMap(), mExitTiles.get(i), pMain);
						}
					}
				}
			}
		};
		return lExitPathUpdater;
	}
	
	/**
	 * This will check to see if the tile the player is on can be changed
	 * 
	 * @param pSprite
	 * @param pTMXmapLoader
	 * @return
	 */
	public IUpdateHandler alterTile(final MyAnimatedSprite pSprite, final TMXmapLoader pTMXmapLoader) {
		
		IUpdateHandler lChangeTileUpdater = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				TMXTile lSpriteTile = pSprite.getSpritePathTiles()[2];    //this uses the center of the sprite to get the tile
				EDirections lSpriteDirection = pSprite.getSpriteDirection();
				ArrayList<TMXTile> lChangeTiles = new ArrayList<TMXTile>(pTMXmapLoader.getChangingTileArray());
				
				//if neither sprite tile is on the rass then...
				if(lChangeTiles.contains(lSpriteTile)) {
					float ALTER_TILE_BUFFER = 3;    //this means that the player sprite can be three pixels off center for the effect to trigger
					if(!lSpriteDirection.equals(EDirections.DOWN)) {
						//Alter the grass the player walks on. You can edit this to change the walked on tile to anything you want.
						if(pSprite.isSpriteCentered(ALTER_TILE_BUFFER, lSpriteTile))
							alterTile(pTMXmapLoader, lSpriteTile, lSpriteTile.getGlobalTileID() + 1);
						else 
							alterTile(pTMXmapLoader, lSpriteTile, lSpriteTile.getGlobalTileID() - 1);
					}
				}
			}

			@Override
			public void reset() {
				
			}
			
		};
		return lChangeTileUpdater;
	}
	
	private void alterTile(TMXmapLoader pTMXmapLoader, TMXTile pTile, int pID) {
		TMXLayer lChangeLayer = pTMXmapLoader.getTMXMapLayer(0);    //this can be changed to change the tile on any layer
		
		//gets the next ID
		int lNextTileID = pID;
		
		TMXTile lChangeTile = lChangeLayer.getTMXTile(pTile.getTileColumn(), pTile.getTileRow());
		lChangeTile.setTextureRegion(pTMXmapLoader.getTMXMap().getTextureRegionFromGlobalTileID(lNextTileID));
		final int lTileHeight = pTMXmapLoader.getTMXMap().getTileHeight();
		final int lTileWidth = pTMXmapLoader.getTMXMap().getTileWidth();
		//See TMXLayer class line 308 (getSpriteBatchIndex)
		lChangeLayer.setIndex(lChangeTile.getTileRow() * pTMXmapLoader.getTMXMap().getTileColumns() + lChangeTile.getTileColumn());
		lChangeLayer.drawWithoutChecks(lChangeTile.getTextureRegion(), lChangeTile.getTileX(), lChangeTile.getTileY(), lTileWidth, lTileHeight, Color.WHITE_ABGR_PACKED_FLOAT);
		//this alerts the tile texture
		lChangeLayer.submit();
	}
	
}
