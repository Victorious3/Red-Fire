package vic.rpg.world.tile;

import java.awt.Point;

import vic.rpg.client.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.entity.tile.EntityTile;
import vic.rpg.world.entity.tile.EntityTileChest;

import com.jogamp.opengl.util.texture.Texture;

public class TileChest extends Tile
{
	private static Texture chest = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/terrain/chest.png"));
	
	@Override
	public Texture getTexture(int x, int y, int data, int layerID, Map map) 
	{
		return chest;
	}

	@Override
	public Point getTextureCoord(int x, int y, int data, int layerID, Map map) 
	{
		return data == 1 ? new Point(0, 0) : new Point(1, 0);
	}

	@Override
	public boolean hasTileEntity() 
	{
		return true;
	}

	@Override
	public Class<? extends EntityTile> getTileEntity() 
	{
		return EntityTileChest.class;
	}

	@Override
	public String getDescription() 
	{
		return "A chest.";
	}

	@Override
	public boolean isWalkingPermitted(int x, int y, int layerID, Map map) 
	{
		return false;
	}	
}
