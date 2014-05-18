package vic.rpg.world.tiles;

import java.awt.Point;

import vic.rpg.render.LightSource;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.entity.EntityTile;
import vic.rpg.world.path.Path;

import com.jogamp.opengl.util.texture.Texture;

/**
 * A Tile represents the ground layer of a {@link Map}. It is not getting instanced.
 * @author Victorious3
 */
public class Tile
{	
	protected static Texture terrainTex = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/terrain/terrain.png"));
	protected Map mapObj;
	public int data = 0;
	public int id = 0;
	
	public Texture getTexture(int x, int y, int data)
	{
		return terrainTex;
	}
	
	/**
	 * Sets the parent {@link Map} of this Tile.
	 * @param map
	 */
	public void setMapObj(Map map)
	{
		this.mapObj = map;
	}
	
	/**
	 * Returns the coordinates of where on the sprite sheet the texture of this Tile is located.
	 * @param x
	 * @param y
	 * @param data
	 * @return Point
	 */
	public Point getTextureCoord(int x, int y, int data)
	{
		return new Point(0, 0);
	}
	
	/**
	 * Returns the height of this Tile on a faked y-axis in 3D space.
	 * @param x
	 * @param y
	 * @param data
	 * @return Integer
	 */
	public int getHeight(int x, int y, int data)
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
	public Class<EntityTile> getTileEntity()
	{
		return null;
	}
	
	/**
	 * Checks weather this Tile has its own {@link LightSource}.
	 * @param x
	 * @param y
	 * @param data
	 * @return Boolean
	 */
	public boolean emitsLight(int x, int y, int data)
	{
		return false;
	}
	
	/**
	 * The LightSource of this Tile. Will only take effect when {@link #emitsLight(int, int, int)} returns {@code true}.
	 * @param x
	 * @param y
	 * @param data
	 * @return LightSource
	 */
	public LightSource getLightSource(int x, int y, int data)
	{
		return null;
	}
	
	/**
	 * Returns the position of the LightSource bound to this {@link Tile}.
	 * @param x
	 * @param y
	 * @param data
	 * @return Point
	 */
	public Point getLightPosition(int x, int y, int data)
	{
		return new Point(x * Map.CELL_SIZE / 2 - Map.CELL_SIZE / 4, y * Map.CELL_SIZE / 2 - Map.CELL_SIZE / 4);
	}
	
	/**
	 * Called every 0.2 seconds.
	 * @param x
	 * @param y
	 * @param data
	 */
	public void tick(int x, int y, int data){};
	
	//Editor stuff
	public String getDescription(){return "MISSING_DESCRIPTION";}
	
	public String getName()
	{
		return getClass().getSimpleName();
	}
	
	/**
	 * Used from the {@link Path} to check the cheapest path. E.g walking on a road takes less effort
	 * then walking through a morass.
	 * @return
	 */
	public double getMovementCost(){return 0;}
	/**
	 * Checks weather it is allowed to walk onto this Tile.
	 * @return
	 */
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
