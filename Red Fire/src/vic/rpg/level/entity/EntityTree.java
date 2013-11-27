package vic.rpg.level.entity;

import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.EntityStatic;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class EntityTree extends EntityStatic 
{
	public EntityTree() 
	{
		super(100, 225);
		if(Utils.getSide() == Side.CLIENT) this.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_ENTITY_STATIC_TREE));
	}
	
	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord + 30, yCoord + 195, 35, 28)));
		return area;
	}
}
