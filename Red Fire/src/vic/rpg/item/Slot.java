package vic.rpg.item;

import java.awt.Color;
import java.awt.event.MouseEvent;

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
		
		if(gui.inventory.getItem(id) != null) DrawUtils.drawTexture(xCoord + (width - gui.inventory.getItem(id).getWidth()) / 2, yCoord + (height - gui.inventory.getItem(id).getHeight()) / 2, gui.inventory.getItem(id).getTexture());
	
		DrawUtils.drawRect(xCoord, yCoord, width, height, Color.black);
		
		if(mouseHovered && gui.inventory.getItem(id) != null)
		{		
			gui.isSlotHovered = true;
		}
	}	

	@Override
	public void postRender(GL2 gl2, int x, int y) 
	{
		if(this.mouseHovered)
		{
			if(gui.inventory.getItem(gui.currentSlot.id) != null)
			{
				if(canBePlacedIn(gui.inventory.getItem(gui.currentSlot.id)))
				{
					DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
				}
			}
			else if(this.gui.inventory.getItem(id) != null)
			{
				DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
				gui.inventory.getItem(id).renderItemInformation(gl2, x, y);
			}
		}
		super.postRender(gl2, x, y);	
	}

	@Override
	public void tick() 
	{
		if(gui.inventory.getItem(id) != null) gui.inventory.getItem(id).tick();
	}
	
	@Override
	public void onClickStart(int x, int y, int mouseButton) 
	{
		super.onClickStart(x, y, mouseButton);
		
		if(mouseButton == MouseEvent.BUTTON1)
		{
			if(gui.inventory.getItem(gui.currentSlot.id) != null && gui.inventory.getItem(id) == null)
			{
				if(canBePlacedIn(gui.inventory.getItem(gui.currentSlot.id)))
				{
					setItem(gui.inventory.getItem(gui.currentSlot.id));
					gui.inventory.setItem(gui.currentSlot.id, null);
					gui.inventory.updateInventory();
				}
			}
			else if(gui.inventory.getItem(id) != null && gui.inventory.getItem(gui.currentSlot.id) == null)
			{									
				gui.inventory.setItem(gui.currentSlot.id, gui.inventory.getItem(id));
				setItem(null);
				gui.inventory.updateInventory();
			}
			else if(gui.inventory.getItem(id) != null && gui.inventory.getItem(gui.currentSlot.id) != null)
			{
				if(canBePlacedIn(gui.inventory.getItem(gui.currentSlot.id)))
				{	
					Item item = gui.inventory.getItem(id);
					setItem(gui.inventory.getItem(gui.currentSlot.id));
					gui.inventory.setItem(gui.currentSlot.id, item);
					gui.inventory.updateInventory();
				}
			}
		}
		else if(mouseButton == MouseEvent.BUTTON3)
		{
			if(gui.inventory.getItem(id) != null) gui.inventory.onItemUse(id, x, y);
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
