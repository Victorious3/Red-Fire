package vic.rpg.level.tiles;

import java.awt.Point;

import vic.rpg.level.TexturePath;
import vic.rpg.level.Tile;

@TexturePath(path = "/vic/rpg/resources/terrain/terrain.png")
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
		int xCoord = (int)((double)data % 16D);
		int yCoord = (int)((double)data / 16D);

		return new Point(xCoord, yCoord);
	}	
}
