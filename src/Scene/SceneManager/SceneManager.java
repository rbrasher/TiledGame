package Scene.SceneManager;

import org.andengine.engine.Engine;
import org.andengine.engine.Engine.EngineLock;
import org.andengine.ui.activity.BaseGameActivity;

import Scene.GameIntroScene;
import Scene.LoadingScene;
import Scene.MainMenuScene;
import Scene.MyScene;
import Scene.WorldScene;
import Scene.SceneManager.Transitions.AbstractTransition;
import Scene.SceneManager.Transitions.ITransitionListener;
import android.util.DisplayMetrics;

import com.ronb.tiledgame.WorldActivity;

/**
 * This class creates, gets and sets all scenes
 * 
 * @author ron
 *
 */
public class SceneManager {

	protected static final String TAG = "SceneManager";
	
	//scenes
	public static GameIntroScene mGameIntroScene;
	public static MainMenuScene mMainMenuScene;
	public static LoadingScene mLoadingScene;
	public static WorldScene mWorldScene;
	public static MyScene[] mAllScenes;
	
	private static SceneManager INSTANCE = new SceneManager();
	
	public static void initialize(WorldActivity pMain) {
		INSTANCE.mDisplayMetrics = new DisplayMetrics();
		INSTANCE.mEngine = pMain.getEngine();
		INSTANCE.mActivity = pMain;
		INSTANCE.mActivity.getWindowManager().getDefaultDisplay().getMetrics(INSTANCE.mDisplayMetrics);
		
		//creates the Scenes that will not be destroyed for the lifetime of the application
		mLoadingScene = new LoadingScene(WorldActivity.mWorldCamera, pMain);
		mGameIntroScene = new GameIntroScene(pMain);
		mMainMenuScene = new MainMenuScene(pMain);
		mWorldScene = new WorldScene(pMain);
		
		MyScene[] allScenes = {mLoadingScene, mGameIntroScene, mMainMenuScene};
		mAllScenes = allScenes;
	}
	
	public static SceneManager getInstance() {
		return INSTANCE;
	}
	
	private BaseGameActivity mActivity;
	private DisplayMetrics mDisplayMetrics;
	private Engine mEngine;
	private MyScene mTransitionScene = new MyScene(null);
	private MyScene mLastScene;
	
	public float getDisplayHeight() {
		return mDisplayMetrics.heightPixels * mDisplayMetrics.scaledDensity;
	}
	
	public float getDisplayWidth() {
		return mDisplayMetrics.widthPixels * mDisplayMetrics.scaledDensity;
	}
	
	public float getSurfaceHeight() {
		return mEngine.getCamera().getHeight();
	}
	
	public float getSurfaceWidth() {
		return mEngine.getCamera().getWidth();
	}
	
	public MyScene getCurrentScene() {
		return (MyScene) mEngine.getScene();
	}
	
	public MyScene getTransitionScene() {
		return mTransitionScene;
	}
	
	public MyScene getLastScene() {
		return mLastScene;
	}
	
	public void setScene(final MyScene pScene) {
		mEngine.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				final EngineLock engineLock = mEngine.getEngineLock();
				engineLock.lock();
				mLastScene = SceneManager.this.getCurrentScene();	//set the last scene
				mEngine.setScene(pScene);
				engineLock.unlock();
			}
			
		});
	}
	
	public void setScene(MyScene pInScene, AbstractTransition pTransition) {
		final MyScene oldScene = this.getCurrentScene();
		//make sure both scenes are not updating
		oldScene.setIgnoreUpdate(true);
		pInScene.setIgnoreUpdate(true);
		
		this.setScene(mTransitionScene);
		mTransitionScene.attachChild(oldScene);
		mTransitionScene.attachChild(pInScene);
		
		pTransition.execute(this.getCurrentScene(), pInScene, new ITransitionListener() {
			
			@Override
			public void onTransitionFinished(final MyScene pOutScene, final MyScene pInScene, final AbstractTransition pTransition) {
				mEngine.runOnUpdateThread(new Runnable() {

					@Override
					public void run() {
						final EngineLock engineLock = mEngine.getEngineLock();
						engineLock.lock();
						mTransitionScene.detachChildren(); 			//remove the attached scenes
						engineLock.unlock();
					}
					
				});
				INSTANCE.setScene(pInScene);
				pInScene.inFocus();    //this does a procedure when the scene is coming into focus
			}
			
			@Override
			public void onTransitionProgress(MyScene pOutScene, final MyScene pInScene, AbstractTransition pTransition) {
				//change the camera to the camera the scene needs
				if(pInScene.getCamera() != null)
					WorldActivity.mWorldEngine.setCamera(pInScene.getCamera());
				
				//start updating the new scene
				pInScene.setIgnoreUpdate(false);
			}
		});
	}
	
}
