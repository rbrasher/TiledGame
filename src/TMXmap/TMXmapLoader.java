package TMXmap;

import java.util.ArrayList;
import java.util.HashMap;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXObjectGroupProperty;
import org.andengine.extension.tmx.TMXObjectProperty;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import Scene.SceneManager.SceneManager;

import com.ronb.tiledgame.WorldActivity;
import com.ronb.tiledgame.Enums.EMap_ID;

/**
 * The purpose of this class is to create a TMX map
 * 
 * @author ron
 *
 */
public class TMXmapLoader {
	public static final String EXIT = "EXIT";
	public static final String LINK = "LINK";
	public static final String SPAWN = "SPAWN";
	public static final String ALPHA_LAYER = "Alpha Layer";
	private WorldActivity mContext;
	private TMXLoader TMXloader;
	private TMXTiledMap mTMXTiledMap;
	private ArrayList<TMXObject> mTMXObjects;
	private TimerHandler mAnimateTimerHandler;
	protected ArrayList<TMXTile> mAnimationTiles;
	private EMap_ID mMapName;
	protected ArrayList<TMXTile> mTopTiles;
	private ArrayList<TMXTile> mChangingTiles;
	private ArrayList<TMXTile> mCollideTiles;
	private ArrayList<TMXTile> mExitTiles;
	private ArrayList<TMXObjectGroup> TMXGroupObjects;
	
	/**
	 * This loads the map that will be used in {@link TMXScene} 
	 * @param pMapName
	 * @param pContext
	 */
	public TMXmapLoader(EMap_ID pMapName, WorldActivity pContext){
		mContext = pContext;
		mMapName = pMapName;
		mAnimationTiles = new ArrayList<TMXTile>();
		mChangingTiles = new ArrayList<TMXTile>();
		String fileName = getMapFileName();

		try {
			this.TMXloader = new TMXLoader(mContext.getAssets(), mContext.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, mContext.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					//Gets the properties of the tiles from every layer
					for (int i = 0; i < pTMXTileProperties.size(); i++) 
						//Gets all the tiles with the property "ANIMATE" that is on the TMXLayer called "Alpha Layer"
						if(pTMXTileProperties.get(i).getName().contentEquals("ANIMATE") && pTMXLayer.getName().contentEquals("Alpha Layer")) 
							mAnimationTiles.add(pTMXTile);		
				}	
				
			});

			this.mTMXTiledMap = TMXloader.loadFromAsset(fileName);

		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		
		mTMXObjects = new ArrayList<TMXObject>();
		TMXGroupObjects = new ArrayList<TMXObjectGroup>();
		//Get the list of object tiles
		for (final TMXObjectGroup pGroup : mTMXTiledMap.getTMXObjectGroups()) {
			mTMXObjects.addAll(pGroup.getTMXObjects());
			TMXGroupObjects.add(pGroup); 
		}
		
		//Get the collision, ext, and changing tiles from the object sets on the map
		mCollideTiles = this.getObjectGroupPropertyTiles("COLLIDE",  TMXGroupObjects);	
		mExitTiles = this.getObjectPropertyTiles("EXIT", mTMXObjects);
		mChangingTiles = this.getObjectGroupPropertyTiles("CHANGE", TMXGroupObjects);
	}
	
	
	//=====================================================Getters and setters=====================================================
	public TMXTiledMap getTMXMap(){
		return this.mTMXTiledMap;
	}
		
	public TMXLayer getTMXMapLayer(int pLayer){
		return mTMXTiledMap.getTMXLayers().get(pLayer);
	}
		
	/*
	 * This gets the file name of the map with the specified area number
	 */
	private String getMapFileName(){
		switch (mMapName) {
		case DESERT1:
			return "tmx/desert.tmx";

		case DESERT2:
			return "tmx/desert2.tmx";
			
		default:
			throw new Error("No TMX file found!. Check the map name.");
		}		
	}

	public ArrayList<TMXTile> getCollideTiles(){
		return mCollideTiles;		
	}
	
	public ArrayList<TMXTile> getExitTiles(){
		return mExitTiles;		
	}

	public ArrayList<TMXTile> getChangingTileArray(){
		return mChangingTiles;		
	}
	

	//=============================================Get the tile properties===========================================
	/*
	 * Gets the the tile properties that were stored in arrays
	 */
	public EMap_ID getNextMapIndexFromTile(TMXTile pTMXTile) {
		//The tile location of pTMXTile
		float lTileX = pTMXTile.getTileX();
		float lTileY = pTMXTile.getTileY();	
		//Iterates through the objects
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties 
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(EXIT)) {	
					//Coordinates at the center of the tile
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
					
					//Cycles through the tiles that the object covers
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							//Log.i("TMXmapLoader", "lObjectTileX: " + lObjectTileX + " lObjectTileY: " + lObjectTileY);
							//Log.i("TMXmapLoader", "ObjectX: " + ObjectX + " ObjectY: " + ObjectY);
							//Checks if the chosen tile is an exit tile	
							if(lObjectTileX == lTileX && lObjectTileY == lTileY){
								return EMap_ID.valueOf(pObjectProperties.getValue());
							}
						}
					}			
				}
			}
			
		}
		return EMap_ID.NONE;
	}
	
	public EMap_ID getMapIndex() {
		return EMap_ID.valueOf(mTMXTiledMap.getTMXTiledMapProperties().get(0).getValue());
	}
	
	public boolean isExitTile(TMXTile pTMXTile) {
		float lTileX = pTMXTile.getTileX();
		float lTileY = pTMXTile.getTileY();
		// The for loop cycles through each object on the map
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains("EXIT")) {	
	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
				
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lExitTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lExitTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							//Checks if the chosen tile is an exit tile
							if(lExitTileX == lTileX && lExitTileY == lTileY){
								return true;
							}							 
						}						
					}

				}
			}

		}
		return false;
	}
	
	public String getExitLink(TMXTile pTMXTile) {
		float lTileX = pTMXTile.getTileX();
		float lTileY = pTMXTile.getTileY();	
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(LINK)) {	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
				
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;

							if(lObjectTileX == lTileX && lObjectTileY == lTileY){
								return pObjectProperties.getValue();
							}							 
						}
					}
				}
			}
		}
		return "NONE";
	}
	
	public String getPlayerSpawnDirection(TMXTile pTMXTile) {
		float lTileX = pTMXTile.getTileX();
		float lTileY = pTMXTile.getTileY();	
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(SPAWN)) {	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
					
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							
							if(lObjectTileX == lTileX && lObjectTileY == lTileY){
								return pObjectProperties.getValue();
							}
						}							 
					}
				}			
			}
		}
		return "NONE";
	}
	
	/*
	 * Returns an array of tiles that have the specified object properties
	 */
	public ArrayList<TMXTile> getObjectPropertyTiles(String pName, ArrayList<TMXObject> pTMXObjects){
		ArrayList<TMXTile> ObjectTile = new ArrayList<TMXTile>();
		for (final TMXObject pObjectTiles : pTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(pName)) {	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
					
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							ObjectTile.add(mTMXTiledMap.getTMXLayers().get(0).getTMXTileAt(lObjectTileX, lObjectTileY));						
						}							 
					}
				}			
			}
		}
		return ObjectTile;
	}
	
	public ArrayList<TMXTile> getObjectGroupPropertyTiles(String pName, ArrayList<TMXObjectGroup> pTMXObjectGroups){
		ArrayList<TMXTile> ObjectTile = new ArrayList<TMXTile>();
		for (final TMXObjectGroup pObjectGroups : pTMXObjectGroups) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectGroupProperty pGroupProperties : pObjectGroups.getTMXObjectGroupProperties()) {
				//Sees if any of the elements have this condition
				if (pGroupProperties.getName().contains(pName)) {
					for (final TMXObject pObjectTiles : pObjectGroups.getTMXObjects()) {
						int ObjectX = pObjectTiles.getX();
						int ObjectY = pObjectTiles.getY();
						// Gets the number of rows and columns in the object
						int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
						int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
						
						for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
							for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
								float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
								float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
								ObjectTile.add(mTMXTiledMap.getTMXLayers().get(0).getTMXTileAt(lObjectTileX, lObjectTileY));						
							}							 
						}
					}
				}			
			}
		}
		return ObjectTile;
	}
	
	public ArrayList<TMXTile> getObjectGroupPropertyTiles(String pName, final int pLayer, ArrayList<TMXObjectGroup> pTMXObjectGroups){
		ArrayList<TMXTile> ObjectTile = new ArrayList<TMXTile>();
		for (final TMXObjectGroup pObjectGroups : pTMXObjectGroups) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectGroupProperty pGroupProperties : pObjectGroups.getTMXObjectGroupProperties()) {
				//Sees if any of the elements have this condition
				if (pGroupProperties.getName().contains(pName)) {
					for (final TMXObject pObjectTiles : pObjectGroups.getTMXObjects()) {
						int ObjectX = pObjectTiles.getX();
						int ObjectY = pObjectTiles.getY();
						// Gets the number of rows and columns in the object
						int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
						int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
						
						for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
							for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
								float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
								float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
								ObjectTile.add(mTMXTiledMap.getTMXLayers().get(pLayer).getTMXTileAt(lObjectTileX, lObjectTileY));						
							}							 
						}
					}
				}			
			}
		}
		return ObjectTile;
	}
	
	public HashMap<TMXTile, String> getObjectPropertyMap(String pName){
		HashMap<TMXTile, String> ObjectTile = new HashMap<TMXTile, String>();
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(pName)) {	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
					
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							ObjectTile.put(mTMXTiledMap.getTMXLayers().get(0).getTMXTileAt(lObjectTileX, lObjectTileY), pObjectProperties.getValue());						
						}							 
					}
				}			
			}
		}
		return ObjectTile;
	}
	
	public HashMap<String, TMXTile> getObjectPropertyMapReversed(String pName){
		HashMap<String, TMXTile> ObjectTile = new HashMap<String, TMXTile>();
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(pName)) {	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
					
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							ObjectTile.put(pObjectProperties.getValue(), mTMXTiledMap.getTMXLayers().get(0).getTMXTileAt(lObjectTileX, lObjectTileY));						
						}							 
					}
				}			
			}
		}
		return ObjectTile;
	}
	
	public ArrayList<TMXTile> getObjectPropertyTiles(String pName, String pValue){
		ArrayList<TMXTile> ObjectTile = new ArrayList<TMXTile>();
		for (final TMXObject pObjectTiles : mTMXObjects) {
			// Iterates through the properties and assigns them to the new variable
			for (final TMXObjectProperty pObjectProperties : pObjectTiles.getTMXObjectProperties()) {
				//Sees if any of the elements have this condition
				if (pObjectProperties.getName().contains(pName) && pObjectProperties.getValue().contains(pValue)) {	
					int ObjectX = pObjectTiles.getX();
					int ObjectY = pObjectTiles.getY();
					// Gets the number of rows and columns in the object
					int ObjectRows = pObjectTiles.getHeight() / WorldActivity.TILE_HEIGHT;
					int ObjectColumns = pObjectTiles.getWidth() / WorldActivity.TILE_WIDTH;
					
					for (int TileRow = 0; TileRow < ObjectRows; TileRow++) {
						for (int TileColumn = 0; TileColumn < ObjectColumns; TileColumn++) {
							float lObjectTileX = ObjectX + TileColumn * WorldActivity.TILE_WIDTH;
							float lObjectTileY = ObjectY + TileRow * WorldActivity.TILE_HEIGHT;
							ObjectTile.add(mTMXTiledMap.getTMXLayers().get(0).getTMXTileAt(lObjectTileX, lObjectTileY));						
						}							 
					}
				}			
			}
		}
		return ObjectTile;
	}
	
	public ArrayList<TMXObject> getObjectTiles() {
		return mTMXObjects;
	}
	
	
	//==========================================Void methods====================================================
	/**
	 * Register the animating tiles
	 */
	public void animateTiles(){
		mAnimateTimerHandler = new TimerHandler(WorldActivity.ANIMATE_TILE_DURATION , true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {				
				for (int i = 0; i < mTMXTiledMap.getTMXLayers().size(); i++) {
					
					final TMXLayer lTMXLayer = mTMXTiledMap.getTMXLayers().get(i);
					
					if(lTMXLayer.getName().contentEquals("Alpha Layer")){
						for (int j = 0; j < mAnimationTiles.size(); j++) {
							for (int k = 0; k < mAnimationTiles.get(j).getTMXTileProperties(mTMXTiledMap).size(); k++) {
								//The value of the ANIMATE property is the next tile in the sequence
								if(mAnimationTiles.get(j).getTMXTileProperties(mTMXTiledMap).get(k).getName().contentEquals("ANIMATE")){
									
							        final TMXTile lAnimateTile = mAnimationTiles.get(j);
									//sets the ID to the next tile in that sequence
							        lAnimateTile.setGlobalTileID(mTMXTiledMap, Integer.parseInt(lAnimateTile.getTMXTileProperties(mTMXTiledMap).get(k).getValue()));
																               
							        final int lTileHeight = mTMXTiledMap.getTileHeight();
							        final int lTileWidth = mTMXTiledMap.getTileWidth();           
							        //See TMXLayer Class line 308 (getSpriteBatchIndex)
							        lTMXLayer.setIndex(lAnimateTile.getTileRow() * mTMXTiledMap.getTileColumns() + lAnimateTile.getTileColumn());
							        lTMXLayer.drawWithoutChecks(lAnimateTile.getTextureRegion(), lAnimateTile.getTileX(), lAnimateTile.getTileY(), lTileWidth, lTileHeight, Color.WHITE_ABGR_PACKED_FLOAT);     
									//This alters the tile texture
									mTMXTiledMap.getTMXLayers().get(i).submit();								
								}
							}
						}
						
					}				
				}				
			}
		});
		//Animate tiles in equal intervals 
		SceneManager.mWorldScene.registerUpdateHandler(mAnimateTimerHandler);
	}
	
	public void stopAnimaingTiles(){
		if(mAnimateTimerHandler != null)
			SceneManager.mWorldScene.unregisterUpdateHandler(mAnimateTimerHandler);
	}
	
	/**
	 * If there is a top layer then bring it to the top
	 */
	public void raiseTopLayer(){
		for (int i = 0; i < mTMXTiledMap.getTMXLayers().size(); i++) {
			if(mTMXTiledMap.getTMXLayers().get(i).getName().contentEquals("Top Layer")){
				mTMXTiledMap.getTMXLayers().get(i).setZIndex(20);
			}
		}
		//sort children
		SceneManager.mWorldScene.sortChildren();		
	}
}
