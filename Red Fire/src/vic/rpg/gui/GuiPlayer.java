package vic.rpg.gui;

import java.awt.Graphics2D;

import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemPeer;
import vic.rpg.item.ItemShield;
import vic.rpg.item.ItemSword;
import vic.rpg.item.Slot;
import vic.rpg.item.SlotGrid;

public class GuiPlayer extends IGuiContainer
{
	public GuiPlayer() 
	{
		super(false);
	}

	@Override
	public void render(Graphics2D g2d) 
	{
		super.render(g2d);
	}

	@Override
	public void initGui() 
	{
		controlsList.add(new Slot(100, 100, this).setItem(new ItemApple()));
		controlsList.add(new Slot(100, 150, this));
		controlsList.add(new Slot(150, 100, this));
		controlsList.add(new Slot(150, 150, this).setItem(new ItemPeer()));
		controlsList.add(new Slot(200, 100, this));
		controlsList.add(new Slot(200, 150, this));
		
		controlsList.add(new SlotGrid(300, 300, 5, 5, this).setItem(0, 2, new ItemSword()).setItem(1, 0, new ItemShield()));
	}	
}
