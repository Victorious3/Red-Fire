package vic.rpg.level.tiles;

import java.awt.Point;

import vic.rpg.level.Tile;

public class TileTerrain extends Tile 
{	
	@Override
	public String getDescription() 
	{
		return "Standart Terrain Tile";
	}

	@Override
	public Point getTextureCoord(int x, int y, int data) 
	{
		return new Point((int) (((double)(data / 16) - (int)(data / 16)) / 10D * 16), (int)(data / 16));
	}	
}
