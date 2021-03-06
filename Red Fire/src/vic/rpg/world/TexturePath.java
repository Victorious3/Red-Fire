package vic.rpg.world;

import vic.rpg.world.tile.Tile;
import vic.rpg.world.tile.TileTerrain;

/**
 * Used by the Editor to mark {@link Tile Tiles} that support the direct picking of a texture like {@link TileTerrain}.
 * @author Victorious3
 */
public interface TexturePath 
{
	String getTexturePath();
}
