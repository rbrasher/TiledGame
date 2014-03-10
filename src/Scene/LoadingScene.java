package Scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.ronb.tiledgame.WorldActivity;

public class LoadingScene extends MyCameraScene {

	protected static final String TAG = "LoadingScene";
	private static final float ANIMATE_DELAY = 0.5f;
	private Text mLoadingText;
	private TimerHandler mAnimateDotsTimer;
	private WorldActivity mContext;
	
	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	
	/**
	 * Instantiates a new fade screen. This is created once and used wherever it needs to be faded. You need to attach it to the parent scene
	 * before fading, otherwise the game will stall.
	 * 
	 * @param pScene - this does not seem to be used
	 * @param pCamera
	 * @param pMain
	 */
	public LoadingScene(Camera pCamera, WorldActivity pMain) {
		super(pCamera, pMain);
		
		this.setBackground(new Background(new Color(Color.BLACK)));
		mContext = pMain;
		
		//create the loading text
		mLoadingText = new Text(0, 0, AssetLoaderUtil.mFontTEXT, "Loading", 10, pMain.getVertexBufferObjectManager());
		int textX = (int) (WorldActivity.CAMERA_WIDTH - (mLoadingText.getWidthScaled() + WorldActivity.CAMERA_WIDTH * 0.1f));
		int textY = (int) (WorldActivity.CAMERA_HEIGHT - (mLoadingText.getHeightScaled() + WorldActivity.CAMERA_HEIGHT * 0.1f));
		mLoadingText.setPosition(textX, textY);
		mLoadingText.setVisible(false);
		this.attachChild(mLoadingText);
	}
	
	@Override
	public MyScene create() {
		return super.create();
	}
	
	@Override
	public MyScene delete() {
		return super.delete();
	}
	
	@Override
	public void inFocus() {
		
	}
	
	// ==========================================
	// METHODS
	// ==========================================
	
	/**
	 * Show the loading text on the scene
	 * 
	 * @param pVisible
	 */
	public void setLoadingTextVisible(boolean pVisible) {
		mLoadingText.setVisible(pVisible);
		//if true, animate the dots
		if(pVisible)
			this.animateDots();
		else
			this.stopAnimateDots();
	}
	
	/**
	 * Does an animation with dots on the scene
	 */
	private void animateDots() {
		mAnimateDotsTimer = new TimerHandler(ANIMATE_DELAY, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if(mLoadingText.getText().equals("Loading"))
					mLoadingText.setText("Loading.");
				else if (mLoadingText.getText().equals("Loading."))
					mLoadingText.setText("Loading..");
				else if (mLoadingText.getText().equals("Loading.."))
					mLoadingText.setText("Loading...");
				else if (mLoadingText.getText().equals("Loading..."))
					mLoadingText.setText("Loading");
			}
			
		});
		this.registerUpdateHandler(mAnimateDotsTimer);
	}
	
	/**
	 * Stops the animation of the dots on the scene
	 */
	private void stopAnimateDots() {
		this.unregisterUpdateHandler(mAnimateDotsTimer);
	}
}
