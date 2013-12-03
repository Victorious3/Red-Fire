package vic.rpg.item;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.gui.GuiContainer;
import vic.rpg.gui.controls.GControl;
import vic.rpg.render.DrawUtils;

public class Slot extends GControl implements Cloneable
{		
	public GuiContainer gui;
	public int sWidth = 1;
	public int sHeight = 1;
	public ItemFilter filter;
	public boolean acceptOtherSizes = false;
	public int id;
	
	public Slot(int xCoord, int yCoord, int id, GuiContainer gui) 
	{
		this(xCoord, yCoord, id, gui, 1, 1, false);
	}
	
	public Slot(int xCoord, int yCoord, int id, GuiContainer gui, boolean acceptOtherSizes) 
	{
		this(xCoord, yCoord, id, gui, 1, 1, acceptOtherSizes);
	}
	
	public Slot(int xCoord, int yCoord, int id, GuiContainer gui, int sWidth, int sHeight) 
	{
		this(xCoord, yCoord, id, gui, sWidth, sHeight, false);
	}
	
	public Slot(int xCoord, int yCoord, int id, GuiContainer gui, int sWidth, int sHeight, boolean acceptOtherSizes) 
	{
		super(xCoord, yCoord, 30 * sWidth, 30 * sHeight);
		this.gui = gui;
		this.sWidth = sWidth;
		this.sHeight = sHeight;
		this.acceptOtherSizes = acceptOtherSizes;
		this.id = id;
	}
	
	public Slot setItem(Item item)
	{
		gui.inventory.setItem(id, item);
		return this;
	}
	
	public Slot addFilter(ItemFilter filter)
	{
		this.filter = filter;
		return this;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{
		if(gui.inventory.getItem(id) != null)
		{
			gui.inventory.getItem(id).render(gl2);
		}
		DrawUtils.setGL(gl2);
		
		if(gui.inventory.getItem(id) == null) DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(112, 112, 112, 180));
		else DrawUtils.fillRect(xCoord, yCoord, width, height, gui.inventory.getItem(id).getBgColor());
		
		if(this.mouseHovered)
		{
			if(gui.currentSlot != null)
			{
				if(canBePlacedIn(gui.currentSlot.gui.inventory.getItem(id)))
				{
					DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
				}
			}
			else if(this.gui.inventory.getItem(id) != null)
			{
				DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
			}
		}
		
		if(gui.inventory.getItem(id) != null) DrawUtils.drawTexture(xCoord + (width - gui.inventory.getItem(id).getWidth()) / 2, yCoord + (height - gui.inventory.getItem(id).getHeight()) / 2, gui.inventory.getItem(id).getTexture());
	
		DrawUtils.drawRect(xCoord, yCoord, width, height, Color.black);
		
		if(mouseHovered && gui.inventory.getItem(id) != null)
		{		
			gui.isSlotHovered = true;
		}
	}

	@Override
	public void tick() 
	{
		if(gui.inventory.getItem(id) != null) gui.inventory.getItem(id).tick();
	}
	
	@Override
	public void onClickStart(int x, int y) 
	{
		super.onClickStart(x, y);
		
		if(gui.currentSlot == null && gui.inventory.getItem(id) != null)
		{
			gui.inventory.addItem(gui.currentSlot.id, this.gui.inventory.getItem(id));
			setItem(null);
		}
		else if(gui.inventory.getItem(id) == null && gui.currentSlot != null)
		{									
			if(canBePlacedIn(gui.inventory.getItem(gui.currentSlot.id)))
			{
				setItem(gui.inventory.getItem(gui.currentSlot.id));
				gui.currentSlot = null;
			}				 
		}
		else if(gui.inventory.getItem(id) != null && gui.currentSlot != null)
		{
			if(canBePlacedIn(gui.inventory.getItem(gui.currentSlot.id)))
			{	
				Item item = gui.inventory.getItem(gui.currentSlot.id);
				setItem(gui.inventory.getItem(gui.currentSlot.id));
				gui.inventory.addItem(gui.currentSlot.id, item);
			}
		}
	}
	
	public boolean canBePlacedIn(Item item)
	{
		if(item == null) return true;
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
