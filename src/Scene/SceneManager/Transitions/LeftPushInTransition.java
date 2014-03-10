package Scene.SceneManager.Transitions;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.IModifier.IModifierListener;
import org.andengine.util.modifier.ease.IEaseFunction;

import Scene.MyScene;
import Scene.SceneManager.SceneManager;

public class LeftPushInTransition extends AbstractTransition {
	
	public LeftPushInTransition(float pDuration) {
        super(pDuration);
    }

    public LeftPushInTransition(float mDuration, IEaseFunction pEaseFunction) {
        super(mDuration, pEaseFunction);
    }

    @Override
    public void execute(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {

        float width = SceneManager.getInstance().getSurfaceWidth();

        MoveXModifier outModifier = new MoveXModifier(1, 0, width, mEaseFunction);
        MoveXModifier inModifier = new MoveXModifier(1, -width, 0, mEaseFunction);

        pInScene.registerEntityModifier(inModifier);
        pOutScene.registerEntityModifier(outModifier);

        inModifier.addModifierListener(new IModifierListener<IEntity>() {

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                onTransitionFinish(pOutScene, pInScene, pTransitionListener);
            }

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier,	IEntity pItem) {			
                onTransitionProgress(pOutScene, pInScene, pTransitionListener);	
			}
        });

    }

    @Override
    public AbstractTransition getBackTransition() {
        return this;
    }
}
