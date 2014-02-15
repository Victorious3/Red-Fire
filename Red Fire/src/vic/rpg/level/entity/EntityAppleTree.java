package vic.rpg.level.entity;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.item.ItemStack;
import vic.rpg.level.entity.living.EntityController;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class EntityAppleTree extends EntityStatic 
{
	public EntityAppleTree() 
	{
		super(70, 87);
		if(Utils.getSide() == Side.CLIENT) this.setTexture(TextureLoader.loadTexture(RenderRegistry.IMG_ENTITY_STATIC_APLTREE));
	}
	
	@Override
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent) 
	{
		super.onMouseClicked(x, y, entity, mouseEvent);
		if(Utils.getSide() == Side.SERVER)
		{
			entity.inventory.addToInventory(new ItemStack(LevelRegistry.ITEM_APPLE));
			entity.sendChatMessage("You take a delicious apple but it has thorns and hurts you.", "Apple Tree");
			EntityController.changeHealth(entity, -0.1F);
		}
	}

	@Override
	public Area getCollisionBoxes(Area area) 
	{
		Point p = Utils.convCartToIso(new Point(xCoord, yCoord));
		area.add(new Area(new Rectangle(p.x + 3, p.y + 56, 65, 31)));
		return area;
	}
}
