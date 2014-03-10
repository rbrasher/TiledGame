package Scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import Menu.MenuButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ronb.tiledgame.WorldActivity;

public class MainMenuScene extends MyCameraScene {

	protected static final String TAG = "MainMenuScene";
	private WorldActivity mContext;
	private MenuButton mTouchButton;
	private MenuButton mDPadButton;
	private Sprite mMainMenuBackground;
	private MenuButton mPlayButton;
	
	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public MainMenuScene(WorldActivity pMain) {
		super(WorldActivity.mWorldCamera, pMain);
		mContext = pMain;
		float red = 1;
		float green = 1;
		float blue = 1;
		this.setBackground(new Background(red, green, blue));
	}
	
	// ==========================================
	// SUPER METHODS
	// ==========================================
	
	@Override
	public MyScene create() {
		mMainMenuBackground = new Sprite(0, 0, AssetLoaderUtil.mMainMenuTextureRegion, mContext.getVertexBufferObjectManager());
		this.attachChild(mMainMenuBackground);
		
		//create a button that goes into the game
		mPlayButton = new MenuButton(mMainMenuBackground.getWidth() / 2f, mMainMenuBackground.getHeight() / 2f, AssetLoaderUtil.mFontMENU, "Play", this, mContext, new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				SceneManager.mWorldScene.create();
				SceneManager.getInstance().setScene(SceneManager.mWorldScene, new FadeOutInAlphaTransition(mContext));
			}
			
		});
		mPlayButton.AttachButton(mMainMenuBackground);
		mPlayButton.setColor(Color.BLACK);
		
		//create a button that changes the UI control
		attachUISettingButton(mMainMenuBackground);
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
	// PRIVATE METHODS
	// ==========================================
	
	/**
	 * This is an example of how to create buttons that switch when clicked. It will control whether you use a
	 * DPAD or touch controls
	 * 
	 * @param pBacking
	 */
	private void attachUISettingButton(final Sprite pBacking) {
		//positions for the buttons
		float LabelX = pBacking.getWidth() * 0.25f;
		float ButtonX = pBacking.getWidth() * 0.75f;
		float ButtonY = pBacking.getHeight() * 0.3f;
		//Label
		Text UILabel = new Text(0, 0, AssetLoaderUtil.mFontMENU, "UI Control", mContext.getVertexBufferObjectManager());
		UILabel.setPosition(LabelX - UILabel.getWidth() / 2f, ButtonY);
		UILabel.setColor(Color.BLACK);
		mMainMenuBackground.attachChild(UILabel);
		//Buttons
		mTouchButton = new MenuButton(0, 0, AssetLoaderUtil.mFontMENU, "Touch", mContext, new OnTouchListener() {

			public void onTouch() {
				mTouchButton.DetachButton();
				mDPadButton.AttachButton(pBacking);
				WorldActivity.TOUCH_ENABLED = false;	//when touched the DPad will be used
			}
			
		});
		
		mDPadButton = new MenuButton(0, 0, AssetLoaderUtil.mFontMENU, "DPad", mContext, new OnTouchListener() {

			public void onTouch() {
				mDPadButton.DetachButton();
				mTouchButton.AttachButton(pBacking);
				WorldActivity.TOUCH_ENABLED = true;		//when touched the touch will be active
			}
			
		});
		
		mTouchButton.setColor(Color.BLACK);
		mDPadButton.setColor(Color.BLACK);
		
		//touch control is set as a default
		mTouchButton.setPosition(ButtonX - mTouchButton.getWidth() / 2f, ButtonY);
		mDPadButton.setPosition(ButtonX = mDPadButton.getWidth() / 2f, ButtonY);
		
		mTouchButton.AttachButton(pBacking);
	}
}
