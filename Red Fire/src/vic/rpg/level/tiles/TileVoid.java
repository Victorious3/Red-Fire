package vic.rpg.level.tiles;

import java.awt.Color;
import java.awt.image.BufferedImage;

import vic.rpg.level.Level;
import vic.rpg.level.Tile;
import vic.rpg.render.Drawable;
import vic.rpg.utils.Utils;

public class TileVoid extends Tile
{
	private Drawable drawable = new Drawable(Level.CELL_SIZE, Level.CELL_SIZE);
	
	public TileVoid()
	{
		if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{
			BufferedImage buf = new BufferedImage(drawable.getWidth(), drawable.getHeight(), BufferedImage.TYPE_INT_ARGB);
			buf.getGraphics().setColor(Color.black);
			buf.getGraphics().fillRect(0, 0, drawable.getWidth(), drawable.getHeight());
			buf.getGraphics().dispose();
			drawable.setTexture(buf);
		}
	}
	
	@Override
	public Drawable getDrawable(int x, int y, int data) 
	{
		return drawable;
	}

	@Override
	public String getDescription() 
	{
		return "The impassable void. It's black. And dark.";
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		return false;
	}
}
