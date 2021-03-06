package vic.rpg.item;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

import vic.rpg.client.render.DrawUtils;
import vic.rpg.gui.GuiContainer;
import vic.rpg.gui.controls.GControl;
import vic.rpg.world.entity.living.Inventory;

/**
 * A Slot that can be used as a container for an {@link ItemStack}. The width and height of the Slot can be adjusted to allow bigger {@link Item Items}.
 * It's mainly a graphical thing because all the storing is done by the underlying {@link Inventory} referenced in {@link #gui}.
 * @see GControl
 * @see Slot
 * @see SlotGrid
 * @author Victorious3
 */
public class Slot extends GControl implements Cloneable
{		
	/**
	 * Gui reference.
	 */
	public GuiContainer gui;
	public int sWidth = 1;
	public int sHeight = 1;
	public ItemFilter filter;
	public boolean acceptOtherSizes = false;
	/**
	 * Id of referenced {@link ItemStack} from the underlying {@link Inventory} accessible via {@link #gui}.
	 *@see #gui
	 */
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
	
	/**
	 * Sets the current {@link ItemStack}
	 * @param stack
	 * @return this
	 */
	public Slot setItemStack(ItemStack stack)
	{
		gui.inventory.setItemStack(id, stack);
		return this;
	}
	
	/**
	 * Set the {@link ItemFilter} of this slot.
	 * @param filter
	 * @return this
	 */
	public Slot setFilter(ItemFilter filter)
	{
		this.filter = filter;
		return this;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{
		DrawUtils.setGL(gl2);
		
		if(getItemStack().isEmpty()) DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(112, 112, 112, 180));
		else DrawUtils.fillRect(xCoord, yCoord, width, height, getItemStack().getItem().getBgColor());
		
		if(!getItemStack().isEmpty())
		{
			DrawUtils.drawTexture(xCoord + (width - getItemStack().getItem().getWidth()) / 2, yCoord + (height - getItemStack().getItem().getHeight()) / 2, getItemStack().getItem().getTexture());
			getItemStack().getItem().render(gl2);
			DrawUtils.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
			if(getItemStack().getStackSize() > 1) DrawUtils.drawString(xCoord + width - DrawUtils.getFormattedStringLenght(String.valueOf(getItemStack().getStackSize())) - 2, yCoord + height - 2, String.valueOf(getItemStack().getStackSize()), Color.black);
		}
	
		DrawUtils.drawRect(xCoord, yCoord, width, height, Color.black);
		
		if(mouseHovered && !getItemStack().isEmpty())
		{		
			gui.isSlotHovered = true;
		}
	}	

	@Override
	public void postRender(GL2 gl2, int x, int y) 
	{
		if(this.mouseHovered)
		{
			if(!getCurrentItemStack().isEmpty() && canBePlacedIn(getCurrentItemStack()))
			{
				DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
			}
			else if(!getItemStack().isEmpty())
			{
				DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
				getItemStack().getItem().renderItemInformation(gl2, x, y);
			}
		}
		super.postRender(gl2, x, y);	
	}

	@Override
	public void tick() 
	{
		if(!getItemStack().isEmpty() && getItemStack().getItem().isTicking) getItemStack().getItem().tick();
	}
	
	@Override
	public void onClickStart(int x, int y, int mouseButton) 
	{
		super.onClickStart(x, y, mouseButton);
		
		if(mouseButton == MouseEvent.BUTTON1)
		{
			if(!getCurrentItemStack().isEmpty() && getItemStack().isEmpty())
			{
				if(canBePlacedIn(getCurrentItemStack()))
				{
					setItemStack(getCurrentItemStack());
					gui.inventory.setItemStack(gui.currentSlot.id, new ItemStack());
					gui.inventory.updateInventory();
				}
			}
			else if(!getItemStack().isEmpty() && getCurrentItemStack().isEmpty())
			{									
				gui.inventory.setItemStack(gui.currentSlot.id, getItemStack());
				setItemStack(new ItemStack());
				gui.inventory.updateInventory();
			}
			else if(!getItemStack().isEmpty() && !getCurrentItemStack().isEmpty())
			{
				if(canBePlacedIn(getCurrentItemStack()))
				{	
					ItemStack stack = getItemStack();
					setItemStack(getCurrentItemStack());
					gui.inventory.setItemStack(gui.currentSlot.id, stack);
					gui.inventory.updateInventory();
				}
			}
		}
		else if(mouseButton == MouseEvent.BUTTON3)
		{
			if(!getItemStack().isEmpty()) gui.inventory.onItemUse(id);
		}
	}

	/**
	 * Checks weather a given {@link ItemStack} can be placed in this Slot.
	 * @see ItemFilter
	 * @param stack
	 * @return Boolean
	 */
	public boolean canBePlacedIn(ItemStack stack)
	{
		if(stack.isEmpty()) return true;
		if(((stack.getItem().gridWidth == this.sWidth && stack.getItem().gridHeight == this.sHeight) && !acceptOtherSizes) || ((stack.getItem().gridWidth <= this.sWidth && stack.getItem().gridHeight <= this.sHeight) && acceptOtherSizes))
		{	
			if(filter != null){if(!filter.isItemValid(stack.getItem())) return false;}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the {@link ItemStack} currently active in {@link #gui}.
	 * @return ItemStack
	 */
	private ItemStack getCurrentItemStack()
	{
		return gui.inventory.getItemStack(gui.currentSlot.id);
	}
	
	/**
	 * Returns the {@link ItemStack} stored in the underlying {@link Inventory}.
	 * @see #id
	 * @return ItemStack
	 */
	public ItemStack getItemStack()
	{
		return gui.inventory.getItemStack(id);
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
