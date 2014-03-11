package com.ronb.tiledgame;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import Scene.SceneManager.SceneManager;
import Sequences.GameIntroSequence;
import Util.AssetLoaderUtil;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;

/**
 * This class will run the overworld code. This is the main activity.
 * 
 * @author ron
 *
 */
public class WorldActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener {

	// ==========================================
	// CONSTANTS
	// ==========================================
	public static final String TAG = "WorldActivity";
	public static final float CAMERA_WIDTH = 480;
	public static final float CAMERA_HEIGHT = 320;
	public static final float FADE_DURATION = 0.5f;
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;
	public static final long ANIM_SPEED = 150;
	public static final long[] WALK_ANIMATE_DURATION = new long[] {ANIM_SPEED, ANIM_SPEED, ANIM_SPEED, ANIM_SPEED};
	public static final float TOUCH_MOVEMENT_WALKING_SPEED = 0.50f;
	public static final float DPAD_MOVEMENT_WALKING_SPEED = 1f;
	protected static final float CAMERA_SCROLLING = 0.25f;
	private static final int FRAMES_PER_SECOND = 60;
	public static final float RUNNING_SPEED = 0.45f;
	public static final int Y_COORDINATE = 1;
	public static final int X_COORDINATE = 0;
	public static final int UP_CENTER_TILE = 4;
	public static final int DOWN_CENTER_TILE = 1;
	public static final int RIGHT_CENTER_TILE = 10;
	public static final int LEFT_CENTER_TILE = 7;
	public static final float ANIMATE_TILE_DURATION = 0.5f;
	
	public static ZoomCamera mWorldCamera;
	public static Engine mWorldEngine;
	public static boolean TOUCH_ENABLED;
	private static ContextWrapper mActivityContext;
	
	//World Sprites
	public Sprite mMainCurtain;
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		Engine engine = new LimitedFPSEngine(pEngineOptions, FRAMES_PER_SECOND);
		return engine;
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		mActivityContext = this;
		
		//initializes the Engine and sets the height and width
		mWorldCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		//set the engine options
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mWorldCamera);
		engineOptions.getRenderOptions().setMultiSampling(false);
		//Adds audio capabilities
		engineOptions.getAudioOptions().setNeedsSound(true).setNeedsMusic(true);
		//forces the screen to go into landscape. Prevents the app from crashing when the screen is being rotated
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		AssetLoaderUtil.loadAllFonts(this);
		AssetLoaderUtil.loadAllTextures(this);
	}

	@Override
	protected Scene onCreateScene() {
		this.getEngine().registerUpdateHandler(new FPSLogger());
		//initializes the engine
		mWorldEngine = this.getEngine();
		
		//start with touch enabled
		TOUCH_ENABLED = true;
		
		mMainCurtain = new Sprite(0, 0, AssetLoaderUtil.FadeTextureRegion, this.getVertexBufferObjectManager());
		
		SceneManager.initialize(this);
		SceneManager.mLoadingScene.create();
		SceneManager.mGameIntroScene.create();
		SceneManager.mMainMenuScene.create();
		
		//goes to main menu
		new GameIntroSequence(this);
		
		return SceneManager.mLoadingScene;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// The constraints on the TouchEvents are there to prevent errors that occur when the screen is touched while loading
		// or if the scene is touched outside of the TMXMap
		final float touchedX = pSceneTouchEvent.getX();
		final float touchedY = pSceneTouchEvent.getY();
		
		final TMXTile lTargetTile = SceneManager.mWorldScene.mTMXMapTouchLayer.getTMXTileAt(touchedX, touchedY);
		if(pSceneTouchEvent.isActionDown() && touchedX < SceneManager.mWorldScene.mTMXMapTouchLayer.getWidth() && touchedY < SceneManager.mWorldScene.mTMXMapTouchLayer.getHeight() && touchedX > 0 && touchedY > 0) {
			SceneManager.mWorldScene.setTouchTile(lTargetTile);	//sets the tile that the player is walking to
			//walks to the touched area
			SceneManager.mWorldScene.mPlayerAstarPath.walkTo(lTargetTile);
		}
		
		return false;
	}
	
	// ==========================================
	// GETTERS & SETTERS
	// ==========================================
	
	
	// ==========================================
	// STATIC METHODS
	// ==========================================
	
	//gets a string from the xml based on the name rather than the key
	static public String getAndroidString(String pStringName) {
		int resourceId = mActivityContext.getResources().getIdentifier(pStringName, "string", mActivityContext.getPackageName());
		if(resourceId == 0) {
			return null;
		} else {
			return mActivityContext.getResources().getString(resourceId);
		}
	}

}
