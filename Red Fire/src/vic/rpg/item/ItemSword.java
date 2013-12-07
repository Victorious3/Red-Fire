package vic.rpg.item;

import java.awt.Color;

public class ItemSword extends Item {

	public ItemSword() 
	{
		super("/vic/rpg/resources/item/sword.png", 3, 30, 90);
	}

	@Override
	public Color getBgColor() 
	{
		return new Color(150, 145, 0, 180);
	}

	@Override
	public String getItemName() 
	{
		return "&4Sword of Fire";
	}
	
	@Override
	public String[] getItemDescription() 
	{
		return new String[]
		{
			"&iThis Sword has",
			"&iwon many hard battles",
			"&iand was carried by the",
			"&ifamous \"Red Lord\""
		};
	}
}
