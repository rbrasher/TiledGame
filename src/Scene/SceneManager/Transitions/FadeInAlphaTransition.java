package Scene.SceneManager.Transitions;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import Scene.MyScene;
import Scene.SceneManager.SceneManager;
import android.opengl.GLES20;

import com.ronb.tiledgame.WorldActivity;

/**
 * This will fade the current scene out and the new scene in
 * 
 * @author ron
 *
 */
public class FadeInAlphaTransition extends AbstractTransition {
	
	protected static final String TAG = "FadeInAlphaTransition";
	private OnStatusUpdateListener mOnStatusUpdateListener;
	private WorldActivity mContext;
	
	public FadeInAlphaTransition(WorldActivity pMain, IEaseFunction pEaseFunction, final OnStatusUpdateListener pOnStatusUpdateListener) {
		super(WorldActivity.FADE_DURATION, pEaseFunction);
		mOnStatusUpdateListener = pOnStatusUpdateListener;
		mContext = pMain;
	}
	
	public FadeInAlphaTransition(WorldActivity pMain, final OnStatusUpdateListener pOnStatusUpdateListener) {
		this(pMain, EaseLinear.getInstance(), pOnStatusUpdateListener);
	}
	
	/**
	 * this will control the fading transition
	 */
	public FadeInAlphaTransition(WorldActivity pMain) {
		this(pMain, null);
	}
	
	@Override
	public void execute(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {
		final Rectangle fadeCover = new Rectangle(0, 0, pInScene.getCamera().getWidth() + pInScene.getCamera().getWidth() * 0.25f, pInScene.getCamera().getHeight() + pInScene.getCamera().getHeight() * 0.25f, mContext.getVertexBufferObjectManager());
		SceneManager.getInstance().getTransitionScene().attachChild(fadeCover);		//attach the fadeCover to the transition scene
		fadeCover.setPosition(pInScene.getCamera().getCenterX() - fadeCover.getWidth() / 2f, pInScene.getCamera().getCenterY() - fadeCover.getHeight() / 2f);
		fadeCover.setColor(Color.BLACK);
		fadeCover.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		fadeCover.setAlpha(1f);		//start transparent
		fadeCover.setCullingEnabled(true);
		//set the z-index for the scenes and the cover
		fadeCover.setZIndex(1000);
		pOutScene.setZIndex(0);
		pInScene.setZIndex(1);
		SceneManager.getInstance().getTransitionScene().sortChildren();
		
		//disable the touch on the outgoing scene
		pOutScene.disableTouchOnScene();
		//set the incoming scene off camera
		//pOutScene.setPosition(pInScene.getCamera().getWidth(), pInScene.getCamera().getHeight());
		
		//switch scenes
		pOutScene.setPosition(pOutScene.getCamera().getWidth(), pOutScene.getCamera().getHeight());
		pInScene.setPosition(0, 0);
		
		//fade the cover in then move the new scene in position and fade the cover off
		final AlphaModifier fadeCoverOutModifier = new AlphaModifier(this.mDuration, 1f, 0);
		
		//the fade modifiers go one after the other
		fadeCover.registerEntityModifier(fadeCoverOutModifier);
		fadeCoverOutModifier.addModifierListener(new IModifierListener<IEntity>() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				if(mOnStatusUpdateListener != null)
					mOnStatusUpdateListener.onStart();
				
				FadeInAlphaTransition.this.onTransitionProgress(pOutScene, pInScene, pTransitionListener);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// enable the touch on the ingoing scene
				pInScene.enableTouchOnScene();
				//end transition methods
				if(mOnStatusUpdateListener != null)
					mOnStatusUpdateListener.onFinish();
				
				FadeInAlphaTransition.this.onTransitionFinish(pOutScene, pInScene, pTransitionListener);
			}
			
		});
	}
	
	@Override
	public AbstractTransition getBackTransition() {
		return this;
	}
}
