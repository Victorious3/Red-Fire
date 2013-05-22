package vic.rpg.item;

import java.awt.Color;
import java.awt.Graphics2D;

import vic.rpg.gui.IGuiContainer;
import vic.rpg.gui.controls.GControl;

public class Slot extends GControl implements Cloneable
{		
	public IGuiContainer gui;
	public Item item = null;

	public Slot(int xCoord, int yCoord, IGuiContainer gui) 
	{
		super(xCoord, yCoord, 30, 30);
		this.gui = gui;
	}
	
	public Slot setItem(Item item)
	{
		this.item = item;	
		return this;
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) 
	{
		if(item != null)
		{
			item.render(g2d);
		}
		
		g2d.setColor(new Color(112, 112, 112, 180));
		if(item != null) g2d.setColor(item.getBgColor());
		g2d.fillRect(xCoord, yCoord, width, height);
		
		g2d.setColor(Color.white);
		if(item != null) g2d.drawImage(item.img, null, xCoord, yCoord);
		
		g2d.setColor(Color.black);
		g2d.drawRect(xCoord, yCoord, width, height);
		
		if(mouseHovered)
		{		
			gui.isSlotHovered = true;
		}
	}

	@Override
	public void tick() 
	{
		if(item != null) item.tick();
	}
	
	@Override
	public void onClickStart(int x, int y) 
	{
		super.onClickStart(x, y);
		
		if(gui.currentSlot == null && item != null)
		{
			gui.currentSlot = this.clone();
			setItem(null);
		}
		else if(item == null && gui.currentSlot != null)
		{
			if(gui.currentSlot.item.gridWidth == 1 && gui.currentSlot.item.gridHeight == 1)
			{
				setItem(gui.currentSlot.item);
				gui.currentSlot = null;
			}		 
		}
		else if(item != null && gui.currentSlot != null)
		{
			if(gui.currentSlot.item.gridWidth == 1 && gui.currentSlot.item.gridHeight == 1)
			{	
				Item item = this.item;
				setItem(gui.currentSlot.item);
				gui.currentSlot.setItem(item);
			}
		}
	}
	
	@Override
	public Slot clone()
	{
		try {
			return (Slot) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
