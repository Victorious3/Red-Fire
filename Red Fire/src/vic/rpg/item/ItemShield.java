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
	
	@Override
	public String getItemName() 
	{
		return "&5Shield of Infinity";
	}

	@Override
	public String[] getItemDescription() 
	{
		return new String[]
		{
			"&iThis Shield was",
			"&icreated by long forgotten",
			"&igods",
			"",
			"&p&3+5 Healing Power"
		};
	}
}
