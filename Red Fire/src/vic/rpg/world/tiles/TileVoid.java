package vic.rpg.world.tiles;

import java.awt.Point;

import vic.rpg.world.Map;

public class TileVoid extends Tile
{	
	@Override
	public String getDescription() 
	{
		return "The impassable void. It's black. And dark.";
	}

	@Override
	public boolean isWalkingPermitted(int x, int y, int layerID, Map map) 
	{
		return false;
	}

	@Override
	public Point getTextureCoord(int x, int y, int data, int layerID, Map map) 
	{	
		return new Point(4, 5);
	}
}
