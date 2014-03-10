package Scene;

import org.andengine.engine.camera.Camera;

import com.ronb.tiledgame.WorldActivity;

/**
 * While you can add a {@link MyHUD} to {@link Scene}, you should not do so.
 * {@link MyHUD}s are meant to be added to {@link Camera}s via {@link Camera#setHUD(MyHUD)}
 * 
 * @author ron
 *
 */
public class MyHUD extends MyCameraScene {
	
	// ==========================================
	// CONSTANTS
	// ==========================================
	
	// ==========================================
	// FIELDS
	// ==========================================
	
	// ==========================================
	// CONSTRUCTORS
	// ==========================================
	public MyHUD(final WorldActivity pMain) {
		super(pMain);
		
		this.setBackgroundEnabled(false);
	}
	
	// ==========================================
	// GETTER & SETTER
	// ==========================================
	
	// ==========================================
	// METHODS FOR/FROM SUPERCLASS/INTERFACES
	// ==========================================
	
	// ==========================================
	// METHODS
	// ==========================================
	
}
