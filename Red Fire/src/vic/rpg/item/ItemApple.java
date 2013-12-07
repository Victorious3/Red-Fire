package vic.rpg.item;

public class ItemApple extends Item {

	public ItemApple() 
	{
		super("/vic/rpg/resources/item/apple.png", 1);
	}

	@Override
	public String getItemName() 
	{
		return "Apple";
	}
}
