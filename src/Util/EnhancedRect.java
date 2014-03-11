package Util;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import android.view.View.OnTouchListener;

import com.ronb.tiledgame.WorldActivity;

public class EnhancedRect {
	
	private Rectangle TouchRect;
	
	/**
	 * Creates a sprite with specific dimensions
	 */
	public EnhancedRect(float pX, float pY, float pWidth, float pHeight, Sprite pBackgroundSprite, WorldActivity pMain, final OnTouchListener pOnTouchListener) {
		TouchRect = new Rectangle(pX, pY, pWidth, pHeight, pMain.getVertexBufferObjectManager()) {
			private boolean rectTouched;
			private int BUFFER = 0;
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				//deals with what happens if touched
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						rectTouched = true;
						break;
					case TouchEvent.ACTION_UP:
						if(rectTouched) {
							rectTouched = false;
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								//do nothing if touched outside
								
							} else {
								//do something to the button when it is touched
								pOnTouchListener.onTouch();
							}
						}
						break;
						
					case TouchEvent.ACTION_MOVE:
						if(rectTouched) {
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								
							} else {
								//TODO: Instead of this, make a sprite that will go over the button
							}
						}
						break;
				}
				return true;
			}
		};
		TouchRect.setAlpha(0);
		pBackgroundSprite.attachChild(TouchRect);
	}
	
	
	public EnhancedRect(float pX, float pY, float pWidth, float pHeight, Sprite pBackgroundSprite, final Sprite pOverlaySprite, WorldActivity pMain, final OnTouchListener pOnTouchListener) {
		TouchRect = new Rectangle(pX, pY, pWidth, pHeight, pMain.getVertexBufferObjectManager()) {
			private boolean rectTouched;
			private int BUFFER = 0;
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						rectTouched = true;
						pOverlaySprite.setVisible(true);
						break;
					case TouchEvent.ACTION_UP:
						if(rectTouched) {
							rectTouched = false;
							pOverlaySprite.setVisible(false);
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								//do nothing if touched outside
							} else {
								//do something to the button when it is touched
								pOnTouchListener.onTouch();
							}
						}
						break;
					case TouchEvent.ACTION_MOVE:
						if(rectTouched) {
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getHeight() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								pOverlaySprite.setVisible(false);
							} else {
								pOverlaySprite.setVisible(true);
							}
						}
						break;
				}
				return true;
			}
		};
		TouchRect.setAlpha(0);
		pBackgroundSprite.attachChild(TouchRect);
	}
	
	/**
	 * This creates a touchable rectangle over a sprite
	 */
	public EnhancedRect(Sprite pSprite, WorldActivity pMain, final OnTouchListener pOnTouchListener) {
		TouchRect = new Rectangle(pSprite.getX(), pSprite.getY(), pSprite.getWidth(), pSprite.getHeight(), pMain.getVertexBufferObjectManager()) {
			private boolean rectTouched;
			private int BUFFER = 0;
			
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						rectTouched = true;
						break;
					case TouchEvent.ACTION_UP:
						if(rectTouched) {
							rectTouched = false;
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								//do nothing if touched outside
							} else {
								//do something when the button is touched
								pOnTouchListener.onTouch();
							}
						}
						break;
					case TouchEvent.ACTION_MOVE:
						if(rectTouched) {
							if(pTouchAreaLocalX < BUFFER || pTouchAreaLocalX > this.getWidth() - BUFFER || pTouchAreaLocalY < BUFFER || pTouchAreaLocalY > this.getHeight() - BUFFER) {
								
							} else {
								
							}
						}
						break;
				}
				return true;
			}
		};
		TouchRect.setAlpha(0);
		pSprite.attachChild(TouchRect);
	}
	
	/**
	 * Enable touch rect
	 */
	public void enableTouchRect(Scene pScene) {
		pScene.registerTouchArea(TouchRect);
	}
	
	/**
	 * Disable touch rect 
	 */
	public void disableTouchRect(Scene pScene) {
		pScene.unregisterTouchArea(TouchRect);
	}
	
	public IEntity getParent() {
		return TouchRect.getParent();
	}
	
	public boolean hasParent() {
		return TouchRect.hasParent();
	}
	
	
}
