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
	
	public Item[][] items;
	
	public SlotGrid(int xCoord, int yCoord, int width, int height, int id, GuiContainer gui) 
	{
		super(xCoord, yCoord, width * 30, height * 30);
		
		this.gridWidth = width;
		this.gridHeight = height;
		this.gui = gui;
		this.id = id;
		
		this.items = gui.inventory.getItemGrid(id);
	}
	
	public SlotGrid(Item[][] items, int xCoord, int yCoord, int id, GuiContainer gui)
	{
		super(xCoord, yCoord, items.length * 30, items[0].length * 30);
		
		this.gridWidth = items.length;
		this.gridHeight = items[0].length;
		
		this.items = items;
		this.gui = gui;
		this.id = id;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{		
		DrawUtils.setGL(gl2);
		for(Item[] items : this.items)
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
				Item item = overlapsWith(1, 1, i, j);
				if(items[i][j] != null)
				{
					DrawUtils.fillRect(xCoord + i * 30, yCoord + j * 30, 30, 30, items[i][j].getBgColor());
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
				if(items[i][j] != null) DrawUtils.drawTexture(xCoord + i * 30, yCoord + j * 30, items[i][j].getTexture());
			}
		}
		
		if(mouseHovered)
		{
			Item it = overlapsWith(1, 1, (x - xCoord) / 30, (y - yCoord) / 30);
			
			if(gui.currentSlot != null)
			{
				int x1 = (x - xCoord) / 30;
				int y1 = (y - yCoord) / 30;
				
				if(canBePlacedAt(x1, y1, gui.currentSlot.item))
				{
					DrawUtils.fillRect(xCoord + x1 * 30, yCoord + y1 * 30, gui.currentSlot.item.gridWidth * 30, gui.currentSlot.item.gridHeight * 30, new Color(0, 0, 0, 50));
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
		Item item = overlapsWith(1, 1, x, y);
		
		if(gui.currentSlot == null && item != null)
		{
			gui.currentSlot = new Slot(x, y, -1, gui);
			gui.currentSlot.item = item.clone();
			setItem(item.xCoord, item.yCoord, null);
		}
		else if(gui.currentSlot != null)
		{
			if(canBePlacedAt(x, y, gui.currentSlot.item))
			{
				setItem(x, y, gui.currentSlot.item);
				gui.currentSlot = null; 
			}			
		}	
	}

	public SlotGrid setItem(int x, int y, Item item) 
	{
		items[x][y] = item;
		if(gui != null) gui.inventory.setItem(id, item, x, y);
		return this;
	}
	
	public boolean setItemAndConfirm(int x, int y, Item item) 
	{
		if(canBePlacedAt(x, y, item))
		{
			setItem(x, y, item);
			return true;
		}
		return false;
	}
	
	public SlotGrid setItems(Item[][] items) 
	{
		this.items = items;
		if(gui != null) gui.inventory.setItemGrid(id, items);
		return this;
	}
	
	@Override
	public void tick() 
	{
		for(Item[] items : this.items)
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
	
	private Item overlapsWith(Item item, int x, int y)
	{
		if(item == null) return null;
		return overlapsWith(item.gridWidth, item.gridHeight, x, y);
	}
	
	private Item overlapsWith(int width, int height, int x, int y)
	{
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				if(items[i][j] != null)
				{
					if(overlapsWith(items[i][j].gridWidth, items[i][j].gridHeight, i, j, width, height, x, y))
					{
						Item item = items[i][j];
						item.xCoord = i;
						item.yCoord = j;
						return item;
					}
				}
			}
		}
		return null;
	}
	
	private boolean overlapsWith(int width1, int height1, int x1, int y1, int width2, int height2, int x2, int y2)
	{			
		for(int x = 0; x < width1; x++)
		{
			for(int y = 0; y < height1; y++)
			{
				int x3 = x + x1; int y3 = y + y1;
				
				for(int x4 = 0; x4 < width2; x4++)
				{
					for(int y4 = 0; y4 < height2; y4++)
					{
						if(x4 + x2 == x3 && y4 + y2 == y3)
						{							
							return true;
						}				
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean overlapsWith(Item item1, int x1, int y1, Item item2, int x2, int y2)
	{
		if(item1 == null || item2 == null) return false;
		return overlapsWith(item1.gridWidth, item1.gridHeight, x1, y1, item2.gridWidth, item2.gridHeight, x2, y2);
	}
	
	public boolean canBePlacedAt(int x, int y, Item item)
	{
		if(item == null) return true;
		if(x + item.gridWidth > gridWidth || y + item.gridHeight > gridHeight || x < 0 || y < 0)
		{
			return false;
		}
		return overlapsWith(item, x, y) == null;
	}
	
	public boolean addItemToGrid(Item item)
	{
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				if(setItemAndConfirm(i, j, item))
				{
					return true;
				}
			}
		}
		return false;
	}
}
