package vic.rpg.level.entity;

import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.EntityStatic;
import vic.rpg.registry.RenderRegistry;

public class EntityAppleTree extends EntityStatic 
{
	public EntityAppleTree() 
	{
		super(70, 87);		
	}

	@Override
	public void initRender() 
	{
		this.drawImage(RenderRegistry.IMG_ENTITY_STATIC_APLTREE, 0, 0, getWidth(), getHeight());
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord + 3, yCoord + 56, 65, 31)));
		return area;
	}
}
