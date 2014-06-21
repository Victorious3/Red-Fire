package vic.rpg.world.tile;

import java.awt.Point;

import vic.rpg.client.render.LightSource;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.entity.tile.EntityTile;
import vic.rpg.world.path.Path;

import com.jogamp.opengl.util.texture.Texture;

/**
 * A Tile represents the ground layer of a {@link Map}. It is not getting instanced.
 * @author Victorious3
 */
public class Tile
{	
	protected static Texture terrainTex = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/terrain/terrain.png"));
	public int data = 0;
	public int id = 0;
	
	public Texture getTexture(int x, int y, int data, int layerID, Map map)
	{
		return terrainTex;
	}
	
	/**
	 * Returns the coordinates of where on the sprite sheet the texture of this Tile is located.
	 * @param x
	 * @param y
	 * @param data
	 * @param map TODO
	 * @return Point
	 */
	public Point getTextureCoord(int x, int y, int data, int layerID, Map map)
	{
		return new Point(0, 0);
	}
	
	/**
	 * Returns the height of this Tile on a faked y-axis in 3D space.
	 * @param x
	 * @param y
	 * @param data
	 * @param map TODO
	 * @return Integer
	 */
	public int getHeight(int x, int y, int data, int layerID, Map map)
	{
		return 1;
	}
	
	/**
	 * Indicates weather the {@link Map} should generate an {@link EntityTile} at the Tile's position. The generated Tile can be specified with {@link #getTileEntity()}
	 * @see #getTileEntity()
	 * @return boolean
	 */
	public boolean hasTileEntity()
	{
		return false;
	}
	
	/**
	 * The {@link EntityTile} that is added to the map upon tile creation.
	 * @return
	 */
	public Class<? extends EntityTile> getTileEntity()
	{
		return null;
	}
	
	/**
	 * Checks weather this Tile has its own {@link LightSource}.
	 * @param x
	 * @param y
	 * @param data
	 * @param map TODO
	 * @return Boolean
	 */
	public boolean emitsLight(int x, int y, int data, int layerID, Map map)
	{
		return false;
	}
	
	/**
	 * The LightSource of this Tile. Will only take effect when {@link #emitsLight(int, int, int)} returns {@code true}.
	 * @param x
	 * @param y
	 * @param data
	 * @param map TODO
	 * @return LightSource
	 */
	public LightSource getLightSource(int x, int y, int data, int layerID, Map map)
	{
		return null;
	}
	
	/**
	 * Returns the position of the LightSource bound to this {@link Tile}.
	 * @param x
	 * @param y
	 * @param data
	 * @param map TODO
	 * @return Point
	 */
	public Point getLightPosition(int x, int y, int data, int layerID, Map map)
	{
		return new Point(x * Map.CELL_SIZE / 2 - Map.CELL_SIZE / 4, y * Map.CELL_SIZE / 2 - Map.CELL_SIZE / 4);
	}
	
	/**
	 * Called every 0.2 seconds.
	 * @param x
	 * @param y
	 * @param data
	 * @param map TODO
	 */
	public void tick(int x, int y, int data, int layerID, Map map){}
	
	//Editor stuff
	public String getDescription()
	{
		return "MISSING_DESCRIPTION";
	}
	
	public String getName()
	{
		return getClass().getSimpleName();
	}
	
	/**
	 * Used from the {@link Path} to check the cheapest path. E.g walking on a road takes less effort
	 * then walking through a morass.
	 * @param x TODO
	 * @param y TODO
	 * @param layerID TODO
	 * @param map TODO
	 * @return
	 */
	public double getMovementCost(int x, int y, int layerID, Map map)
	{
		return 0;
	}
	
	/**
	 * Checks weather it is allowed to walk onto this Tile.
	 * @param x TODO
	 * @param y TODO
	 * @param layerID TODO
	 * @param map TODO
	 * @return
	 */
	public boolean isWalkingPermitted(int x, int y, int layerID, Map map)
	{
		return true;
	}
	
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
