package vic.rpg.level.tiles;

import java.awt.Point;

import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

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
		Point p = Utils.conv1Dto2DPoint(data, this.worldObj.width);
		return this.worldObj.getTileAt(p.x, p.y).getMovementCost();
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		Point p = Utils.conv1Dto2DPoint(data, this.worldObj.width);
		return this.worldObj.getTileAt(p.x, p.y).isWalkingPermitted();	
	}

	@Override
	public String getDescription() 
	{
		return "Don't use this Tile! Ever!";
	}
	
	public Tile getParent(int x, int y, int data)
	{
		Point p = Utils.conv1Dto2DPoint(data, this.worldObj.width);
		return this.worldObj.getTileAt(p.x, p.y);
	}
}
