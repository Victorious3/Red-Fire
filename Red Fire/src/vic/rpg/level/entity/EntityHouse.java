package vic.rpg.level.entity;

import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.EntityStatic;
import vic.rpg.registry.RenderRegistry;

public class EntityHouse extends EntityStatic
{
	public EntityHouse()
	{
		super(300, 340);
	}

	@Override
	public void initRender() 
	{
		super.initRender();
		this.drawImage(RenderRegistry.IMG_ENTITY_STATIC_HOUSE, 0, 0, getWidth(), getHeight());
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord, yCoord + 21, 175, 215)));
		area.add(new Area(new Rectangle(xCoord + 175, yCoord, 127, 320)));
		
//		Polygon p = new Polygon();
//		p.xpoints = new int[]{xCoord + 000, xCoord + 138, xCoord + 160, xCoord + 160, xCoord + 214, xCoord + 235, xCoord + 300, xCoord + 300, xCoord + 317, xCoord + 172, xCoord + 000};
//		p.ypoints = new int[]{yCoord + 021, yCoord + 000, yCoord + 000, yCoord + 021, yCoord + 021, yCoord + 000, yCoord + 061, yCoord + 318, yCoord + 318, yCoord + 234, yCoord + 234};
//		p.npoints = p.xpoints.length;
		
//		list.add(p);
		return area;
	}
	
	
}
