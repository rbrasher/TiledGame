package Util;

import java.util.ArrayList;

import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;

import com.ronb.tiledgame.WorldActivity;

public class PathUtil {

	public static ArrayList<TMXTile> getPathTiles(Path pPath, TMXLayer pTMXLayer) {
		final ArrayList<TMXTile> lPathTiles = new ArrayList<TMXTile>();
		for(int i = 0; i < pPath.getSize() - 1; i++) {
			TMXTile P1 = pTMXLayer.getTMXTileAt(pPath.getCoordinatesX()[i], pPath.getCoordinatesY()[i]);
			TMXTile P2 = pTMXLayer.getTMXTileAt(pPath.getCoordinatesX()[i + 1], pPath.getCoordinatesY()[i + 1]);
			//add the first tile in the segment to the list of tiles if it is part of the first segment
			if(i == 0)
				lPathTiles.add(P1);		//initial location
			//get the tiles in between the two points
			int TileNumberX = ((int)pPath.getCoordinatesX()[i + 1] - (int)pPath.getCoordinatesX()[i] / WorldActivity.TILE_WIDTH);
			int TileNumberY = ((int)pPath.getCoordinatesY()[i + 1] - (int)pPath.getCoordinatesY()[i] / WorldActivity.TILE_HEIGHT);
			//adds a negative multiplier if the sprite is moving left or up
			int DirectionMult = 1;
			if(TileNumberX < 0 || TileNumberY < 0)
				DirectionMult = -1;
			//the tiles will change in X or Y, not both
			if(Math.abs(TileNumberX) > 1) {
				//the lower bound is increased by one and the upper bound is decreased by one because we don't care about the first and last tile in the sequence
				//since they are already included
				for(int j = 0; j < Math.abs(TileNumberX); j++) {
					lPathTiles.add(pTMXLayer.getTMXTileAt(P1.getTileX() + WorldActivity.TILE_WIDTH * j * DirectionMult, P1.getTileY()));
				}
			} else if(Math.abs(TileNumberY) > 1) {
				for(int j = 0; j < Math.abs(TileNumberY); j++) {
					lPathTiles.add(pTMXLayer.getTMXTileAt(P1.getTileX(), P1.getTileY() + WorldActivity.TILE_HEIGHT * j * DirectionMult));
				}
			}
			//add the last tile in the segment to the list of tiles
			lPathTiles.add(P2);
		}
		return lPathTiles;
	}
	
	public static Path getPathFromTiles(ArrayList<TMXTile> pTiles) {
		final Path lPath = new Path(pTiles.size());
		for(TMXTile iTMXTile : pTiles) {
			lPath.to(iTMXTile.getTileX(), iTMXTile.getTileY());
		}
		return lPath;
	}
}
