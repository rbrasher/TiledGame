package Menu;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import Scene.SceneManager.SceneManager;
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
		
		if(!ButtonText.hasParent()) {
			pMenu.attachChild(ButtonText);
			pMenu.attachChild(TouchRect);
			mScene.registerTouchArea(TouchRect);
		}
	}
	
	//Detaches the button from the scene
	public void DetachButton() {
		WorldActivity.mWorldEngine.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				mBaseMenu.detachChild(ButtonText);
				mBaseMenu.detachChild(TouchRect);
				mScene.unregisterTouchArea(TouchRect);
			}
		});
	}
	
	//Switches the current button with the selected one
	public void SwitchButton(MenuButton pNextButton) {
		mNextButton = pNextButton;
		isNextButtonAttached = true;
		//Attaches the selected button to the selected background and detaches the current button
		pNextButton.AttachButton(mBaseMenu);
		this.DetachButton();
	}
	
	/**
	 * If the button has been switched then it is reset to the original. It does this by seeing if the
	 * button was switched, if it was then detach the switched button and attach the original button
	 */
	public void ResetButton() {
		if(isNextButtonAttached) {
			mNextButton.DetachButton();
			this.AttachButton(mBaseMenu);
			isNextButtonAttached = false;
		}
	}
	
	/**
	 * Changes the region where user input is recognized and sets the position of the touch rectangle.
	 */
	public void AlterTouchRegion(float pWidth, float pHeight, boolean pCenterRect) {
		if(pCenterRect) {
			TouchRect.setPosition(ButtonText.getX() + this.getWidth() / 2 - pWidth / 2, ButtonText.getY() + this.getHeight() / 2 - pHeight / 2);
		}
		TouchRect.setWidth(pWidth);
		TouchRect.setHeight(pHeight);
	}
	
	public void AlterTouchRegion(float pX, float pY, float pWidth, float pHeight) {
		TouchRect.setPosition(TouchRect.getX() + pX, TouchRect.getY() + pY);
		TouchRect.setWidth(pWidth);
		TouchRect.setHeight(pHeight);
	}
	
	//set the position of the button
	public void setPosition(float pX, float pY) {
		TouchRect.setPosition(pX, pY);
		ButtonText.setPosition(pX, pY);
	}
	
	/**
	 * Get the width of the button
	 */
	public float getWidth() {
		return ButtonText.getWidth();
	}
	
	/**
	 * Get the height of the button
	 */
	public float getHeight() {
		return ButtonText.getHeight();
	}
	
	/**
	 * Enables or disables the buttons touch area
	 */
	public void DisableButton() {
		mScene.unregisterTouchArea(TouchRect);
	}
	
	public void EnableButton() {
		mScene.registerTouchArea(TouchRect);
	}
	
	public IEntity GetParent() {
		return ButtonText.getParent();
	}
	
	public boolean hasParent() {
		return ButtonText.hasParent();
	}
	
	public void setVisible(boolean pVisible) {
		ButtonText.setVisible(pVisible);
	}
	
	public void setColor(Color pColor) {
		ButtonText.setColor(pColor);
	}
	
	/**
	 * Fades the button in or out
	 */
	public void FadeButton(float pDuration, float pInitial, float pFinal) {
		ButtonText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		ButtonText.registerEntityModifier(new AlphaModifier(pDuration, pInitial, pFinal));
	}
	
	//this is used when detaching something
	public void FadeButton(float pDuration, float pInitial, float pFinal, final OnStatusUpdateListener pOnFinishHandler) {
		ButtonText.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		ButtonText.registerEntityModifier(new AlphaModifier(pDuration, pInitial, pFinal, new IEntityModifierListener() {
			
			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				
			}
			
			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pOnFinishHandler.onFinish();
			}
		}));
	}
	
}
