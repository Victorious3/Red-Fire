package vic.rpg.item;

import java.awt.Color;

public class ItemShield extends Item {

	public ItemShield() 
	{
		super("/vic/rpg/resources/item/shield.png", 4, 60, 90);
	}

	@Override
	public Color getBgColor() 
	{
		return new Color(158, 31, 74, 180);
	}
}
