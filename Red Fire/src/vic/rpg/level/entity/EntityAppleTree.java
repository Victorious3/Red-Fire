package vic.rpg.level.entity;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.EntityStatic;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.LightSource;

public class EntityAppleTree extends EntityStatic 
{
	public EntityAppleTree() 
	{
		super(70, 87);
		this.lightSources.add(new LightSource(1000, 1.0F, Color.yellow));
	}
	
	@Override
	public Point getLightPosition(LightSource l) 
	{
		if(l == lightSources.get(0))
		{
			return new Point(this.xCoord + this.getWidth() / 2, this.yCoord + this.getHeight() / 2);
		}
		return null;
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
