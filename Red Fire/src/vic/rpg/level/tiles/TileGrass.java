package vic.rpg.level.tiles;

import vic.rpg.level.Tile;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Render;
import vic.rpg.utils.Utils;

public class TileGrass extends Tile 
{
	public TileGrass()
	{
		if(Utils.getSide().contains(Utils.SIDE_CLIENT)) render.drawImage(RenderRegistry.IMG_TERRAIN_GRASS, 0, 0, render.getWidth(), render.getHeight());
	}
	
	@Override
	public Render getRender(int x, int y, int data) 
	{
		return render;
	}

	@Override
	public void tick(int x, int y, int data) 
	{
		
	}
}
