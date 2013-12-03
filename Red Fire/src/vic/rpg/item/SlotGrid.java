package vic.rpg.item;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.gui.GuiContainer;
import vic.rpg.gui.controls.GControl;
import vic.rpg.render.DrawUtils;

//TODO Clean that mess up -_-
public class SlotGrid extends GControl
{	
	public GuiContainer gui;
	
	public int gridWidth = 1;
	public int gridHeight = 1;
	public int id;
	
	public SlotGrid(int xCoord, int yCoord, int width, int height, int id, GuiContainer gui) 
	{
		super(xCoord, yCoord, width * 30, height * 30);
		
		this.gridWidth = width;
		this.gridHeight = height;
		this.gui = gui;
		this.id = id;
	}
	
	public SlotGrid(Item[][] items, int xCoord, int yCoord, int id, GuiContainer gui)
	{
		super(xCoord, yCoord, items.length * 30, items[0].length * 30);
		
		this.gridWidth = items.length;
		this.gridHeight = items[0].length;
		
		gui.inventory.setItemGrid(id, items);
		this.gui = gui;
		this.id = id;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{		
		DrawUtils.setGL(gl2);
		for(Item[] items : this.gui.inventory.getItemGrid(id))
		{
			for(Item item : items)
			{
				if(item != null) item.render(gl2);
			}
		}
		
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				Item item = gui.inventory.overlapsWith(gui.inventory.getItemGrid(id), 1, 1, i, j);
				if(gui.inventory.getItemGrid(id)[i][j] != null)
				{
					DrawUtils.fillRect(xCoord + i * 30, yCoord + j * 30, 30, 30, gui.inventory.getItemGrid(id)[i][j].getBgColor());
				}
				else if(item != null) DrawUtils.fillRect(xCoord + i * 30, yCoord + j * 30, 30, 30, item.getBgColor());
				else DrawUtils.fillRect(xCoord + i * 30, yCoord + j * 30, 30, 30, new Color(112, 112, 112, 180));
			}
		}
		
		for(int i = 0; i <= width; i += 30)
		{
			DrawUtils.drawLine(xCoord + i, yCoord, xCoord + i, yCoord + height, Color.black);
		}
		for(int i = 0; i <= height; i += 30)
		{
			DrawUtils.drawLine(xCoord, yCoord + i, xCoord + width, yCoord + i, Color.black);
		}
		
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				if(gui.inventory.getItemGrid(id)[i][j] != null) DrawUtils.drawTexture(xCoord + i * 30, yCoord + j * 30, gui.inventory.getItemGrid(id)[i][j].getTexture());
			}
		}
		
		if(mouseHovered)
		{
			Item it = gui.inventory.overlapsWith(gui.inventory.getItemGrid(id), 1, 1, (x - xCoord) / 30, (y - yCoord) / 30);
			
			if(gui.inventory.getItem(gui.currentSlot.id) != null)
			{
				int x1 = (x - xCoord) / 30;
				int y1 = (y - yCoord) / 30;
				
				if(gui.inventory.canBePlacedAt(gui.inventory.getItemGrid(id), x1, y1, gui.inventory.getItem(gui.currentSlot.id)))
				{
					DrawUtils.fillRect(xCoord + x1 * 30, yCoord + y1 * 30,gui.inventory.getItem(gui.currentSlot.id).gridWidth * 30, gui.inventory.getItem(gui.currentSlot.id).gridHeight * 30, new Color(0, 0, 0, 50));
				}
			}
			else if(it != null)
			{
				gui.isSlotHovered = true;
				DrawUtils.fillRect(xCoord + it.xCoord * 30, yCoord + it.yCoord * 30, it.gridWidth * 30, it.gridHeight * 30, new Color(0, 0, 0, 50));
			}
		}		
	}

	@Override
	public void onClickStart(int x, int y) 
	{
		super.onClickStart(x, y);
		
		x -= xCoord;
		y -= yCoord;
		
		onClickedAtCoord(x / 30, y / 30);
		
	}
	
	private void onClickedAtCoord(int x, int y)
	{
		Item item = gui.inventory.overlapsWith(gui.inventory.getItemGrid(id), 1, 1, x, y);
		
		if(gui.currentSlot == null && item != null)
		{
			gui.inventory.addItem(gui.currentSlot.id, item);
			setItem(item.xCoord, item.yCoord, null);
		}
		else if(gui.currentSlot != null)
		{
			if(gui.inventory.canBePlacedAt(gui.inventory.getItemGrid(id), x, y, gui.inventory.getItem(gui.currentSlot.id)));
			{
				setItem(x, y, gui.inventory.getItem(gui.currentSlot.id));
				gui.inventory.addItem(gui.currentSlot.id, null);
			}			
		}	
	}

	public SlotGrid setItem(int x, int y, Item item) 
	{
		if(gui != null) gui.inventory.setItemGrid(id, item, x, y);
		return this;
	}
	
	public boolean setItemAndConfirm(int x, int y, Item item) 
	{
		if(gui.inventory.canBePlacedAt(gui.inventory.getItemGrid(id), x, y, item))
		{
			setItem(x, y, item);
			return true;
		}
		return false;
	}
	
	public SlotGrid setItems(Item[][] items) 
	{
		gui.inventory.setItemGrid(id, items);
		return this;
	}
	
	@Override
	public void tick() 
	{
		for(Item[] items : this.gui.inventory.getItemGrid(id))
		{
			for(Item item : items)
			{
				if(item != null)
				{
					if(item.isTicking) item.tick();
				}			
			}
		}
	}
}
