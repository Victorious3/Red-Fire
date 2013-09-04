package vic.rpg.level.tiles;

import java.awt.Dimension;

import vic.rpg.level.Tile;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class TileBoat extends Tile
{
	private static Texture boatTex = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/terrain/boat.png"));
	
	@Override
	public Texture getTexture(int x, int y, int data) 
	{
		return boatTex;
	}

	@Override
	public Dimension getDimension(int x, int y, int data) 
	{
		return new Dimension(4, 2);
	}

	@Override
	public String getDescription() 
	{
		return "A testish boat.";
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		return false;
	}	
}
