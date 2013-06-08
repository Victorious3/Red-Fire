package vic.rpg.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import vic.rpg.Game;
import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemFilter;
import vic.rpg.item.ItemPeer;
import vic.rpg.item.Slot;
import vic.rpg.item.SlotGrid;
import vic.rpg.registry.RenderRegistry;

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
		
		g2d.setColor(Color.white);
		g2d.setFont(RenderRegistry.RPGFont.deriveFont(36F));
		g2d.drawString("Inventory", 400, 300);
		g2d.drawString("Equipment", 400, 30);
		
		g2d.setFont(RenderRegistry.RPGFont.deriveFont(18F));
		g2d.drawString("Helmet", 620, 20);
		g2d.drawString("Gems", 700, 50);
		g2d.drawString("right Hand", 530, 100);
		g2d.drawString("Armor", 620, 100);
		g2d.drawString("left Hand", 700, 100);
		g2d.drawString("Boots", 620, 210);		
	}

	@Override
	public void initGui() 
	{
		setInventory(Game.thePlayer.getInventory());
		
		controlsList.add(new Slot(620, 20, 1, this, 2, 2));
		controlsList.add(new Slot(700, 50, 2, this).addFilter(new ItemFilter.SimpleItemFilter(ItemApple.class)));
		controlsList.add(new Slot(730, 50, 3, this).addFilter(new ItemFilter.SimpleItemFilter(ItemPeer.class)));
		controlsList.add(new Slot(540, 100, 4, this, 2, 4, true));
		controlsList.add(new Slot(620, 100, 5, this, 2, 3));
		controlsList.add(new Slot(700, 100, 6, this, 2, 3, true));
		controlsList.add(new Slot(620, 210, 7, this, 1, 2));
		controlsList.add(new Slot(650, 210, 8, this, 1, 2));
		
		controlsList.add(new SlotGrid(400, 300, 12, 8, 0, this));
	}	
}
