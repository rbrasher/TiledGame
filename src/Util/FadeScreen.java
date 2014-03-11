package Util;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseLinear;

import android.opengl.GLES20;

/**
 * Fades in and out of the screen. This will affect the performance if the curtain is not removed or reduced in size.
 *  
 * @author ron
 *
 */
public class FadeScreen {
	
	private Sprite mCurtain;
	private AlphaModifier mFadeModifier;
	
	// ==========================================
	// CONSTRUCTORS
	// ==========================================
	public FadeScreen(Scene pScene, Sprite pCurtain) {
		mCurtain = pCurtain;
		mCurtain.setCullingEnabled(true);    //this is supposed to help with the FPS
		mCurtain.setScale(1.0f);
		mCurtain.setVisible(true);
		mCurtain.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		if(!mCurtain.hasParent()) {
			pScene.attachChild(mCurtain);
			mCurtain.setZIndex(1000);
			mCurtain.getParent().sortChildren();
		} else {
			mCurtain.setZIndex(1000);
			mCurtain.getParent().sortChildren();
		}
	}
	
	//this is for the WorldActivity
	public FadeScreen(Scene pScene, Sprite pCurtain, BoundCamera pCamera) {
		this(pScene, pCurtain);
		mCurtain.setPosition(pCamera.getCenterX() - mCurtain.getWidth() / 2, pCamera.getCenterY() - mCurtain.getHeight() / 2);
		mCurtain.setScale(1.5f);
	}
	
	public FadeScreen(Scene pScene, Sprite pCurtain, TMXTile pPlayerTile) {
		this(pScene, pCurtain);
		mCurtain.setPosition(pPlayerTile.getTileX() - mCurtain.getWidth() / 2, pPlayerTile.getTileY() - mCurtain.getHeight() / 2);
		mCurtain.setScale(2.0f);
	}
	
	//this is used if the fade needs to happen in the world scene
	public FadeScreen(float pX, float pY, Scene pScene, Sprite pCurtain) {
		this(pScene, pCurtain);
		//set the center of the curtain and the players position
		pCurtain.setPosition(pX - pCurtain.getWidth() / 2, pY - pCurtain.getHeight() / 2);
	}
	
	// ==========================================
	// PUBLIC METHODS
	// ==========================================
	public void FadeIn(float FADE_DURATION, final OnStatusUpdateListener pOnFinishHandler) {
		mFadeModifier = new AlphaModifier(FADE_DURATION, 1f, 0, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				pOnFinishHandler.onStart();
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pOnFinishHandler.onFinish();
				mCurtain.setScale(0.1f);
			}
			
		}, EaseLinear.getInstance());
		mFadeModifier.setAutoUnregisterWhenFinished(true);
		mCurtain.registerEntityModifier(mFadeModifier);
	}
	
	public void FadeIn(float FADE_DURATION) {
		mFadeModifier = new AlphaModifier(FADE_DURATION, 1f, 0, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				mCurtain.setScale(0.1f);
			}
			
		}, EaseLinear.getInstance());
		mFadeModifier.setAutoUnregisterWhenFinished(true);
		mCurtain.registerEntityModifier(mFadeModifier);
	}
	
	public void FadeOut(float FADE_DURATION) {
		mFadeModifier = new AlphaModifier(FADE_DURATION, 0, 1f, EaseLinear.getInstance());
		
		mFadeModifier.setAutoUnregisterWhenFinished(true);
		mCurtain.registerEntityModifier(mFadeModifier);
	}
	
	public void FadeOut(float FADE_DURATION, final OnStatusUpdateListener pOnFinishHandler) {
		mFadeModifier = new AlphaModifier(FADE_DURATION, 0, 1f, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				pOnFinishHandler.onStart();
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pOnFinishHandler.onFinish();
			}
			
		}, EaseLinear.getInstance());
		
		mFadeModifier.setAutoUnregisterWhenFinished(true);
		mCurtain.registerEntityModifier(mFadeModifier);
	}
}
