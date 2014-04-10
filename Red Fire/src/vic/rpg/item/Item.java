package vic.rpg.item;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;

import vic.rpg.Game;
import vic.rpg.combat.SlotSkill;
import vic.rpg.level.INBTReadWrite;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.entity.living.Inventory;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.Drawable;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

/**
 * An item is a thing a player can pick up, use and play with it. It can be a weapon, a potion or anything else that can be carried in an {@link Inventory}.
 * @author Victorious3
 */
public abstract class Item extends Drawable implements INBTReadWrite
{
	protected String image; 
	
	public int id = 0;
	public int gridWidth = 1;
	public int gridHeight = 1;
	public int maxStackSize = 16;
	
	public boolean isUsable = false;
	public boolean isTakeable = true;
	public boolean isCastable = false;
	public boolean isStackable = false;
	
	public boolean isTicking = false;
	public boolean hasUpdated = false;
	
	//Used for inventory
	public int xCoord = 0;
	public int yCoord = 0;
	
	public Item(String image, int id, int width, int height)
	{
		super(width, height);
		this.image = image;
		this.id = id;
		
		this.gridWidth = (int)(width / 30);
		this.gridHeight = (int)(height / 30);
		
		if(Utils.getSide() == Side.CLIENT)
		{
			this.setTexture(Utils.readImage(image));
		}
	}
	
	public Item(String image, int id)
	{
		this(image, id, 30, 30);
	}
	
	/**
	 * Getting called every 0.2 seconds if this Item is rendered in a {@link Slot}, {@link SlotGrid} or {@link SlotSkill}. Disable it by setting {@link #isTicking} to {@code false}.
	 */
	public void tick()
	{
		
	}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args)
	{
		return tag;
	}
	
	@Override
	public void readFromNBT(CompoundTag tag, Object... args)
	{
		
	}
	
	/**
	 * The Item name rendered in the mouse hover.
	 * @return
	 */
	public abstract String getItemName();
	
	/**
	 * A set of special information that has to do with this Item.
	 * @return
	 */
	public String[] getItemDescription()
	{
		return null;
	}
	
	/**
	 * Called when this item is used by a right click. Disable it by setting {@link #isUsable} to {@code false}.
	 * @param entity
	 * @param i
	 * @param stack
	 * @return ItemStack - a modified version of stack
	 */
	public ItemStack onItemUse(EntityLiving entity, Inventory i, ItemStack stack)
	{
		return stack;
	}
	
	/**
	 * Called when this item is casted by clicking on a {@link SlotSkill}. Disable it by setting {@link #isCastable} to {@code false}.
	 * @param entity
	 * @param i
	 * @param stack
	 * @return ItemStack - a modified version of stack
	 */
	public ItemStack onItemCast(EntityLiving entity, Inventory i, ItemStack stack)
	{
		return stack;
	}
	
	/**
	 * Called when this item is dropped to the floor.
	 * @param entity
	 * @param i
	 * @param stack
	 * @return ItemStack - a modified version of stack
	 */
	public ItemStack onItemDrop(EntityLiving entity, Inventory i, ItemStack stack)
	{
		return stack;
	}
	
	/**
	 * Called when this item was picked up by {@code entity}.
	 * @param entity
	 * @param i
	 * @param stack
	 * @return
	 */
	public boolean onItemPickUp(EntityLiving entity, Inventory i, ItemStack stack)
	{
		return true;
	}
	
	/**
	 * Returns {@code true} if the given Item has the same id as this Item.
	 * @param item
	 * @return
	 */
	public boolean equals(Item item)
	{
		if(id == item.id) return true;
		return false;
	}

	/**
	 * A background color that is rendered in the back of this Item.
	 * @return
	 */
	public Color getBgColor() 
	{
		return new Color(130, 91, 213, 180);
	}
	
	/**
	 * Render a box that contains all the Item information. Modify it by changing {@link #getItemName()} and {@link #getItemDescription()}
	 * @param gl2
	 * @param x
	 * @param y
	 */
	public void renderItemInformation(GL2 gl2, int x, int y)
	{
		DrawUtils.setGL(gl2);
		DrawUtils.setFont(new Font("Monospaced", Font.PLAIN, 12));

		int width = 0;
		if(getItemDescription() != null)
		{
			for(String s : getItemDescription())
			{
				int width2 = (int)DrawUtils.getTextRenderer().getBounds(DrawUtils.removeFormatation(s)).getWidth();
				width = width2 > width ? width2 : width;
			}
		}
		int width3 = (int)DrawUtils.getTextRenderer().getBounds(DrawUtils.removeFormatation(getItemName())).getWidth();
		width = width3 > width ? width3 : width;
		
		if(x + 15 + width + 3 > Game.WIDTH) x -= width + 15 + 3;
		
		int height = 14 + (getItemDescription() != null ? getItemDescription().length * 14 : 0);
		
		DrawUtils.fillRect(x + 15, y + 15, width + 3, height + 3, new Color(17, 2, 16, 230));
		float lineWidth = DrawUtils.getLineWidth();
		DrawUtils.setLineWidth(4);
		DrawUtils.drawRect(x + 15, y + 15, width + 3, height + 3, new Color(41, 5, 96));
		DrawUtils.setLineWidth(lineWidth);
		DrawUtils.drawString(x + 18, y + 15 + 12, getItemName(), Color.white);
		
		if(getItemDescription() != null)
		{
			for(int i = 0; i < getItemDescription().length; i++)
			{
				DrawUtils.drawString(x + 18, y + 14 * i + 40, getItemDescription()[i], Color.white);
			}
		}
	}
}
