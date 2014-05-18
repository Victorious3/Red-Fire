package vic.rpg.world.tiles;

import java.awt.Point;

import vic.rpg.utils.Utils;

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
	public Texture getTexture(int x, int y, int data) 
	{
		return null;
	}

	@Override
	public double getMovementCost() 
	{
		Point p = Utils.conv1Dto2DPoint(data, this.mapObj.width);
		return this.mapObj.getTileAt(p.x, p.y).getMovementCost();
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		Point p = Utils.conv1Dto2DPoint(data, this.mapObj.width);
		return this.mapObj.getTileAt(p.x, p.y).isWalkingPermitted();	
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
	public Tile getParent(int x, int y, int data)
	{
		Point p = Utils.conv1Dto2DPoint(data, this.mapObj.width);
		return this.mapObj.getTileAt(p.x, p.y);
	}
}
