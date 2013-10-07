package vic.rpg.level.entity;

import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.EntityStatic;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

public class EntityHouse extends EntityStatic
{
	public EntityHouse()
	{
		super(300, 340);
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_ENTITY_STATIC_HOUSE));
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord, yCoord + 21, 175, 215)));
		area.add(new Area(new Rectangle(xCoord + 175, yCoord, 127, 320)));
		return area;
	}

	@Override
	public void tick() 
	{
		
	}
}
