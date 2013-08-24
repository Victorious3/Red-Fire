package vic.rpg.level.entity;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.item.ItemApple;
import vic.rpg.level.EntityStatic;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.TextureLoader;
import vic.rpg.render.LightSource;
import vic.rpg.utils.Utils;

public class EntityAppleTree extends EntityStatic 
{
	public EntityAppleTree() 
	{
		super(70, 87);
		this.lightSources.add(new LightSource(1000, 1.0F, Color.yellow));
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_ENTITY_STATIC_APLTREE));
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
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent) 
	{
		super.onMouseClicked(x, y, entity, mouseEvent);
		if(Utils.getSide().equals(Utils.SIDE_SERVER))
		{
			entity.inventory.addToInventory(new ItemApple());
			entity.sendChatMessage("You take a delicious apple", "Apple Tree");
		}
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		area.add(new Area(new Rectangle(xCoord + 3, yCoord + 56, 65, 31)));
		return area;
	}
}
