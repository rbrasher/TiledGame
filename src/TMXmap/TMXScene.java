package TMXmap;

import org.andengine.extension.tmx.TMXTiledMap;

import GameSprite.MyAnimatedSprite;
import Path.AstarPath;
import Scene.SceneManager.SceneManager;
import android.os.AsyncTask;

import com.ronb.tiledgame.WorldActivity;
import com.ronb.tiledgame.Enums.EMap_ID;

/**
 * This class loads the scene with the TMX and attaches it to the scene
 * 
 * @author ron
 *
 */
public class TMXScene {
	private static final int PLAYER_ZINDEX = 10;
	private WorldActivity mContext;
	private EMap_ID mMapName;
	private TMXmapLoader mNewTMXMapLoader;
	private MyAnimatedSprite mPlayerSprite;

	public TMXScene(final EMap_ID pMapName, final WorldActivity pMain,final OnStatusUpdateListener pOnExecute){
		mContext = pMain;
		mPlayerSprite = SceneManager.mWorldScene.getPlayerSprite();
		mMapName = pMapName;
		
		mContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//Use AsyncTask to load TMX map 			 
				new AsyncTask<TMXTiledMap, Integer, TMXTiledMap>() {	

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						if(pOnExecute != null)
							pOnExecute.onStart();
						mPlayerSprite.clearUpdateHandlers();
						mPlayerSprite.clearEntityModifiers();
					}

					@Override
					protected TMXTiledMap doInBackground(TMXTiledMap... params) {
						//Initializes the TMXLoader and TMX map	
						mNewTMXMapLoader = new TMXmapLoader(mMapName, mContext);
						final TMXTiledMap NewTMXMap = mNewTMXMapLoader.getTMXMap();
						//used in astar path. Sets the bottom layer as the utility layer
						SceneManager.mWorldScene.setTouchLayer(mNewTMXMapLoader.getTMXMapLayer(0));
						SceneManager.mWorldScene.setTMXMapLoader(mNewTMXMapLoader);
						SceneManager.mWorldScene.setTMXTiledMap(NewTMXMap);
						//This is supposed to help with the jerkiness
						mPlayerSprite.registerSceneUpdater();
						//Start tracking the Player tiles
						mPlayerSprite.registerTileTracker(mNewTMXMapLoader.getTMXMapLayer(0));
						//Register the exit tile updater
						mPlayerSprite.registerExitPathUpdater(mNewTMXMapLoader);
						//Register the animation tile updater
						mPlayerSprite.registerAnimationUpdater();
						//Register the alter tile updater
						mPlayerSprite.registerAlterTileUpdater(mNewTMXMapLoader);
						//Set the z index
						mPlayerSprite.setZIndex(PLAYER_ZINDEX);
						//Make the camera not exceed the bounds of the TMXEntity. 
						WorldActivity.mWorldCamera.setBounds(0, 0, NewTMXMap.getTileColumns() * NewTMXMap.getTileWidth(), NewTMXMap.getTileRows() * NewTMXMap.getTileHeight());
						//Initializes the player astar path
						SceneManager.mWorldScene.setAstarPath(new AstarPath(NewTMXMap, mNewTMXMapLoader, pMain).generatePathMap());
						//attach player sprite
						WorldActivity.mWorldCamera.setChaseEntity(mPlayerSprite);
						
						return NewTMXMap;
					}

					@Override
					protected void onPostExecute(final TMXTiledMap pTMXTiledMap) {
						super.onPostExecute(pTMXTiledMap);

						//Attaches the layers, the NPCS, and animates the tiles
						for (int i = 0; i < pTMXTiledMap.getTMXLayers().size(); i++) {
							pTMXTiledMap.getTMXLayers().get(i).setVisible(false);//This is to prevent pop in
							pTMXTiledMap.getTMXLayers().get(i).setCullingEnabled(true);//This is supposed to not draw anything outside the camera range
							SceneManager.mWorldScene.attachChild(pTMXTiledMap.getTMXLayers().get(i));
						}		
						
						//Raise the top layer
						mNewTMXMapLoader.raiseTopLayer();

						//Start Animating tiles
						mNewTMXMapLoader.animateTiles();					
						
						//This attaches the player sprite to the scene
						mPlayerSprite.setAlpha(1.0f);	
						SceneManager.mWorldScene.attachChild(mPlayerSprite);								
						mPlayerSprite.setVisible(false);	
						
						//Allows additional control when TMXScene is called
						if(pOnExecute != null)
							pOnExecute.onFinish();												
					}
					
				}.execute();
			}
			
		});
	}

	public TMXScene(final EMap_ID pMapName, WorldActivity pMain) {
		this(pMapName, pMain, null);
	}
	
	//=======================================Getters And Setters =====================================
	
	public TMXmapLoader getTMXMapLoader(){
		return mNewTMXMapLoader;		
	}
}
