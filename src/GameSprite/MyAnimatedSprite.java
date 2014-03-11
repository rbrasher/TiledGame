package GameSprite;

import java.util.ArrayList;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.ease.EaseLinear;

import TMXmap.TMXmapLoader;
import Util.UpdateLoops;

import com.ronb.tiledgame.WorldActivity;
import com.ronb.tiledgame.Enums.EDirections;

/**
 * This class extends the animated sprite class. Controls how the main sprite
 * interacts with the map
 * 
 * @author ron
 * 
 */

public class MyAnimatedSprite extends AnimatedSprite {

	protected static final String TAG = "MyAnimatedSprite";

	// ==========================================
	// CONSTANTS
	// ==========================================
	private IUpdateHandler mSpritePositionUpdater;
	private IUpdateHandler mExitPathUpdater;
	private IUpdateHandler mAnimationUpdater;
	private IUpdateHandler mSceneUpdater;
	private UpdateLoops mUpdateLoops;
	private Path mPath;
	private IUpdateHandler mAlterTileUpdater;
	private WorldActivity mContext;

	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public MyAnimatedSprite(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion, final WorldActivity pMain) {
		super(pX, pY, pTiledTextureRegion, pMain.getVertexBufferObjectManager());
		mContext = pMain;
		mUpdateLoops = new UpdateLoops();
	}

	// ==========================================
	// GETTERS & SETTERS
	// ==========================================
	public TMXTile[] getSpritePathTiles() {
		return mUpdateLoops.getSpriteTiles();
	}

	public EDirections getSpriteDirection() {
		return (mUpdateLoops.getSpriteDirection() != null) ? mUpdateLoops
				.getSpriteDirection() : EDirections.NONE;
	}

	public TMXTile getSpritePositionTile(TMXLayer pTMXLayer) {
		TMXTile lSpriteTile = pTMXLayer.getTMXTileAt(this.getX(), this.getY());
		return lSpriteTile;
	}

	public boolean isSpriteCentered() {
		if (this.getX() % WorldActivity.TILE_WIDTH == 0
				&& this.getY() % WorldActivity.TILE_HEIGHT == 0)
			return true;
		else
			return false;
	}

	public boolean isSpriteCentered(float pBuffer, TMXTile pTMXTile) {
		if (this.getX() <= pTMXTile.getTileX() + pBuffer
				&& this.getX() >= pTMXTile.getTileX() - pBuffer
				&& this.getY() <= pTMXTile.getTileY() + pBuffer
				&& this.getY() >= pTMXTile.getTileY() - pBuffer)
			return true;
		else
			return false;
	}
	
	//this is the path the sprite is taking. If null then there is no path
	public void setPath(Path pPath) {
		mPath = pPath;
	}
	
	public Path getPath() {
		return mPath;
	}
	
	//if the next and current tile are equal then the sprite is not moving
	public boolean isSpriteMoving() {
		return (!this.getSpritePathTiles()[0].equals(this.getSpritePathTiles()[1]) ? true : false);
	}
	
	public ArrayList<TMXTile> getAdjacentTiles(TMXmapLoader pTMXmapLoader) {
		ArrayList<TMXTile> lAdjacentTiles = new ArrayList<TMXTile>();

		TMXLayer lTouchLayer = pTMXmapLoader.getTMXMapLayer(0);
		TMXTile lPlayerTile = this.getSpritePathTiles()[0];
		if(lPlayerTile.getTileColumn() + 1 < pTMXmapLoader.getTMXMap().getTileColumns())
			lAdjacentTiles.add(lTouchLayer.getTMXTile(lPlayerTile.getTileColumn() + 1, lPlayerTile.getTileRow()));

		if(lPlayerTile.getTileColumn() - 1 >= 0)
			lAdjacentTiles.add(lTouchLayer.getTMXTile(lPlayerTile.getTileColumn() - 1, lPlayerTile.getTileRow()));

		if(lPlayerTile.getTileRow() + 1 < pTMXmapLoader.getTMXMap().getTileRows())
			lAdjacentTiles.add(lTouchLayer.getTMXTile(lPlayerTile.getTileColumn(), lPlayerTile.getTileRow() + 1));

		if(lPlayerTile.getTileRow() - 1 >= 0)
			lAdjacentTiles.add(lTouchLayer.getTMXTile(lPlayerTile.getTileColumn(), lPlayerTile.getTileRow() - 1));
		return lAdjacentTiles;
	}
	
	
	//=======================================Public Method======================================
	//TODO:These update methods are badly implemented. I feel that using update loops was a bad idea and that
	//it would be better to update these as the sprite traverses on a path. If you use this then do so with caution. 
	
	public void registerTileTracker(final TMXLayer pTMXLayer){
		//unregister the previous update handler.This has to be before the new declaration
		this.unregisterUpdateHandler(mSpritePositionUpdater);
		mSpritePositionUpdater = mUpdateLoops.trackSpriteTiles(this, pTMXLayer);
		this.registerUpdateHandler(mSpritePositionUpdater);
	}
	
	public void registerExitPathUpdater(TMXmapLoader pTMXmapLoader){
		this.unregisterUpdateHandler(mExitPathUpdater);//Removes the previous assignment 
		mExitPathUpdater = new UpdateLoops().exitPath(this, pTMXmapLoader, mContext);
		this.registerUpdateHandler(mExitPathUpdater);
	}

	public void registerAlterTileUpdater(TMXmapLoader pTMXmapLoader){
		this.unregisterUpdateHandler(mAlterTileUpdater);//Removes the previous assignment 
		mAlterTileUpdater = new UpdateLoops().alterTile(this, pTMXmapLoader);
		this.registerUpdateHandler(mAlterTileUpdater);
	}

	/*
	 * Controls the animations of the player sprite based on the direction of travel
	 */
	public void registerAnimationUpdater(){
		this.unregisterUpdateHandler(mAnimationUpdater);//Removes the previous assignment 
		mAnimationUpdater = new UpdateLoops().animatePlayer(this);
		this.registerUpdateHandler(mAnimationUpdater);
	}
		
	public void unregisterExitPathUpdater(){
		this.unregisterUpdateHandler(mExitPathUpdater);
	}
	
	/*
	 * Not used but keep just in case
	 */
	public void unregisterTileTracker(){
		this.unregisterUpdateHandler(mSpritePositionUpdater);
	}
		
	public void stopAnimation(EDirections pDirection){
		switch (pDirection) {
		case RIGHT:
			this.stopAnimation(WorldActivity.RIGHT_CENTER_TILE);
			break;

		case LEFT:
			this.stopAnimation(WorldActivity.LEFT_CENTER_TILE);
			break;
			
		case UP:
			this.stopAnimation(WorldActivity.UP_CENTER_TILE);
			break;
			
		case DOWN:
			this.stopAnimation(WorldActivity.DOWN_CENTER_TILE);
			break;
			
		default:
			this.stopAnimation();
			break;
		}
	}
	
	public void registerSceneUpdater(){
		this.unregisterUpdateHandler(mSceneUpdater);
		mSceneUpdater = new IUpdateHandler(){

			@Override
			public void onUpdate(float arg0) {
				WorldActivity.mWorldCamera.updateChaseEntity();			
			}

			@Override
			public void reset() {
			}
			
		};
		this.registerUpdateHandler(mSceneUpdater);
	}
	
	/*
	 * These paths do not use collisions
	 */
	public void registerPath(final Path pPath, float pDuration, boolean pLoopContinuous, final OnStatusUpdateListener pOnFinishHandler){
		this.setPath(pPath);
		int LoopContinuous = 1;
		if(pLoopContinuous)
			LoopContinuous  = -1;
		this.registerEntityModifier(new LoopEntityModifier(new PathModifier(pDuration, pPath, new IPathModifierListener() {
			@Override
			public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
			}

			@Override
			public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
				EDirections lDirection = EDirections.NONE;
				if(pWaypointIndex + 1 < pPath.getSize()){
					lDirection  = EDirections.getDirectionToNextStepArray(pPath)[pWaypointIndex];
				}
				//Log.i(TAG, " lDirection " + lDirection);
				switch(lDirection) {
					case RIGHT:
						int RightAnimTiles[] = {WorldActivity.RIGHT_CENTER_TILE - 1,
								WorldActivity.RIGHT_CENTER_TILE,
								WorldActivity.RIGHT_CENTER_TILE + 1,
								WorldActivity.RIGHT_CENTER_TILE};
						animate(WorldActivity.WALK_ANIMATE_DURATION, RightAnimTiles, -1);
						break;
					case DOWN:
						int DownAnimTiles[] = {WorldActivity.DOWN_CENTER_TILE - 1,
								WorldActivity.DOWN_CENTER_TILE,
								WorldActivity.DOWN_CENTER_TILE + 1,
								WorldActivity.DOWN_CENTER_TILE};
						animate(WorldActivity.WALK_ANIMATE_DURATION, DownAnimTiles, -1);
						break;
					case LEFT:
						int LeftAnimTiles[] = {WorldActivity.LEFT_CENTER_TILE - 1,
								WorldActivity.LEFT_CENTER_TILE,
								WorldActivity.LEFT_CENTER_TILE + 1,
								WorldActivity.LEFT_CENTER_TILE};
						animate(WorldActivity.WALK_ANIMATE_DURATION, LeftAnimTiles, -1);
						break;
					case UP:
						int UpAnimTiles[] = {WorldActivity.UP_CENTER_TILE - 1,
								WorldActivity.UP_CENTER_TILE,
								WorldActivity.UP_CENTER_TILE + 1,
								WorldActivity.UP_CENTER_TILE};
						animate(WorldActivity.WALK_ANIMATE_DURATION, UpAnimTiles, -1);
						break;
				default:
					break;
						
				}
			}

			@Override
			public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
			}

			@Override
			public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
				setPath(null);				
				pOnFinishHandler.onFinish();
			}
			
		}, EaseLinear.getInstance()), LoopContinuous));	
	}
	
	public void registerPath(final Path pPath, float pDuration){
		registerPath(pPath, pDuration, true, new OnStatusUpdateListener());
	}
	
	public void registerPath(final Path pPath, float pDuration, boolean pLoopContinuous){
		registerPath(pPath, pDuration, pLoopContinuous, new OnStatusUpdateListener());
	}

	public void setDirection(EDirections pSpriteDirection) {
		//Log.i(TAG, "pSpriteDirection "  + pSpriteDirection);
		switch (pSpriteDirection) {
		case UP:
			this.setCurrentTileIndex(WorldActivity.UP_CENTER_TILE);
			break;

		case DOWN:
			this.setCurrentTileIndex(WorldActivity.DOWN_CENTER_TILE);
			break;

		case LEFT:
			this.setCurrentTileIndex(WorldActivity.LEFT_CENTER_TILE);
			break;

		case RIGHT:
			this.setCurrentTileIndex(WorldActivity.RIGHT_CENTER_TILE);
			break;

		default:
			break;
		}
		
	}

}
