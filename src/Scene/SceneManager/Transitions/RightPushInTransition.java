package Scene.SceneManager.Transitions;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.IEaseFunction;

import Scene.MyScene;
import Scene.SceneManager.SceneManager;

public class RightPushInTransition extends AbstractTransition {
	
	private OnStatusUpdateListener mOnStatusUpdateListener;
    
	public RightPushInTransition(float pDuration) {
        this(pDuration, new OnStatusUpdateListener());
    }
	
	public RightPushInTransition(float pDuration, OnStatusUpdateListener pOnStatusUpdateListener) {
        super(pDuration);
        mOnStatusUpdateListener = pOnStatusUpdateListener;
    }

    public RightPushInTransition(float mDuration, IEaseFunction pEaseFunction) {
        super(mDuration, pEaseFunction);
    }

    @Override
    public void execute(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {

        float width = SceneManager.getInstance().getSurfaceWidth();

        MoveXModifier outModifier = new MoveXModifier(this.mDuration, 0, -width, mEaseFunction);
        MoveXModifier inModifier = new MoveXModifier(this.mDuration, width, 0, mEaseFunction);

        pInScene.registerEntityModifier(inModifier);
        pOutScene.registerEntityModifier(outModifier);

        inModifier.addModifierListener(new IModifierListener<IEntity>() {

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                onTransitionFinish(pOutScene, pInScene, pTransitionListener);
                mOnStatusUpdateListener.onFinish();
            }

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,
					IEntity pItem) {
				mOnStatusUpdateListener.onStart();
                onTransitionProgress(pOutScene, pInScene, pTransitionListener);
			}
        });

    }

    @Override
    public AbstractTransition getBackTransition() {
        return this;
    }
}
