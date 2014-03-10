package Scene.SceneManager.Transitions;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
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
 * This will fade the current scene out and the new scene in. Can only be used when switching between
 * scenes with the same camera
 * 
 * @author ron
 *
 */
public class FadeOutInAlphaTransition extends AbstractTransition {
	
	protected static final String TAG = "FadeInOutTransition";
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
	public FadeOutInAlphaTransition(WorldActivity pMain, IEaseFunction pEaseFunction, final OnStatusUpdateListener pOnStatusUpdateListener) {
		super(WorldActivity.FADE_DURATION, pEaseFunction);
		mOnStatusUpdateListener = pOnStatusUpdateListener;
		mContext = pMain;
	}
	
	/**
	 * This will control the fading transition
	 * @param pOnStatusUpdateListener more control via {@link OnStatusUpdateListener#onStart()} and{@link OnStatusUpdateListener#onFinish()}
	 */
	public FadeOutInAlphaTransition(WorldActivity pMain, final OnStatusUpdateListener pOnStatusUpdateListener) {
		this(pMain, EaseLinear.getInstance(), pOnStatusUpdateListener);
	}

	/**
	 * This will control the fading transition
	 */
	public FadeOutInAlphaTransition(WorldActivity pMain) {
		this(pMain, new OnStatusUpdateListener());
	}
	
	@Override
	public void execute(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {
		//TODO: Try using a camera scene instead of making the rectangle huge
		final Rectangle fadeCover = new Rectangle(0, 0, pInScene.getCamera().getWidth() + pInScene.getCamera().getWidth() * 10, 
				pInScene.getCamera().getHeight() + pInScene.getCamera().getHeight() * 10f, mContext.getVertexBufferObjectManager());
		SceneManager.getInstance().getTransitionScene().attachChild(fadeCover);//attach the cover to the transition scene
		fadeCover.setPosition(pInScene.getCamera().getCenterX() - fadeCover.getWidth()/2f , pInScene.getCamera().getCenterY() - fadeCover.getHeight()/2f);
		fadeCover.setColor(Color.BLACK);
		fadeCover.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		fadeCover.setAlpha(0);//start transparent
		fadeCover.setCullingEnabled(true);

		//Set the z index for the scenes and the cover
		fadeCover.setZIndex(1000);
		pOutScene.setZIndex(1);
		pInScene.setZIndex(0);
		SceneManager.getInstance().getTransitionScene().sortChildren();
		
		//Disable the touch on the outgoing scene
		pOutScene.disableTouchOnScene();
		
		//set the incoming scene off camera
		pInScene.setPosition(pInScene.getCamera().getWidth(), pInScene.getCamera().getHeight());
		
		//Fade the cover in then move the new scene in position and fade the cover off.
        final AlphaModifier fadeCoverInModifier = new AlphaModifier(this.mDuration, 0, 1f);
        final AlphaModifier fadeCoverOutModifier = new AlphaModifier(this.mDuration, 1f, 0);

		//The fade modifiers go one after the other
		fadeCover.registerEntityModifier(new SequenceEntityModifier(fadeCoverInModifier, fadeCoverOutModifier));
		fadeCoverInModifier.addModifierListener(new IModifierListener<IEntity>() {
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				mOnStatusUpdateListener.onStart();
			}//end onModifieStarted
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				//switch scenes 
            	pOutScene.setPosition(pInScene.getCamera().getWidth(), pInScene.getCamera().getHeight());
				//Reverse z index
				pOutScene.setZIndex(0);
				pInScene.setZIndex(1);
				SceneManager.getInstance().getTransitionScene().sortChildren();
                FadeOutInAlphaTransition.this.onTransitionProgress(pOutScene, pInScene, pTransitionListener);
			}//end onModifierFinished
        });
		
		fadeCoverOutModifier.addModifierListener(new IModifierListener<IEntity>() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {	
            	pInScene.setPosition(0, 0);			
			}//end onModifieStarted
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
        		//Enable the touch on the ingoing scene
                pInScene.enableTouchOnScene();
				//End transition methods
				mOnStatusUpdateListener.onFinish();
                FadeOutInAlphaTransition.this.onTransitionFinish(pOutScene, pInScene, pTransitionListener);
			}//end onModifierFinished
        });
		
	}

	@Override
	public AbstractTransition getBackTransition() {
		return this;
	}
}
