package vic.rpg.world.tiles;

import java.awt.Point;

public class TileTree extends Tile
{
	@Override
	public String getDescription() 
	{
		return "A tree.";
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		return false;
	}

	@Override
	public Point getTextureCoord(int x, int y, int data) 
	{
		return new Point(0, 15);
	}

	@Override
	public int getHeight(int x, int y, int data) 
	{
		return 3;
	}	
}
