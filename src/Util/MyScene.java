package Util;

import java.util.ArrayList;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;

public class MyScene extends Scene {

	public static final String TAG = "MyScene";
	private ArrayList<ITouchArea> mSavedTouchAreas;
	
	public void saveTouchAreas() {
		mSavedTouchAreas = new ArrayList<ITouchArea>();
		for(ITouchArea iTouch : mTouchAreas) {
			mSavedTouchAreas.add(iTouch);
		}
	}
	
	public void registerSavedTouchAreas() {
		for(ITouchArea iTouch : mSavedTouchAreas) {
			this.registerTouchArea(iTouch);
		}
	}
}
