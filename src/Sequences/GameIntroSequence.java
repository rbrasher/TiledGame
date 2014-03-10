package Sequences;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.util.modifier.IModifier;

import Scene.SceneManager.SceneManager;
import Scene.SceneManager.Transitions.FadeInAlphaTransition;
import Scene.SceneManager.Transitions.FadeOutInAlphaTransition;

import com.ronb.tiledgame.WorldActivity;

/**
 * This will start with the intro scene and end on the MainMenuScene
 * 
 * @author ron
 *
 */
public class GameIntroSequence {

	private static final String TAG = "GameIntro";
	private TimeLineHandler mGameIntroSequence;
	private WorldActivity mMain;
	
	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public GameIntroSequence(WorldActivity pMain) {
		mMain = pMain;
		
		mGameIntroSequence = new TimeLineHandler(TAG, false) {
			@Override
			public void onFinish() {
				mGameIntroSequence.deleteTimeline();
			}
		};
		
		mGameIntroSequence.add(this.FadeIn());
		mGameIntroSequence.add(this.LoadAssets());
		mGameIntroSequence.add(this.FadeOut());
		mGameIntroSequence.start();
	}
	
	// ==========================================
	// PUBLIC METHODS
	// ==========================================
	
	
	// ==========================================
	// PRIVATE METHODS
	// ==========================================
	
	/**
	 * Fade into the logo
	 * 
	 * @return
	 */
	private TimeLineItem FadeIn() {
		TimeLineItem Item = new TimeLineItem() {
			
			@Override
			public void Procedure() {
				SceneManager.getInstance().setScene(SceneManager.mGameIntroScene, new FadeInAlphaTransition(mMain, new OnStatusUpdateListener() {
					
					@Override
					public void onFinish() {
						mGameIntroSequence.unpauseTimeline();    //start loading immediately
					}
				}));
			}
			
		};
		return Item;
	}
	
	/**
	 * Load all the assets in the game
	 * 
	 * @return
	 */
	private TimeLineItem LoadAssets() {
		TimeLineItem Item = new TimeLineItem() {
			private IUpdateHandler mUpdateLoad;
			private boolean mDelayFinished = false;
			
			@Override
			public void Procedure() {
				//runs the delay
				float Delay = 2.0f;
				SceneManager.mGameIntroScene.registerEntityModifier(new DelayModifier(Delay, new IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						mDelayFinished = true;
					}
					
				}));
				
				//this moves the load forward after the conditions have been met
				mUpdateLoad = new IUpdateHandler() {

					@Override
					public void onUpdate(float pSecondsElapsed) {
						if(mDelayFinished) {
							mGameIntroSequence.unpauseTimeline();
							WorldActivity.mWorldEngine.unregisterUpdateHandler(mUpdateLoad);
						}
					}

					@Override
					public void reset() {
						
					}
					
				};
				WorldActivity.mWorldEngine.registerUpdateHandler(mUpdateLoad);
			}
		};
		return Item;
	}
	
	/**
	 * Fade out of the logo scene
	 * 
	 * @return
	 */
	private TimeLineItem FadeOut() {
		TimeLineItem Item = new TimeLineItem() {
			
			@Override
			public void Procedure() {
				SceneManager.getInstance().setScene(SceneManager.mMainMenuScene, new FadeOutInAlphaTransition(mMain, new OnStatusUpdateListener() {
					
					@Override
					public void onFinish() {
						mGameIntroSequence.unpauseTimeline();
					}
				}));
			}
		};
		
		return Item;
	}
}
