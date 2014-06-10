package vic.rpg.world.tiles;

import java.awt.Point;

import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.TexturePath;

public class TileTerrain extends Tile implements TexturePath
{	
	@Override
	public String getDescription() 
	{
		return "Standard Terrain Tile";
	}

	@Override
	public Point getTextureCoord(int x, int y, int data, int layerID, Map map) 
	{
		return Utils.conv1Dto2DPoint(data, 10D);
	}

	@Override
	public String getTexturePath() 
	{
		return "/vic/rpg/resources/terrain/terrain.png";
	}	
}
