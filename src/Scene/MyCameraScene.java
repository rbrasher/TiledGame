package Scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.ronb.tiledgame.WorldActivity;

public class MyCameraScene extends MyScene {
	
	// ==========================================
	// CONSTANTS
	// ==========================================
	
	// ==========================================
	// FIELDS
	// ==========================================
	
	// ==========================================
	// CONSTRUCTORS
	// ==========================================
	
	/**
	 * {@link CameraScene#setCamera(Camera)} needs to be called manually. Otherwise nothing will be drawn.
	 */
	public MyCameraScene(final WorldActivity pMain) {
		this(null, pMain);
	}
	
	public MyCameraScene(final Camera pCamera, final WorldActivity pMain) {
		super(pCamera, pMain);
	}
	
	public MyCameraScene(final Camera pCamera) {
		super(pCamera);
	}
	
	// ==========================================
	// GETTER & SETTER
	// ==========================================
	public Camera getCamera() {
		return this.mCamera;
	}
	
	public void setCamera(final Camera pCamera) {
		this.mCamera = pCamera;
	}
	
	// ==========================================
	// METHODS FOR/FROM SUPERCLASS/INTERFACES
	// ==========================================
	@Override
	public boolean onSceneTouchEvent(final TouchEvent pSceneTouchEvent) {
		if(this.mCamera == null) {
			return false;
		} else {
			this.mCamera.convertSceneToCameraSceneTouchEvent(pSceneTouchEvent);
			
			final boolean handled = super.onSceneTouchEvent(pSceneTouchEvent);
			
			if(handled) {
				return false;
			} else {
				this.mCamera.convertCameraSceneToSceneTouchEvent(pSceneTouchEvent);
				return false;
			}
		}
	}
	
	@Override
	protected boolean onChildSceneTouchEvent(final TouchEvent pSceneTouchEvent) {
		final boolean childIsCameraScene = this.mChildScene instanceof CameraScene;
		if(childIsCameraScene) {
			this.mCamera.convertCameraSceneToSceneTouchEvent(pSceneTouchEvent);
			final boolean result = super.onChildSceneTouchEvent(pSceneTouchEvent);
			this.mCamera.convertSceneToCameraSceneTouchEvent(pSceneTouchEvent);
			return result;
		} else {
			return super.onChildSceneTouchEvent(pSceneTouchEvent);
		}
	}
	
	@Override
	protected void onApplyMatrix(final GLState pGLState, final Camera pCamera) {
		this.mCamera.onApplyCameraSceneMatrix(pGLState);
	}
	
	// ==========================================
	// METHODS
	// ==========================================
	public void centerShapeInCamera(final IAreaShape pAreaShape) {
		final Camera camera = this.mCamera;
		pAreaShape.setPosition((camera.getWidth() - pAreaShape.getWidth()) * 0.5f, (camera.getHeight() - pAreaShape.getHeight()) * 0.5f);
	}
	
	public void centerShapeInCameraHorizontally(final IAreaShape pAreaShape) {
		pAreaShape.setPosition((this.mCamera.getWidth() - pAreaShape.getWidth()) * 0.5f, pAreaShape.getY());
	}
	
	public void centerShapeInCameraVertically(final IAreaShape pAreaShape) {
		pAreaShape.setPosition(pAreaShape.getX(), (this.mCamera.getHeight() - pAreaShape.getHeight()) * 0.5f);
	}
}
