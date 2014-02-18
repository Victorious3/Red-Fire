package vic.rpg.item;

import vic.rpg.level.entity.living.EntityController;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.entity.living.Inventory;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class ItemApple extends Item {

	public ItemApple() 
	{
		super("/vic/rpg/resources/item/apple.png", 1);
		this.isStackable = true;
	}

	@Override
	public String getItemName() 
	{
		return "Apple";
	}

	@Override
	public ItemStack onItemUse(EntityLiving entity, Inventory i, ItemStack stack) 
	{
		if(Utils.getSide() == Side.SERVER)
		{
			EntityController.changeHealth(entity, 0.1F);
		}
		stack.setStackSize(stack.getStackSize() - 1);
		return stack;
	}
}
