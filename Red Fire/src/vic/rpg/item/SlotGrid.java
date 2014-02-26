package vic.rpg.item;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

import vic.rpg.gui.GuiContainer;
import vic.rpg.gui.controls.GControl;
import vic.rpg.level.entity.living.Inventory;
import vic.rpg.render.DrawUtils;

/**
 * A SlotGrid is basically a number of {@link Slot Slots} organized in a grid. It can hold more than one {@link ItemStack}.
 * It's mainly a graphical thing because all the storing is done by the underlying {@link Inventory} referenced in {@link #gui}.
 * @see GControl
 * @see Slot
 * @see SlotGrid
 * @author Victorious3
 */
public class SlotGrid extends GControl
{	
	/**
	 * Gui reference.
	 */
	public GuiContainer gui;
	
	public int gridWidth = 1;
	public int gridHeight = 1;
	/**
	 * Id of referenced {@link ItemStack ItemStack[][]} from the underlying {@link Inventory} accessible via {@link #gui}.
	 *@see #gui
	 */
	public int id;
	
	public SlotGrid(int xCoord, int yCoord, int width, int height, int id, GuiContainer gui) 
	{
		super(xCoord, yCoord, width * 30, height * 30);
		
		this.gridWidth = width;
		this.gridHeight = height;
		this.gui = gui;
		this.id = id;
	}
	
	public SlotGrid(ItemStack[][] items, int xCoord, int yCoord, int id, GuiContainer gui)
	{
		super(xCoord, yCoord, items.length * 30, items[0].length * 30);
		
		this.gridWidth = items.length;
		this.gridHeight = items[0].length;
		
		gui.inventory.setItemStackGrid(id, items);
		this.gui = gui;
		this.id = id;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{		
		DrawUtils.setGL(gl2);
		for(ItemStack[] stacks : this.gui.inventory.getItemStackGrid(id))
		{
			for(ItemStack stack : stacks)
			{
				if(!stack.isEmpty()) stack.getItem().render(gl2);
			}
		}
		
		for(int i = 0; i < gridWidth; i++)
		{
			for(int j = 0; j < gridHeight; j++)
			{
				ItemStack stack = gui.inventory.overlapsWith(gui.inventory.getItemStackGrid(id), 1, 1, i, j);
				if(!gui.inventory.getItemStackGrid(id)[i][j].isEmpty())
				{
					DrawUtils.fillRect(xCoord + i * 30, yCoord + j * 30, 30, 30, gui.inventory.getItemStackGrid(id)[i][j].getItem().getBgColor());
				}
				else if(!stack.isEmpty()) DrawUtils.fillRect(xCoord + i * 30, yCoord + j * 30, 30, 30, stack.getItem().getBgColor());
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
				if(!gui.inventory.getItemStackGrid(id)[i][j].isEmpty()) 
				{
					DrawUtils.drawTexture(xCoord + i * 30, yCoord + j * 30, gui.inventory.getItemStackGrid(id)[i][j].getItem().getTexture());
					DrawUtils.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
					int stackSize = gui.inventory.getItemStackGrid(id)[i][j].getStackSize();
					if(stackSize > 1) DrawUtils.drawString(xCoord + i * 30 + 30 - DrawUtils.getFormattedStringLenght(String.valueOf(stackSize)) - 2, yCoord + j * 30 + 28, String.valueOf(stackSize), Color.black);
				}
			}
		}		
	}

	@Override
	public void postRender(GL2 gl2, int x, int y) 
	{
		if(mouseHovered)
		{
			ItemStack stack = gui.inventory.overlapsWith(getItemGrid(), 1, 1, (x - xCoord) / 30, (y - yCoord) / 30);
			
			if(!getCurrentStack().isEmpty())
			{
				int x1 = (x - xCoord) / 30;
				int y1 = (y - yCoord) / 30;
				
				if(gui.inventory.canBePlacedAt(getItemGrid(), x1, y1, getCurrentStack()))
				{
					DrawUtils.fillRect(xCoord + x1 * 30, yCoord + y1 * 30, getCurrentStack().getItem().gridWidth * 30, getCurrentStack().getItem().gridHeight * 30, new Color(0, 0, 0, 50));
				}
			}
			else if(!stack.isEmpty())
			{
				gui.isSlotHovered = true;
				DrawUtils.fillRect(xCoord + stack.xCoord * 30, yCoord + stack.yCoord * 30, stack.getItem().gridWidth * 30, stack.getItem().gridHeight * 30, new Color(0, 0, 0, 50));
				stack.getItem().renderItemInformation(gl2, x, y);
			}
		}
		super.postRender(gl2, x, y);
	}

	@Override
	public void onClickStart(int x, int y, int mouseButton) 
	{
		super.onClickStart(x, y, mouseButton);
		
		x -= xCoord;
		y -= yCoord;
		
		if(mouseButton == MouseEvent.BUTTON1) onClickedAtCoord(x / 30, y / 30);
		else if(mouseButton == MouseEvent.BUTTON3)
		{
			ItemStack stack = gui.inventory.overlapsWith(gui.inventory.getItemStackGrid(id), 1, 1, x / 30, y / 30);
			if(!stack.isEmpty())
			{
				gui.inventory.onItemUse(id, x / 30, y / 30);
			}
		}		
	}
	
	/**
	 * A helper method that does react based on the x and y coordinate in the {@link ItemStack ItemStack[][]} grid. 
	 * @param x
	 * @param y
	 */
	private void onClickedAtCoord(int x, int y)
	{
		ItemStack stack = gui.inventory.overlapsWith(getItemGrid(), 1, 1, x, y);
		
		if(getCurrentStack().isEmpty() && !stack.isEmpty())
		{
			gui.inventory.setItemStack(gui.currentSlot.id, stack);
			setItemStack(stack.xCoord, stack.yCoord, new ItemStack());
			gui.inventory.updateInventory();
		}
		else if(!getCurrentStack().isEmpty())
		{
			if(gui.inventory.canBePlacedAt(getItemGrid(), x, y, getCurrentStack()))
			{
				setItemStack(x, y, getCurrentStack());
				gui.inventory.setItemStack(gui.currentSlot.id, new ItemStack());
				gui.inventory.updateInventory();
			}			
		}	
	}

	/**
	 * Sets the {@link ItemStack} on the given coordinates.
	 * @see #setItemStackAndConfirm(int, int, ItemStack)
	 * @param x
	 * @param y
	 * @param item
	 * @return this
	 */
	public SlotGrid setItemStack(int x, int y, ItemStack item) 
	{
		if(gui != null) gui.inventory.setItemStackGrid(id, item, x, y);
		return this;
	}
	
	/**
	 * Sets the {@link ItemStack} on the given coordinates if {@link Inventory#canBePlacedAt(ItemStack[][], int, int, ItemStack)} does allow it.
	 * @see #setItemStack(int, int, ItemStack)
	 * @param x
	 * @param y
	 * @param item
	 * @return Boolean
	 */
	public boolean setItemStackAndConfirm(int x, int y, ItemStack item) 
	{
		if(gui.inventory.canBePlacedAt(gui.inventory.getItemStackGrid(id), x, y, item))
		{
			setItemStack(x, y, item);
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the whole {@link ItemStack ItemStack[][]} grid.
	 * @param items
	 * @return
	 */
	public SlotGrid setItemStacks(ItemStack[][] items) 
	{
		gui.inventory.setItemStackGrid(id, items);
		return this;
	}
	
	/**
	 * Returns the {@link ItemStack} currently active in {@link #gui}.
	 * @return ItemStack
	 */
	private ItemStack getCurrentStack()
	{
		return gui.inventory.getItemStack(gui.currentSlot.id);
	}
	
	/**
	 * Returns the {@link ItemStack ItemStack[][]} stored in the underlying {@link Inventory}.
	 * @see #id
	 * @return ItemStack[][]
	 */
	private ItemStack[][] getItemGrid()
	{
		return gui.inventory.getItemStackGrid(id);
	}
	
	@Override
	public void tick() 
	{
		for(ItemStack[] stacks : this.gui.inventory.getItemStackGrid(id))
		{
			for(ItemStack stack : stacks)
			{
				if(!stack.isEmpty())
				{
					if(stack.getItem().isTicking) stack.getItem().tick();
				}			
			}
		}
	}
}
