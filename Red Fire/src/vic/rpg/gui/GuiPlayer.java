package vic.rpg.gui;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemFilter;
import vic.rpg.item.ItemPeer;
import vic.rpg.item.Slot;
import vic.rpg.item.SlotGrid;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;

public class GuiPlayer extends GuiContainer
{
	public GuiPlayer() 
	{
		super(false);
	}

	@Override
	public void render(GL2 gl2) 
	{						
		super.render(gl2);
		DrawUtils.setGL(gl2);
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(36F));
		DrawUtils.drawString(400, 300, "Inventory", Color.white);
		DrawUtils.drawString(400, 30, "Equipment", Color.white);
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(18F));
		DrawUtils.drawString(620, 20, "Helmet", Color.white);
		DrawUtils.drawString(700, 50, "Gems", Color.white);
		DrawUtils.drawString(530, 100, "right Hand", Color.white);
		DrawUtils.drawString(620, 100, "Armor", Color.white);
		DrawUtils.drawString(700, 100, "left Hand", Color.white);
		DrawUtils.drawString(620, 210, "Boots", Color.white);		
	}

	@Override
	public void initGui() 
	{
		setInventory(Game.thePlayer.getInventory());
		
		controlsList.clear();
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
