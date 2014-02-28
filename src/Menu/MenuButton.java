package Menu;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.View.OnTouchListener;

import com.ronb.tiledgame.WorldActivity;

public class MenuButton {

	private final Text ButtonText;
	private final Rectangle TouchRect;
	private Sprite mBaseMenu;	//serves as the background sprite where the buttons will be attached
	private final Scene mScene;
	private MenuButton mNextButton;
	private boolean isNextButtonAttached;
	
	public MenuButton(final float pX, final float pY, Font pFont, String pName, Scene pScene, WorldActivity pContext, final OnTouchListener pOnTouchListener) {
		mScene = pScene;
		isNextButtonAttached = false;
		//generic button
		ButtonText = new Text(pX, pY, pFont, pName, pContext.getVertexBufferObjectManager());
		TouchRect = new Rectangle(pX - 5, pY - 10, ButtonText.getWidth() + 15, ButtonText.getHeight() + 15, ((BaseGameActivity) pContext).getVertexBufferObjectManager()) {
			private boolean rectTouched;
			private int BUFFER = 0;
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				//deals with what happens if touched
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						rectTouched = true;
						this.setColor(0.0f, 0.1f, 0.0f);
						this.setAlpha(0.5f);
						break;
					case TouchEvent.ACTION_UP:
						if(rectTouched) {
							rectTouched = false;
							this.setColor(0.0f, 0.0f, 0.0f);
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								//do nothing if touched outside
							} else {
								//do something when the button is pushed
								pOnTouchListener.onTouch(null, null);
							}
						}
						break;
					case TouchEvent.ACTION_MOVE:
						if(rectTouched) {
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								this.setColor(0.0f, 0.0f, 0.0f);
								this.setAlpha(0.0f);
							} else {
								//TODO: Instead of this, make a sprite that will go over the button
								this.setAlpha(0.5f);
								this.setColor(0.0f, 1.0f, 0.0f);
							}
						}
						break;
				}
				return true;
				
			}
		};
		
		ButtonText.setColor(0.0f, 0.0f, 0.0f);
		TouchRect.setAlpha(0);
	}
	
	public MenuButton(final float pX, final float pY, Font pFont, String pName, WorldActivity pContext, final OnTouchListener pOnTouchListener) {
		this(pX, pY, pFont, pName, SceneManager.mWorldScene, pContext, pOnTouchListener);
	}
	
	//attaches the MenuButton to the scene
	public void AttachButton(Sprite pMenu) {
		mBaseMenu = pMenu;
		if(!ButtonText.hasParent()) {
			pMenu.attachChild(ButtonText);
			pMenu.attachChild(TouchRect);
			mScene.registerTouchArea(TouchRect);
		}
	}
	
	public void AttachButton(Sprite pMenu, boolean pCenterText) {
		mBaseMenu = pMenu;
		if(pCenterText)
			ButtonText.setPosition(pMenu.getWidth() / 2 - ButtonText.getWidth() / 2, pMenu.getHeight() / 2 - ButtonText.getHeight() / 2);
		
		if(!ButtonText.hasParent()) {
			pMenu.attachChild(ButtonText);
			pMenu.attachChild(TouchRect);
			mScene.registerTouchArea(TouchRect);
		}
	}
	
	//center the touch rectangle
	public void AttachButton(Sprite pMenu, boolean pCenterText, boolean pMatchTouchRect) {
		mBaseMenu = pMenu;
		
		if(pCenterText)
			ButtonText.setPosition(pMenu.getWidth() / 2 - ButtonText.getWidth() / 2, pMenu.getHeight() / 2 - ButtonText.getHeight() / 2);
		
		if(pMatchTouchRect) {
			TouchRect.setHeight(pMenu.getHeight());
			TouchRect.setWidth(pMenu.getWidth());
			TouchRect.setPosition(0, 0);
		}
		
		if(!ButtonText.hasParent())
			
			
	}
	
}
