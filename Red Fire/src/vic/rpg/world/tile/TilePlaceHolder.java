package vic.rpg.world.tile;

import java.awt.Point;

import vic.rpg.utils.Utils;
import vic.rpg.world.Map;

import com.jogamp.opengl.util.texture.Texture;

/**
 * Used for {@link Tile Tiles} of a size bigger than 1x1 to keep its place.
 * It is passing all methods from the parent {@link Tile}. <b>Should never be
 * used on its own!</b>
 * @author Victorious3
 */
public class TilePlaceHolder extends Tile
{
	@Override
	public Texture getTexture(int x, int y, int data, int layerID, Map map) 
	{
		return null;
	}

	@Override
	public double getMovementCost(int x, int y, int layerID, Map map) 
	{
		Point p = Utils.conv1Dto2DPoint(data, map.width);
		return map.getTileAt(p.x, p.y).getMovementCost(x, y, layerID, map);
	}

	@Override
	public boolean isWalkingPermitted(int x, int y, int layerID, Map map) 
	{
		Point p = Utils.conv1Dto2DPoint(data, map.width);
		return map.getTileAt(p.x, p.y).isWalkingPermitted(x, y, layerID, map);	
	}

	@Override
	public String getDescription() 
	{
		return "Don't use this Tile! Ever!";
	}
	
	/**
	 * Returns the parent {@link Tile} of this place holder.
	 * @param x
	 * @param y
	 * @param data
	 * @return Tile
	 */
	public Tile getParent(int x, int y, int data, int layerID, Map map)
	{
		Point p = Utils.conv1Dto2DPoint(data, map.width);
		return map.getTileAt(p.x, p.y, layerID);
	}
}
