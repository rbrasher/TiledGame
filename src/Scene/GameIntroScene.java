package Scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.Color;

import Util.AssetLoaderUtil;

import com.ronb.tiledgame.WorldActivity;

/**
 * This will load all the assets for the game and will show the logo
 * 
 * @author ron
 *
 */
public class GameIntroScene extends MyCameraScene {
	private WorldActivity mContext;
	public static final String TAG = "GameIntroScene";
	
	// ==========================================
	// CONSTRUCTOR
	// ==========================================
	public GameIntroScene(WorldActivity pMain) {
		super(WorldActivity.mWorldCamera, pMain);
		mContext = pMain;
	}
	
	// ==========================================
	// SUPER METHODS
	// ==========================================
	@Override
	public MyScene create() {
		//Attach the logo sprite
		this.setBackground(new Background(new Color(0, 0, 0)));
		
		//center the sprite with the scene
		float LogoX = WorldActivity.CAMERA_WIDTH / 2f - AssetLoaderUtil.mLogoTextureRegion.getWidth() / 2f;
		float LogoY = WorldActivity.CAMERA_HEIGHT / 2f - AssetLoaderUtil.mLogoTextureRegion.getHeight() / 2f;
		Sprite logo = new Sprite(LogoX, LogoY, AssetLoaderUtil.mLogoTextureRegion.deepCopy(), mContext.getVertexBufferObjectManager());
		this.attachChild(logo);
		return super.create();
	}
	
	@Override
	public MyScene delete() {
		this.detachChildren();
		return super.delete();
	}
	
	@Override
	public void inFocus() {
		
	}
}
