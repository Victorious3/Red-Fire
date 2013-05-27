package vic.rpg.level.tiles;

import vic.rpg.level.Tile;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Render;
import vic.rpg.utils.Utils;

public class TileWater extends Tile 
{	
	public TileWater() 
	{
		if(Utils.getSide().contains(Utils.SIDE_CLIENT)) render.drawImage(RenderRegistry.IMG_TERRAIN_WATER, 0, 0, render.getWidth(), render.getHeight());		
	}

	public int tickCount = 0;
	
	@Override
	public void tick(int x, int y, int data)
	{
		if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{	
			if(RenderRegistry.anim_water.hasUpdated)
			{
				render.g2d.drawImage(RenderRegistry.anim_water.currImage, 0, 0, null);
			}
		}	
	}

	@Override
	public Render getRender(int x, int y, int data) 
	{
		return render;
	}
	
	@Override
	public String getDescription() 
	{
		return "Nice blue ocean.";
	}
}
