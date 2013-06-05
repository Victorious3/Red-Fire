package vic.rpg.item;

import java.awt.Color;
import java.awt.Graphics2D;

import vic.rpg.gui.IGuiContainer;
import vic.rpg.gui.controls.GControl;

public class Slot extends GControl implements Cloneable
{		
	public IGuiContainer gui;
	public Item item = null;
	public int sWidth = 1;
	public int sHeight = 1;
	public ItemFilter filter;
	
	public Slot(int xCoord, int yCoord, IGuiContainer gui) 
	{
		this(xCoord, yCoord, gui, 1, 1);
	}
	
	public Slot(int xCoord, int yCoord, IGuiContainer gui, int sWidth, int sHeight) 
	{
		super(xCoord, yCoord, 30 * sWidth, 30 * sHeight);
		this.gui = gui;
		this.sWidth = sWidth;
		this.sHeight = sHeight;
	}
	
	public Slot setItem(Item item)
	{
		this.item = item;	
		return this;
	}
	
	public Slot addFilter(ItemFilter filter)
	{
		this.filter = filter;
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
		
		if(mouseHovered && item != null)
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
			if(gui.currentSlot.item.gridWidth == this.sWidth && gui.currentSlot.item.gridHeight == this.sHeight)
			{				
				if(filter != null){if(!filter.isItemValid(gui.currentSlot.item)) return;}
				setItem(gui.currentSlot.item);
				gui.currentSlot = null;
			}		 
		}
		else if(item != null && gui.currentSlot != null)
		{
			if(gui.currentSlot.item.gridWidth == this.sWidth && gui.currentSlot.item.gridHeight == this.sHeight)
			{	
				if(filter != null){if(!filter.isItemValid(gui.currentSlot.item)) return;}
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
