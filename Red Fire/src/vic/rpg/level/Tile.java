package vic.rpg.level;

import java.awt.Dimension;
import java.awt.Point;

import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class Tile
{	
	protected static Texture terrainTex = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/terrain/terrain.png"));
	protected Level worldObj;
	public int data = 0;
	public int id = 0;
	
	public Texture getTexture(int x, int y, int data)
	{
		return terrainTex;
	}
	
	public void setWorldObj(Level lvl)
	{
		this.worldObj = lvl;
	}
	
	public Point getTextureCoord(int x, int y, int data)
	{
		return new Point(0, 0);
	}
	
	public Dimension getDimension(int x, int y, int data)
	{
		return new Dimension(1, 1);
	}
	
	public void tick(int x, int y, int data){};
	
	//Editor stuff
	public String getDescription(){return "MISSING_DESCRIPTION";}
	
	public double getMovementCost(){return 0;}
	public boolean isWalkingPermitted(){return true;}
	
	@Override
	public Tile clone() 
	{
		try {
			return (Tile) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}		
}
