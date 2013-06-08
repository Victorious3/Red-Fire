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
	public boolean acceptOtherSizes = false;
	public int id;
	
	public Slot(int xCoord, int yCoord, int id, IGuiContainer gui) 
	{
		this(xCoord, yCoord, id, gui, 1, 1, false);
	}
	
	public Slot(int xCoord, int yCoord, int id, IGuiContainer gui, boolean acceptOtherSizes) 
	{
		this(xCoord, yCoord, id, gui, 1, 1, acceptOtherSizes);
	}
	
	public Slot(int xCoord, int yCoord, int id, IGuiContainer gui, int sWidth, int sHeight) 
	{
		this(xCoord, yCoord, id, gui, sWidth, sHeight, false);
	}
	
	public Slot(int xCoord, int yCoord, int id, IGuiContainer gui, int sWidth, int sHeight, boolean acceptOtherSizes) 
	{
		super(xCoord, yCoord, 30 * sWidth, 30 * sHeight);
		this.gui = gui;
		this.sWidth = sWidth;
		this.sHeight = sHeight;
		this.acceptOtherSizes = acceptOtherSizes;
		this.id = id;
		this.item = gui.inventory.getItem(id);
	}
	
	public Slot setItem(Item item)
	{
		this.item = item;
		gui.inventory.setStack(id, item);
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
		g2d.setColor(new Color(0, 0, 0, 50));
		if(this.mouseHovered)
		{
			if(gui.currentSlot != null)
			{
				if(canBePlacedIn(gui.currentSlot.item))
				{
					g2d.fillRect(xCoord, yCoord, width, height);
				}
			}
			else if(this.item != null)
			{
				g2d.fillRect(xCoord, yCoord, width, height);
			}
		}
		
		g2d.setColor(Color.white);
		if(item != null) g2d.drawImage(item.img, null, xCoord + (width - item.getWidth()) / 2, yCoord + (height - item.getHeight()) / 2);
		
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
			if(canBePlacedIn(gui.currentSlot.item))
			{
				setItem(gui.currentSlot.item);
				gui.currentSlot = null;
			}				 
		}
		else if(item != null && gui.currentSlot != null)
		{
			if(canBePlacedIn(gui.currentSlot.item))
			{	
				Item item = this.item;
				setItem(gui.currentSlot.item);
				gui.currentSlot.setItem(item);
			}
		}
	}
	
	public boolean canBePlacedIn(Item item)
	{
		if(((item.gridWidth == this.sWidth && item.gridHeight == this.sHeight) && !acceptOtherSizes) || ((item.gridWidth <= this.sWidth && item.gridHeight <= this.sHeight) && acceptOtherSizes))
		{	
			if(filter != null){if(!filter.isItemValid(item)) return false;}
			return true;
		}
		return false;
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
