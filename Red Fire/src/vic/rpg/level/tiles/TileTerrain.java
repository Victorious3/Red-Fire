package vic.rpg.level.tiles;

import java.awt.Point;

import vic.rpg.level.TexturePath;
import vic.rpg.utils.Utils;

public class TileTerrain extends Tile implements TexturePath
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

	@Override
	public String getTexturePath() 
	{
		return "/vic/rpg/resources/terrain/terrain.png";
	}	
}
