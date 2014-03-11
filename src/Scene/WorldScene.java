package Scene;

import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;

import GameSprite.MyAnimatedSprite;
import Path.AstarPath;
import Scene.SceneManager.SceneManager;
import TMXmap.TMXScene;
import TMXmap.TMXmapLoader;
import Util.AssetLoaderUtil;
import android.opengl.GLES20;

import com.ronb.tiledgame.WorldActivity;
import com.ronb.tiledgame.Enums.EMap_ID;

public class WorldScene extends MyScene {

	public static final String TAG = "WorldScene";
	private WorldActivity mContext;
	private MyAnimatedSprite mPlayerSprite;
	public AstarPath mPlayerAstarPath;
	public TMXLayer mTMXMapTouchLayer;
	private TMXTiledMap mTMXTiledMap;
	private TMXmapLoader mTMXMapLoader;
	protected boolean mAllowTouch;
	private TMXTile mTouchTile;
	private DigitalOnScreenControl mDigitalOnScreenControl;
	
	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public WorldScene(WorldActivity pMain) {
		super(WorldActivity.mWorldCamera, pMain);
		mContext = pMain;
	}
	
	// ==========================================
	// SUPER METHODS
	// ==========================================
	
	@Override
	public MyScene create() {
		//create the sprite and sets the position to the saved location
		WorldActivity.mWorldCamera.setCenter(0, 0);
		mPlayerSprite = new MyAnimatedSprite(0, 0, AssetLoaderUtil.mPlayerTextureRegion, mContext);
		mPlayerSprite.setZIndex(10);
		
		//gets the map index from the file and loads it
		EMap_ID lInitialMapIndex = EMap_ID.DESERT1;
		new TMXScene(lInitialMapIndex, mContext, new OnStatusUpdateListener() {
			
			@Override
			public void onFinish() {
				WorldScene.this.setChildrenVisible(true);
				//this enables the touch screen
				if(WorldActivity.TOUCH_ENABLED)
					WorldScene.this.setOnSceneTouchListener(mContext);
				else
					//attach the UI Button
					attachUI();
			}
		});
		return super.create();
	}
	
	@Override
	public MyScene delete() {
		this.detachChildren();
		return super.delete();
	}
	
	@Override
	public void inFocus() {
		
	}
	
	// ==========================================
	// ACCESSORS
	// ==========================================
	
	/**
	 * Used in onSceneTouchEvent
	 */
	public void setAstarPath(AstarPath pPath) {
		mPlayerAstarPath = pPath;
	}
	
	public void setTouchLayer(TMXLayer pTouchLayer) {
		mTMXMapTouchLayer = pTouchLayer;
	}
	
	public void setTMXTiledMap(TMXTiledMap pTMXTiledMap) {
		mTMXTiledMap = pTMXTiledMap;
	}
	
	public void setTMXMapLoader(TMXmapLoader pTMXMapLoader) {
		mTMXMapLoader = pTMXMapLoader;
	}
	
	public void setChildrenVisible(boolean pVisible) {
		for(int i = 0; i < this.getChildCount(); i++) {
			this.getChildByIndex(i).setVisible(pVisible);
		}
		this.sortChildren();
	}
	
	public void setTouchTile(TMXTile lTargetTile) {
		mTouchTile = lTargetTile;
	}
	
	public TMXTile getTouchTile() {
		return mTouchTile;
	}
	
	public TMXLayer getTouchLayer() {
		return mTMXMapTouchLayer;
	}
	
	public MyAnimatedSprite getPlayerSprite() {
		return mPlayerSprite;
	}
	
	public TMXTiledMap getTMXTiledMap() {
		return mTMXTiledMap;
	}
	
	public TMXmapLoader getTMXMapLoader() {
		return mTMXMapLoader;
	}
	
	// ==========================================
	// PUBLIC METHODS
	// ==========================================
	
	/**
	 * Attaches the DPad to the scene
	 */
	public void attachUI() {
		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0, this.mCamera.getHeight() - AssetLoaderUtil.mDPADBackingTextureRegion.getHeight(), this.mCamera, AssetLoaderUtil.mDPADTextureRegion, 0.1f, mContext.getVertexBufferObjectManager(), new IOnScreenControlListener() {

			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				if(pValueX + pValueY != 0) {
					final float moveToXTile = mPlayerSprite.getX() + WorldActivity.TILE_WIDTH * pValueX;
					final float moveToYTile = mPlayerSprite.getY() + WorldActivity.TILE_HEIGHT * pValueY;
					
					TMXmapLoader pTMXmapLoader = SceneManager.mWorldScene.getTMXMapLoader();
					//sets the Dpad limits to within the tiled map
					if(moveToXTile < pTMXmapLoader.getTMXMapLayer(0).getWidth() && moveToYTile < pTMXmapLoader.getTMXMapLayer(0).getHeight() && moveToXTile >= 0 && moveToYTile >= 0) {
						//only register a direction if the path is null
						if(mPlayerSprite.getPath() == null) {
							final TMXTile lChosenTile = pTMXmapLoader.getTMXMapLayer(0).getTMXTileAt(moveToXTile, moveToYTile);
							SceneManager.mWorldScene.mPlayerAstarPath.walkTo(lChosenTile);
						} //end if path is null
					} // end if
				}
			} //end onControlChange
			
		});
		
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();
		
		this.setChildScene(this.mDigitalOnScreenControl);
	}
	
	public void disableUI() {
		mDigitalOnScreenControl.setIgnoreUpdate(true);
	}
	
	public void enableUI() {
		mDigitalOnScreenControl.setIgnoreUpdate(false);
	}
}
