package vic.rpg.level.entity;

import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.EntityStatic;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.utils.Utils;

public class EntityTree extends EntityStatic 
{
	public EntityTree() 
	{
		super(100, 225);
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.drawImage(RenderRegistry.IMG_ENTITY_STATIC_TREE, 0, 0, getWidth(), getHeight());
	}
	
	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord + 30, yCoord + 195, 35, 28)));
		return area;
	}
}
