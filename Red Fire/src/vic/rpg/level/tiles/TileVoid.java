package vic.rpg.level.tiles;

import java.awt.Color;

import vic.rpg.level.Level;
import vic.rpg.level.Tile;
import vic.rpg.render.Render;
import vic.rpg.utils.Utils;

public class TileVoid extends Tile
{
	private Render render = new Render(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public TileVoid()
	{
		if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{
			render.g2d.setColor(Color.black);
			render.g2d.fillRect(0, 0, render.getWidth(), render.getHeight());
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
		return "The impassable void. It's black. And dark.";
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		return false;
	}
}
