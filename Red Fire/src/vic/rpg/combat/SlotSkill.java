package vic.rpg.combat;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.GuiContainer;
import vic.rpg.gui.controls.GControl;
import vic.rpg.item.ItemFilter;
import vic.rpg.item.ItemStack;
import vic.rpg.item.Slot;
import vic.rpg.item.SlotGrid;
import vic.rpg.level.entity.living.Inventory;
import vic.rpg.registry.GameRegistry;
import vic.rpg.render.DrawUtils;

/**
 * A SkillSlot that can be used as a container for a {@link Skill}, an {@link ItemStack} or both.
 * It's mainly a graphical thing because all the storing is done by the underlying {@link Inventory} referenced in {@link #gui}.
 * @see GControl
 * @see Slot
 * @see SlotGrid
 * @author Victorious3
 */
public class SlotSkill extends GControl
{
	/**
	 * Id of referenced {@link ItemStack} from the underlying {@link Inventory} accessible via {@link #gui}. {@code null} if no {@link ItemStack ItemStacks} are supported.
	 *@see #gui
	 */
	private Integer itemID;
	/**
	 * Id of referenced {@link Skill} from the underlying {@link Inventory} accessible via {@link #gui}.
	 *@see #gui
	 */
	private Integer id;
	private ItemFilter filter;
	/**
	 * Gui reference.
	 */
	private GuiContainer gui;
	
	public SlotSkill(int xCoord, int yCoord, Integer id, GuiContainer gui) 
	{
		this(xCoord, yCoord, id, null, gui);
	}
	
	public SlotSkill(int xCoord, int yCoord, Integer id, Integer itemID, GuiContainer gui) 
	{
		this(xCoord, yCoord, id, itemID, null, gui);
	}
	
	public SlotSkill(int xCoord, int yCoord, Integer id, Integer itemID, ItemFilter filter, GuiContainer gui) 
	{
		super(xCoord, yCoord, 60, 60);
		this.id = id;
		this.itemID = itemID;
		this.filter = filter;
		this.gui = gui;
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
		else if(getSkill() != null)
		{
			DrawUtils.drawTexture(xCoord, yCoord, getSkill().getTexture());
		}
	
		DrawUtils.drawRect(xCoord, yCoord, width, height, Color.black);
		
		if(mouseHovered && GameRegistry.key.shiftPressed && !getItemStack().isEmpty())
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
				if(GameRegistry.key.shiftPressed) DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(0, 0, 0, 50));
				getItemStack().getItem().renderItemInformation(gl2, x, y);
			}
		}
		super.postRender(gl2, x, y);
	}
	
	@Override
	public void tick() 
	{
		if(itemID != null && !getItemStack().isEmpty() && getItemStack().getItem().isTicking) getItemStack().getItem().tick();
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
			else if(!getItemStack().isEmpty() && getCurrentItemStack().isEmpty() && GameRegistry.key.shiftPressed)
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
			else if(!getItemStack().isEmpty())
			{
				setItemStack(getItemStack().getItem().onItemCast(Game.getPlayer(), gui.inventory, getItemStack()));
			}
			else if(getSkill() != null)
			{
				getSkill().onSkillCast(Game.getPlayer());
			}
		}
		else if(mouseButton == MouseEvent.BUTTON3)
		{
			if(!getItemStack().isEmpty()) gui.inventory.onItemUse(itemID);
		}
	}

	/**
	 * Checks weather a given {@link ItemStack} can be placed in this SkillSlot.
	 * @see ItemFilter
	 * @param stack
	 * @return Boolean
	 */
	public boolean canBePlacedIn(ItemStack stack)
	{
		if(itemID == null) return false;
		if(stack.isEmpty()) return true;
		if(stack.getItem().gridWidth < 3 && stack.getItem().gridHeight < 3)
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
	 * @see #itemID
	 * @return ItemStack
	 */
	public ItemStack getItemStack()
	{
		if(itemID == null) return new ItemStack();
		else return gui.inventory.getItemStack(itemID);
	}
	
	/**
	 * Returns the {@link Skill} stored in the underlying {@link Inventory} or {@code null} if it's not set.
	 * @see #id
	 * @return Skill
	 */
	public Skill getSkill()
	{
		return gui.inventory.getSkill(id);
	}

	/**
	 * Sets the current {@link Skill} if the current {@link ItemStack} is empty.
	 * @see #id
	 * @see #itemID
	 * @param skill
	 */
	public void setSkill(Skill skill)
	{
		if(getItemStack().isEmpty()) gui.inventory.setSkill(id, skill);
	}
	
	/**
	 * Sets the current {@link ItemStack} if this SlotSkill supports {@link ItemStack ItemStacks}.
	 * Overrides any {@link Skill} present at that time.
	 * @see #id
	 * @see #itemID
	 * @param stack
	 */
	public void setItemStack(ItemStack stack)
	{	
		setSkill(null);
		gui.inventory.setItemStack(itemID, stack);		
	}
}
