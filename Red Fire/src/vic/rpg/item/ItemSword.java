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
}
