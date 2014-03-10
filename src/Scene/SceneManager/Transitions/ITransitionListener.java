package Scene.SceneManager.Transitions;

import Scene.MyScene;

public interface ITransitionListener {
	
	public void onTransitionFinished(MyScene pOutScene, MyScene pInScene, AbstractTransition pTransition);
	
	public void onTransitionProgress(MyScene pOutScene, MyScene pInScene, AbstractTransition pTransition);
	
}
