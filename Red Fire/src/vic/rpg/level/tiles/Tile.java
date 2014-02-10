package vic.rpg.level.tiles;

import java.awt.Point;

import vic.rpg.level.Level;
import vic.rpg.render.LightSource;
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
	
	public int getHeight(int x, int y, int data)
	{
		return 1;
	}
	
	public boolean emitsLight(int x, int y, int data)
	{
		return false;
	}
	
	public LightSource getLightSource(int x, int y, int data)
	{
		return null;
	}
	
	public Point getLightPosition(int x, int y, int data)
	{
		return new Point(x * Level.CELL_SIZE / 2 - Level.CELL_SIZE / 4, y * Level.CELL_SIZE / 2 - Level.CELL_SIZE / 4);
	}
	
	public void tick(int x, int y, int data){};
	
	//Editor stuff
	public String getDescription(){return "MISSING_DESCRIPTION";}
	public String getName(){return "MISSING_NAME";}
	
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
