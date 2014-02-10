package vic.rpg.level.entity;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class EntityTree extends EntityStatic 
{
	public EntityTree() 
	{
		super(100, 225);
		if(Utils.getSide() == Side.CLIENT) 
		{
			this.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_ENTITY_STATIC_TREE));
		}
	}
	
	@Override
	public Area getCollisionBoxes(Area area) 
	{
		Point p = Utils.convCartToIso(new Point(xCoord, yCoord));
		area.add(new Area(new Rectangle(p.x + 30, p.y + 195, 35, 28)));
		return area;
	}
}
