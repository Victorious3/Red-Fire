package vic.rpg.level;

import vic.rpg.level.tiles.Tile;
import vic.rpg.level.tiles.TileTerrain;

/**
 * Used by the Editor to mark {@link Tile Tiles} that support the direct picking of a texture like {@link TileTerrain}.
 * @author Victorious3
 */
public interface TexturePath 
{
	String getTexturePath();
}
