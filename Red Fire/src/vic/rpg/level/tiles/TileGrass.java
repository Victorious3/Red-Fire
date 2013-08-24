package vic.rpg.level.tiles;

import vic.rpg.level.Level;
import vic.rpg.level.Tile;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Drawable;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

public class TileGrass extends Tile 
{
	public Drawable drawableFlowers = new Drawable(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public TileGrass()
	{
		if(Utils.getSide().contains(Utils.SIDE_CLIENT)) 
		{
			drawable.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_TERRAIN_GRASS));
			drawableFlowers.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_TERRAIN_GRASS_2));
		}
	}
	
	@Override
	public String getDescription() 
	{
		return "Just some standard grass. Data = 1 creates flowers!";
	}

	@Override
	public Drawable getDrawable(int x, int y, int data) 
	{
		if(data == 1) return drawableFlowers;
		else return drawable;
	}
}
