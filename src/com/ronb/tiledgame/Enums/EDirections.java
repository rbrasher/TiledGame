package com.ronb.tiledgame.Enums;

import java.util.ArrayList;

import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;

import GameSprite.MyAnimatedSprite;

import com.ronb.tiledgame.WorldActivity;

public enum EDirections {
	
	UP, DOWN, LEFT, RIGHT, NONE;
	
	//return an array filled with directions for a path
	public static EDirections[] getDirectionToNextStepArray(Path pPath) {
		
		EDirections[] lDirectionArray = new EDirections[pPath.getSize()];
		
		for(int i = 0; i < pPath.getSize(); i++) {
			if(i + 1 < pPath.getSize()) {
				if(pPath.getCoordinatesX()[i + 1] - pPath.getCoordinatesX()[i] > 0) {
					//move to the right
					lDirectionArray[i] = RIGHT;
				} else if (pPath.getCoordinatesX()[i + 1] - pPath.getCoordinatesX()[i] < 0) {
					//move to the left
					lDirectionArray[i] = LEFT;
				} else if (pPath.getCoordinatesY()[i + 1] - pPath.getCoordinatesY()[i] > 0) {
					//move down
					lDirectionArray[i] = DOWN;
				} else if (pPath.getCoordinatesY()[i + 1] - pPath.getCoordinatesY()[i] < 0) {
					//move up
					lDirectionArray[i] = UP;
				} else {
					lDirectionArray[i] = NONE;
				}
			}
		}
		return lDirectionArray;
	}
	
	//return an array filled with directions for a path. This is a more accurate way to see which direction the sprite is going
	public static EDirections getDirectionToNextStep(Path pPath, TMXTile pTile) {
		
		if(pPath != null) {
			EDirections[] lDirectionArray = new EDirections[pPath.getSize()];
			
			for(int i = 0; i < pPath.getSize(); i++) {
				if(i + 1 < pPath.getSize()) {
					if(pPath.getCoordinatesX()[i + 1] - pPath.getCoordinatesX()[i] > 0) {
						//move to the right
						lDirectionArray[i] = RIGHT;
					} else if (pPath.getCoordinatesX()[i + 1] - pPath.getCoordinatesX()[i] < 0) {
						//move to the left
						lDirectionArray[i] = LEFT;
					} else if (pPath.getCoordinatesY()[i + 1] - pPath.getCoordinatesY()[i] > 0) {
						//move down
						lDirectionArray[i] = DOWN;
					} else if (pPath.getCoordinatesY()[i + 1] - pPath.getCoordinatesY()[i] < 0) {
						//move up
						lDirectionArray[i] = UP;
					} else {
						lDirectionArray[i] = NONE;
					}
				}
			}
			
			int Waypoint = 0;
			for(int j = 0; j < pPath.getSize(); j++) {
				if(pTile.getTileX() == pPath.getCoordinatesX()[j] && pTile.getTileY() == pPath.getCoordinatesY()[j]) {
					Waypoint = j;
				}
			}
			
			return lDirectionArray[Waypoint];
		} else {
			return NONE;
		}
	}
	
	//return an array filled with directions for a path
	public static EDirections[] getDirectionToNextStep(ArrayList<TMXTile> pTiles) {
		EDirections[] lDirectionArray = new EDirections[pTiles.size()];
		
		for(int i = 0; i < pTiles.size(); i++) {
			if(i + 1 < pTiles.size()) {
				if(pTiles.get(i + 1).getTileX() - pTiles.get(i).getTileX() > 0) {
					//move to the right
					lDirectionArray[i] = RIGHT;
				} else if (pTiles.get(i + 1).getTileX() - pTiles.get(i).getTileX() < 0) {
					//move to the left
					lDirectionArray[i] = LEFT;
				} else if (pTiles.get(i + 1).getTileY() - pTiles.get(i).getTileY() > 0) {
					//move down
					lDirectionArray[i] = DOWN;
				} else if (pTiles.get(i + 1).getTileY() - pTiles.get(i).getTileY() < 0) {
					//move up
					lDirectionArray[i] = UP;
				} else {
					lDirectionArray[i] = NONE;
				}
			}
		}
		return lDirectionArray;
	}
	
	//gets the direction the selected sprite is facing
	public static EDirections getSpriteDirection(MyAnimatedSprite pSprite) {
		int DTiles[] = {WorldActivity.DOWN_CENTER_TILE - 1, WorldActivity.DOWN_CENTER_TILE, WorldActivity.DOWN_CENTER_TILE + 1};
		
		int RTiles[] = {WorldActivity.RIGHT_CENTER_TILE - 1, WorldActivity.RIGHT_CENTER_TILE, WorldActivity.RIGHT_CENTER_TILE + 1};
		
		int UTiles[] = {WorldActivity.UP_CENTER_TILE - 1, WorldActivity.UP_CENTER_TILE, WorldActivity.UP_CENTER_TILE + 1};
		
		int LTiles[] = {WorldActivity.LEFT_CENTER_TILE - 1, WorldActivity.LEFT_CENTER_TILE, WorldActivity.LEFT_CENTER_TILE + 1};
		
		final int lTileIndex = pSprite.getCurrentTileIndex();
		
		if(lTileIndex >= DTiles[0] && lTileIndex <= DTiles[2]) {
			return DOWN;
		} else if (lTileIndex >= RTiles[0] && lTileIndex <= RTiles[2]) {
			return RIGHT;
		} else if (lTileIndex >= UTiles[0] && lTileIndex <= UTiles[2]) {
			return UP;
		} else if (lTileIndex >= LTiles[0] && lTileIndex <= LTiles[2]) {
			return LEFT;
		} else {
			return NONE;
		}
	}
	
	/**
	 * Get the direction from the sprite location to the tile
	 * @param pTile
	 * @param pSprite
	 * @return
	 */
	public static EDirections getDirectionToNextTile(TMXTile pTile, MyAnimatedSprite pSprite) {
		EDirections lDirection = NONE;
		
		if(pTile.getTileX() - pSprite.getX() > 0) {
			//move to the right
			lDirection = RIGHT;
		} else if (pTile.getTileX() - pSprite.getX() < 0) {
			//move to the left
			lDirection = LEFT;
		} else if (pTile.getTileY() - pSprite.getY() > 0) {
			//move down
			lDirection = DOWN;
		} else if (pTile.getTileY() - pSprite.getY() < 0) {
			//move up
			lDirection = UP;
		} else {
			lDirection = NONE;
		}
		return lDirection;
	}
}
