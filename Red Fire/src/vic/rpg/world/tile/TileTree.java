package vic.rpg.world.tile;

import java.awt.Point;

import vic.rpg.world.Map;

public class TileTree extends Tile
{
	@Override
	public String getDescription() 
	{
		return "A tree.";
	}

	@Override
	public boolean isWalkingPermitted(int x, int y, int layerID, Map map) 
	{
		return false;
	}

	@Override
	public Point getTextureCoord(int x, int y, int data, int layerID, Map map) 
	{
		return new Point(0, 15);
	}

	@Override
	public int getHeight(int x, int y, int data, int layerID, Map map) 
	{
		return 3;
	}	
}
