package Sequences;

import java.util.ArrayList;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseCubicIn;

import GameSprite.MyAnimatedSprite;
import Scene.SceneManager.SceneManager;
import TMXmap.TMXScene;
import TMXmap.TMXmapLoader;
import Util.FadeScreen;
import android.opengl.GLES20;
import android.util.Log;

import com.ronb.tiledgame.WorldActivity;
import com.ronb.tiledgame.Enums.EDirections;
import com.ronb.tiledgame.Enums.EMap_ID;

/**
 * This sequence controls the switching of the TMX map
 * 
 * @author ron
 *
 */
public class TMXMapSwitchSequence {
	private static final String TAG = "TMXMapSwitchSequence";
	private TimeLineHandler TMXSwitchSequence;
	private TMXmapLoader mTMXLoader;
	private TMXTile mExitTile;
	private boolean mDoorTile;
	private MyAnimatedSprite mPlayerSprite;
	private WorldActivity mContext;

	
	public TMXMapSwitchSequence(TMXmapLoader pTMXLoader, TMXTiledMap pTMXMap, TMXTile pExitTile, final WorldActivity pMain){
		mPlayerSprite = SceneManager.mWorldScene.getPlayerSprite();
		mTMXLoader = pTMXLoader;
		mExitTile = pExitTile;
		mContext = pMain;		
		
		//Disables the touch controls
		if(WorldActivity.TOUCH_ENABLED)
			SceneManager.mWorldScene.setOnSceneTouchListener(null);
		else
			SceneManager.mWorldScene.disableUI();
		//Checks to see if the exit tile was a door or not
		Log.i(TAG, "pExitTile:  " + pExitTile.getTMXTileProperties(pTMXMap));
		Log.i(TAG, "pExitTile2: " + pExitTile.getTileColumn());
		Log.i(TAG, "pTMXMap:    " + pTMXMap);
		mDoorTile = false;
		if(pExitTile.getTMXTileProperties(pTMXMap) != null){//null check
			if(pExitTile.getTMXTileProperties(pTMXMap).containsTMXProperty("DOOR", "true"))
				mDoorTile = true;
		}		
		//Evolution timeline Ilm.Summoner.Handler
		TMXSwitchSequence = new TimeLineHandler(TAG){
			@Override
			public void onFinish() {
				if(WorldActivity.TOUCH_ENABLED)
					SceneManager.mWorldScene.setOnSceneTouchListener(pMain);
				else
					SceneManager.mWorldScene.enableUI();
				TMXSwitchSequence.deleteTimeline();//This finishes the timeline			
			}			
		};	

		TimeLineItem Item0 = AnimateDoorOpen();
		TimeLineItem Item1 = WalkIn();
		TimeLineItem Item2 = AnimateDoorClose();
		TimeLineItem Item3 = SpawnPlayer();
		
		TMXSwitchSequence.add(Item0);
		TMXSwitchSequence.add(Item1);
		TMXSwitchSequence.add(Item2);
		TMXSwitchSequence.add(Item3);
		TMXSwitchSequence.start();		
		
	}
	
	//========================================TimeLine Items======================================================
	/*
	 * The first step is to animate the door if there is one
	 */
	private TimeLineItem AnimateDoorOpen(){
		TimeLineItem Item = new TimeLineItem(){

			@Override
			public void Procedure() {
				//TODO:Do the animation at the exit tile, Make sure the sprite faces the right way(face the opposite of the spawn direction)
				if(mDoorTile == true){
					//TODO: Play door sound					
				}
				TMXSwitchSequence.unpauseTimeline();
			}			
		};
		return Item;
	}
	
	/*
	 * Close door animation and fade out
	 */
	private TimeLineItem AnimateDoorClose(){
		TimeLineItem Item = new TimeLineItem(){

			@Override
			public void Procedure() {
				//TODO:Do the animation at the exit tile
				if(mDoorTile == true){
					//TODO: Play door sound					
				}
				//Fade out 
				new FadeScreen(SceneManager.mWorldScene, mContext.mMainCurtain, WorldActivity.mWorldCamera).FadeOut(WorldActivity.FADE_DURATION, new OnStatusUpdateListener(){

					@Override
					public void onFinish() {
						TMXSwitchSequence.unpauseTimeline();
					}
					
				});
			}
			
		};
		return Item;
	}
	
	/*
	 * Walk the player sprite in and fade him out
	 */
	private TimeLineItem WalkIn(){
		TimeLineItem Item = new TimeLineItem(){

			@Override
			public void Procedure() {				
				float lStartX = mPlayerSprite.getX();
				float lStartY = mPlayerSprite.getY();
				float lFinalX = mExitTile.getTileX();
				float lFinalY = mExitTile.getTileY();
				
				//Sets the path the Player will walk
				Path lPath = new Path(2).to(lStartX, lStartY).to(lFinalX, lFinalY);
				final float TRAVEL_SPEED = WorldActivity.WALKING_SPEED;	
				mPlayerSprite.setPath(lPath);
				
				//Fade the player sprite variables
				final float FadeDuration = TRAVEL_SPEED;
				mPlayerSprite.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				
				mPlayerSprite.registerEntityModifier(new ParallelEntityModifier(new PathModifier(TRAVEL_SPEED, lPath), 
						new AlphaModifier(FadeDuration, 1.0f, 0, new IEntityModifierListener() {
							
							@Override
							public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
								//TODO: fade music out
								
							}
							
							@Override
							public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
								TMXSwitchSequence.unpauseTimeline();
							}
						}, EaseCubicIn.getInstance())
				));							
			}			
		};
		return Item;
	}

	/*
	 * Spawn the player
	 */
	private TimeLineItem SpawnPlayer(){
		TimeLineItem Item = new TimeLineItem(){

			@Override
			public void Procedure() {
				WorldActivity.mWorldEngine.runOnUpdateThread(new Runnable() {
					private TMXScene mTMXScene;

					@Override
					public void run() {
						//Clear up the scene
						//WorldActivity.mWorldScene.clearEntityModifiers();
						SceneManager.mWorldScene.detachChildren();
						for (int i = 0; i < SceneManager.mWorldScene.getChildCount(); i++) {
							//Log.i("TMX Sequence", "CLASS NAME: " + WorldActivity.mWorldScene.getChild(i).getClass().getName());
							if(SceneManager.mWorldScene.getChildByIndex(i).getClass().getName().contains("TMXLayer")){
								TMXLayer TempLayer = (TMXLayer) SceneManager.mWorldScene.getChildByIndex(i);
								TempLayer.dispose();
							}
						}
						EMap_ID lMap_ID = mTMXLoader.getNextMapIndexFromTile(mExitTile);

						Log.i(TAG, "lMap_ID: " + lMap_ID);
						mTMXScene = new TMXScene(lMap_ID, mContext, new OnStatusUpdateListener(){							
							
							@Override
							public void onStart() {
								super.onStart();
								//Stop animating the previous tiles
								mTMXLoader.stopAnimaingTiles();
							}

							@Override
							public void onFinish() {
								final TMXmapLoader lNewMapLoader = mTMXScene.getTMXMapLoader();

								//Get the exit link value 
								String lLastExitLink = mTMXLoader.getExitLink(mExitTile);
								
								//Move the player to the proper exit tile
								ArrayList<TMXTile> lExitTiles = lNewMapLoader.getObjectPropertyTiles(TMXmapLoader.EXIT, mTMXLoader.getMapIndex().toString());//This gets the tiles that go to the previous map
								TMXTile lPlayerSpawnTile = null;

								//Log.i(TAG, "mTMXLoader.getMapIndex().toString()    " + mTMXLoader.getMapIndex().toString());
								for (int i = 0; i < lExitTiles.size(); i++) {
									//This picks the tile that goes to the specific exit tile.Checks if the new link value equals the previous exit tile's link value
									if(lNewMapLoader.getExitLink(lExitTiles.get(i)).contentEquals(lLastExitLink))
										lPlayerSpawnTile = lExitTiles.get(i);									
									//Log.i(TAG, "lNewMapLoader.getExitLink(lExitTiles.get(i)) " + lNewMapLoader.getExitLink(lExitTiles.get(i)));
								}		
								//Log.i(TAG, "lLastExitLink    " + lLastExitLink);
								if(lPlayerSpawnTile == null)
									throw new Error("No Player Spawn tile was chosen!");
								
								
								//Set the player position to the spawn tile. 
								mPlayerSprite.setPosition(lPlayerSpawnTile.getTileX(), lPlayerSpawnTile.getTileY());
								WorldActivity.mWorldCamera.setCenter(lPlayerSpawnTile.getTileX(), lPlayerSpawnTile.getTileY());
								
								//Move the curtain to the player spawn location
								mContext.mMainCurtain.setPosition(mPlayerSprite.getX() - mContext.mMainCurtain.getWidth()/2, mPlayerSprite.getY() - mContext.mMainCurtain.getHeight()/2); //Move the curtain
								mContext.mMainCurtain.setZIndex(1000);						
								
								//Get the movement direction and the player spawn tile
								final String lSpawnDirection = lNewMapLoader.getPlayerSpawnDirection(lPlayerSpawnTile);
								final int lSpawnX = lPlayerSpawnTile.getTileColumn() * WorldActivity.TILE_WIDTH;
								final int lSpawnY = lPlayerSpawnTile.getTileRow() * WorldActivity.TILE_HEIGHT;
								//Tile player will walk from
								final TMXTile lStartTile = lPlayerSpawnTile;
								
								
								//Fade in and walk to the next tile
								new FadeScreen(SceneManager.mWorldScene, mContext.mMainCurtain, lStartTile).FadeIn( WorldActivity.FADE_DURATION, new OnStatusUpdateListener(){

									private EDirections lSpriteDirection;

									@Override
									public void onStart() {
										//TODO: Fade new music in
										
										//Show all the children on the scene 
										SceneManager.mWorldScene.setChildrenVisible(true);	
										
										int lFinalX = lSpawnX;
										int lFinalY = lSpawnY;
										//Walk to next tile
										if(lSpawnDirection.contentEquals("up")){
											lFinalY = lFinalY - WorldActivity.TILE_HEIGHT;
											lSpriteDirection = EDirections.UP;
										}else if(lSpawnDirection.contentEquals("down")){
											lFinalY = lFinalY + WorldActivity.TILE_HEIGHT;
											lSpriteDirection = EDirections.DOWN;
										}else if(lSpawnDirection.contentEquals("left")){
											lFinalX = lFinalX - WorldActivity.TILE_WIDTH;
											lSpriteDirection = EDirections.LEFT;
										}else if(lSpawnDirection.contentEquals("right")){
											lFinalX = lFinalX + WorldActivity.TILE_WIDTH;
											lSpriteDirection = EDirections.RIGHT;
										}else{
											throw new Error("No final tile was chosen!");
										}
										
										//Sets the path the Player will walk
										Path lPath = new Path(2).to(lSpawnX, lSpawnY).to(lFinalX, lFinalY);
										final float TRAVEL_SPEED = WorldActivity.WALKING_SPEED;  
										mPlayerSprite.registerEntityModifier(new PathModifier(TRAVEL_SPEED, lPath, new IEntityModifierListener() {
											
											@Override
											public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
												
											}
											
											@Override
											public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
												mPlayerSprite.setPath(null);
												
											}
										}));
										mPlayerSprite.setPath(lPath);										
										
									}

									@Override
									public void onFinish() {
										TMXSwitchSequence.unpauseTimeline();
									}
									
								});
							}					
						});
					}
				});					
			}			
		};
		return Item;
	}
}
