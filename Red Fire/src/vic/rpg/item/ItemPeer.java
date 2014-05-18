package vic.rpg.item;

import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.entity.living.EntityController;
import vic.rpg.world.entity.living.EntityLiving;
import vic.rpg.world.entity.living.Inventory;

public class ItemPeer extends Item {

	public ItemPeer() 
	{
		super("/vic/rpg/resources/item/peer.png", 2);
		this.isStackable = true;
	}
	
	@Override
	public String getItemName() 
	{
		return "Peer";
	}
	
	@Override
	public ItemStack onItemUse(EntityLiving entity, Inventory i, ItemStack stack) 
	{
		if(Utils.getSide() == Side.SERVER)
		{
			EntityController.changeHealth(entity, -0.1F);
		}
		stack.setStackSize(stack.getStackSize() - 1);
		return stack;
	}
}
