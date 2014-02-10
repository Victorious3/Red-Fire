package vic.rpg.level.tiles;

import java.awt.Point;

import vic.rpg.level.TexturePath;
import vic.rpg.utils.Utils;

@TexturePath(path = "/vic/rpg/resources/terrain/terrain.png")
public class TileTerrain extends Tile 
{	
	@Override
	public String getDescription() 
	{
		return "Standard Terrain Tile";
	}

	@Override
	public Point getTextureCoord(int x, int y, int data) 
	{
		return Utils.conv1Dto2DPoint(data, 10D);
	}	
}
