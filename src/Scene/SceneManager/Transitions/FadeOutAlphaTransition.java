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
public class FadeOutAlphaTransition extends AbstractTransition {
	
	protected static final String TAG = "FadeOutTransition";
	private OnStatusUpdateListener mOnStatusUpdateListener;
	private WorldActivity mContext;
	
	/**
	 * This will control the fading transition
	 * @param pEaseFunction The ease of the transition
	 * @param pCreate If true the inScene will be created
	 * @param pMain the main activity
	 * @param pOnStatusUpdateListener more control via {@link OnStatusUpdateListener#onStart()} and{@link OnStatusUpdateListener#onFinish()}. If pCreate is true
	 * then added control via {@link OnStatusUpdateListener#onBackground()}
	 */
	public FadeOutAlphaTransition(WorldActivity pMain, IEaseFunction pEaseFunction, final OnStatusUpdateListener pOnStatusUpdateListener) {
		super(WorldActivity.FADE_DURATION, pEaseFunction);
		mOnStatusUpdateListener = pOnStatusUpdateListener;
		mContext = pMain;
	}

	/**
	 * This will control the fading transition
	 * @param pOnStatusUpdateListener more control via {@link OnStatusUpdateListener#onStart()} and{@link OnStatusUpdateListener#onFinish()}
	 */
	public FadeOutAlphaTransition(WorldActivity pMain, final OnStatusUpdateListener pOnStatusUpdateListener) {
		this(pMain, EaseLinear.getInstance(), pOnStatusUpdateListener);
	}


	/**
	 * This will control the fading transition
	 */
	public FadeOutAlphaTransition(WorldActivity pMain) {
		this(pMain, null);
	}
	
	
	@Override
	public void execute(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {
		final Rectangle fadeCover = new Rectangle(0, 0, pOutScene.getCamera().getWidth() + pOutScene.getCamera().getWidth() * 0.25f, 
				pOutScene.getCamera().getHeight() + pOutScene.getCamera().getHeight() * 0.25f, mContext.getVertexBufferObjectManager());
		fadeCover.setPosition(pOutScene.getCamera().getCenterX() - fadeCover.getWidth()/2f , pOutScene.getCamera().getCenterY() - fadeCover.getHeight()/2f);
		fadeCover.setColor(Color.BLACK);
		fadeCover.setZIndex(1000);
		fadeCover.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		fadeCover.setAlpha(0);//start transparent
		SceneManager.getInstance().getTransitionScene().attachChild(fadeCover);//attach the cover to the transition scene
		SceneManager.getInstance().getTransitionScene().sortChildren();

		//Set the z index for the scenes and the cover
		fadeCover.setZIndex(1000);
		pOutScene.setZIndex(1);
		pInScene.setZIndex(0);
		SceneManager.getInstance().getTransitionScene().sortChildren();
		
		//Disable the touch on the outgoing scene
		pOutScene.disableTouchOnScene();
		
		//set the incoming scene off camera
		pInScene.setPosition(pOutScene.getCamera().getWidth(), pOutScene.getCamera().getHeight());
		
		//Fade the cover in then move the new scene in position and fade the cover off.
        final AlphaModifier fadeCoverInModifier = new AlphaModifier(this.mDuration, 0, 1f);

		//The fade modifiers go one after the other
		fadeCover.registerEntityModifier(fadeCoverInModifier);
		fadeCoverInModifier.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				mOnStatusUpdateListener.onStart();
			}//end onModifieStarted
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
        		//Enable the touch on the ingoing scene
                pInScene.enableTouchOnScene();
				//switch scenes 
            	pOutScene.setPosition(pOutScene.getCamera().getWidth(), pOutScene.getCamera().getHeight());
            	pInScene.setPosition(0, 0);
				
				//Reverse z index
				pOutScene.setZIndex(0);
				pInScene.setZIndex(1);
				SceneManager.getInstance().getTransitionScene().sortChildren();

				//End transition methods
				mOnStatusUpdateListener.onFinish();
                FadeOutAlphaTransition.this.onTransitionProgress(pOutScene, pInScene, pTransitionListener);
                FadeOutAlphaTransition.this.onTransitionFinish(pOutScene, pInScene, pTransitionListener);
			}//end onModifierFinished
        });	
		
			
	}

	@Override
	public AbstractTransition getBackTransition() {
		return this;
	}
}
