package vic.rpg.item;

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
}
