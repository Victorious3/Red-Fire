package vic.rpg.level.entity;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;

import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class EntityHouse extends EntityStatic
{
	public EntityHouse()
	{
		super(192, 224);
		if(Utils.getSide() == Side.CLIENT) this.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_ENTITY_STATIC_HOUSE));
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		Point p = Utils.convCartToIso(new Point(xCoord, yCoord));
		area.add(new Area(new Polygon(new int[]{p.x + 3, p.x + 97, p.x + 192, p.x + 95}, new int[]{p.y + 177, p.y + 117, p.y + 177, p.y + 224}, 4)));
		return area;
	}

	@Override
	public void tick() 
	{
		
	}
}
