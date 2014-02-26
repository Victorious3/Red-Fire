package vic.rpg.level;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import vic.rpg.level.tiles.Tile;
import vic.rpg.level.tiles.TileTerrain;

/**
 * Used by the Editor to mark {@link Tile Tiles} that support the direct picking of a texture like {@link TileTerrain}.
 * @author Victorious3
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TexturePath 
{
	String path();
}
