package vic.rpg.level.tiles;

import vic.rpg.level.Level;
import vic.rpg.level.Tile;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Render;
import vic.rpg.utils.Utils;

public class TileGrass extends Tile 
{
	public Render renderFlowers = new Render(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public TileGrass()
	{
		if(Utils.getSide().contains(Utils.SIDE_CLIENT)) 
		{
			render.drawImage(RenderRegistry.IMG_TERRAIN_GRASS, 0, 0, render.getWidth(), render.getHeight());
			renderFlowers.drawImage(RenderRegistry.IMG_TERRAIN_GRASS_2, 0, 0, render.getWidth(), render.getHeight());
		}
	}
	
	@Override
	public String getDescription() 
	{
		return "Just some standard grass. Data = 1 creates flowers!";
	}

	@Override
	public Render getRender(int x, int y, int data) 
	{
		if(data == 1) return renderFlowers;
		else return render;
	}
}
