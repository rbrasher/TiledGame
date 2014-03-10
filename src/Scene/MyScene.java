package Scene;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;

import com.ronb.tiledgame.WorldActivity;

public class MyScene extends Scene {

	public static final String TAG = "MyScene";
	private ArrayList<ITouchArea> mSavedTouchAreas;
	private boolean mDisableTouch;
	protected Camera mCamera;
	private WorldActivity mContext;
	
	/**
	 * Instantiates a new my camera scene. This constructor allows a fade effect over the scene. It
	 * also allows the scene to be tracked via localytics
	 * 
	 * @param pCamera - the camera
	 * @param pMain
	 */
	public MyScene(final Camera pCamera, final WorldActivity pMain) {
		super();
		mDisableTouch = false;
		mCamera = pCamera;
		mContext = pMain;
	}
	
	/**
	 * Instantiates a new my camera scene
	 * 
	 * @param pCamera
	 */
	public MyScene(final Camera pCamera) {
		super();
		mDisableTouch = false;
		mCamera = pCamera;
	}
	
	/**
	 * This will be called when this scene is set in the engine
	 */
	public void inFocus() {
		
	}
	
	/**
	 * This was added so that the touch areas in the scene could be saved and retrieved
	 */
	public void saveTouchAreas() {
		mSavedTouchAreas = new ArrayList<ITouchArea>();
		final int arrayLength = mTouchAreas.size();
		for(int i = 0; i < arrayLength; i++) {
			mSavedTouchAreas.add(mTouchAreas.get(i));
		}
	}
	
	/**
	 * Restores the touch areas saved from {@link MyCameraScene#saveTouchAreas()}
	 */
	public void registerSavedTouchAreas() {
		final int arrayLength = mSavedTouchAreas.size();
		for(int i = 0; i < arrayLength; i++) {
			this.registerTouchArea(mSavedTouchAreas.get(i));
		}
	}
	
	public int getSceneCount() {
		MyScene lScene = this;
		int lSceneCount = 0;
		while(lScene.hasChildScene()) {
			lScene = (MyScene) lScene.getChildScene();
			lSceneCount++;
		}
		return lSceneCount;
	}
	
	/**
	 * Loops until the selected scene has no child scene.
	 * @return
	 */
	public MyScene getTopScene() {
		MyScene lScene = this;
		int lSceneCount = 0;
		while(lScene.hasChildScene()) {
			if(lSceneCount > 50)
				throw new Error(lSceneCount + " scenes seems like too much. You may have an error somewhere!"); //break;
			lScene = (MyScene) lScene.getChildScene();
			lSceneCount++;
		}
		return lScene;
	}
	
	/**
	 * This gets the camera associated with this scene
	 * @return
	 */
	public Camera getCamera() {
		return mCamera;
	}
	
	public void disableTouchOnScene() {
		if(!mDisableTouch) {
			mDisableTouch = true;
			MyCameraScene lNOTouchScene = new MyCameraScene(WorldActivity.mWorldCamera);
			lNOTouchScene.setBackgroundEnabled(false);
			this.getTopScene().setChildScene(lNOTouchScene, false, false, true);
		}
	}
	
	public void disableTouchOnScene(final OnStatusUpdateListener pOnStatusUpdateListener) {
		this.disableTouchOnScene();
		pOnStatusUpdateListener.onFinish();
	}
	
	public void enableTouchOnScene() {
		if(mDisableTouch) {
			mDisableTouch = false;
			this.getTopScene().back();
		}
	}
	
	/**
	 * This needs to be run in an update thread or a touch event
	 */
	public void clearScene() {
		//detaches children from three layers. This should be done recursively.
		for(int i = 0; i < this.getChildCount(); i++) {
			IEntity Child = this.getChildByIndex(i);
			for(int j = 0; j < Child.getChildCount(); j++) {
				IEntity Child2 = this.getChildByIndex(j);
				for(int j2 = 0; j2 < Child2.getChildCount(); j2++) {
					if(!Child2.getChildByIndex(j2).isDisposed())
						Child2.getChildByIndex(j2).dispose();
					Child2.getChildByIndex(j2).detachChildren();
					Child2.getChildByIndex(j2).clearEntityModifiers();
					Child2.getChildByIndex(j2).clearUpdateHandlers();
				}
				if(!Child2.isDisposed())
					Child2.dispose();
				Child2.detachChildren();
				Child2.clearEntityModifiers();
				Child2.clearUpdateHandlers();
			}
			if(!Child.isDisposed())
				Child.dispose();
			Child.detachChildren();
			Child.clearEntityModifiers();
			Child.clearUpdateHandlers();
		}
		if(!this.isDisposed())
			this.dispose();
		this.detachChildren();
		this.clearEntityModifiers();
		this.clearUpdateHandlers();
	}
	
	/**
	 * This creates the base of the scene
	 */
	public MyScene create() {
		this.setTouchAreaBindingOnActionDownEnabled(true);
		this.setTouchAreaBindingOnActionMoveEnabled(true);
		this.setOnAreaTouchTraversalFrontToBack();
		return this;
	}
	
	/**
	 * This deletes the base of the scene. This should remove everything that was created in the create scene
	 */
	public MyScene delete() {
		this.clearChildScene();
		this.detachChildren();
		this.clearEntityModifiers();
		this.clearTouchAreas();
		this.clearUpdateHandlers();
		return this;
	}
	
	/**
	 * This is used in lieu of deleting a scene. This will update all the sprites and variables that have changed since last call.
	 * This will not be applicable to all scenes.
	 * @return
	 */
	public MyScene update() {
		this.clearChildScene();
		this.detachChildren();
		this.clearEntityModifiers();
		this.clearTouchAreas();
		this.clearUpdateHandlers();
		return this;
	}
	
	/**
	 * Recycle pool items
	 */
	public void recycleScene() {
		
	}
}
