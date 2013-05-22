package vic.rpg.item;

import java.awt.Color;

import org.jnbt.CompoundTag;

import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.render.Render;
import vic.rpg.utils.Utils;

public class Item extends Render implements Cloneable
{
	protected String image; 
	
	public int id = 0;
	public int gridWidth = 1;
	public int gridHeight = 1;
	public int maxStackSize = 200;
	
	public boolean isUsable = false;
	public boolean isTakeable = true;
	public boolean isCastable = false;

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
		
		if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{
			g2d.drawImage(Utils.readImageFromJar(image), 0, 0, null);
		}
	}
	
	public Item(String image, int id)
	{
		this(image, id, 30, 30);
	}
	
	public void tick()
	{
		
	}
	
	public CompoundTag writeToNBT(CompoundTag tag)
	{
		return tag;
	}
	
	public void readFromNBT(CompoundTag tag)
	{
		
	}
	
	public boolean onItemUse(EntityLiving entity, int x, int y)
	{
		return false;
	}
	
	public boolean onItemCast(EntityLiving entity, int x, int y)
	{
		return false;
	}
	
	public boolean onItemDrop(EntityLiving entity, int x, int y)
	{
		return true;
	}
	
	public void onItemSlotChange(){}
	
	public boolean onItemPickUp(EntityLiving entity, int x, int y)
	{
		return true;
	}
	
	public boolean equals(Item item)
	{
		if(id == item.id) return true;
		return false;
	}
	
	@Override
	public Item clone()
	{
		try {
			return (Item) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Color getBgColor() 
	{
		return new Color(130, 91, 213, 180);
	}
}
