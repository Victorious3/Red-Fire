package vic.rpg.level.tiles;

import vic.rpg.level.Tile;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Drawable;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

public class TileWater extends Tile 
{	
	public TileWater() 
	{
		if(Utils.getSide().contains(Utils.SIDE_CLIENT)) drawable.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_TERRAIN_WATER));		
	}

	public int tickCount = 0;
	
	@Override
	public double getMovementCost() 
	{
		return 5;
	}
	
	@Override
	public void tick(int x, int y, int data)
	{	
		drawable.setTexture(RenderRegistry.anim_water.getCurrentTexture());
	}

	@Override
	public Drawable getDrawable(int x, int y, int data) 
	{
		return drawable;
	}
	
	@Override
	public String getDescription() 
	{
		return "Nice blue ocean.";
	}
}
