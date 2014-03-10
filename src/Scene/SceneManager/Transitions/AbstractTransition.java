package Scene.SceneManager.Transitions;

import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

import Scene.MyScene;

public abstract class AbstractTransition {
	
	protected IEaseFunction mEaseFunction;
	protected float mDuration;
	
	public AbstractTransition(float pDuration) {
		this(pDuration, EaseLinear.getInstance());
	}
	
	public AbstractTransition(float pDuration, IEaseFunction pEaseFunction) {
		this.mDuration = pDuration;
		this.mEaseFunction = pEaseFunction;
	}
	
	public abstract void execute(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener);
	
	public abstract AbstractTransition getBackTransition();
	
	protected void onTransitionProgress(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {
		pTransitionListener.onTransitionProgress(pOutScene, pInScene, this);
	}
	
	protected void onTransitionFinish(final MyScene pOutScene, final MyScene pInScene, final ITransitionListener pTransitionListener) {
		pOutScene.setPosition(0, 0);
		pOutScene.setRotation(0);
		pOutScene.setAlpha(1);
		pOutScene.setScale(1);
		pOutScene.setVisible(true);
		
		pInScene.setPosition(0, 0);
		pInScene.setRotation(0);
		pInScene.setAlpha(1);
		pInScene.setScale(1);
		pInScene.setVisible(true);
		
		pTransitionListener.onTransitionFinished(pOutScene, pInScene, this);
	}
}
