package vic.rpg.item;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;

import vic.rpg.Game;
import vic.rpg.level.INBTReadWrite;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.Drawable;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public abstract class Item extends Drawable implements Cloneable, INBTReadWrite
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
		
		if(Utils.getSide() == Side.CLIENT)
		{
			this.setTexture(Utils.readImageFromJar(image));
		}
	}
	
	public Item(String image, int id)
	{
		this(image, id, 30, 30);
	}
	
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
	
	public abstract String getItemName();
	
	public String[] getItemDescription()
	{
		return null;
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
		return (Item) super.clone();	
	}

	public Color getBgColor() 
	{
		return new Color(130, 91, 213, 180);
	}
	
	//TODO This is rendered behind the other slots. Need to add something like "postRender()".
	public void renderItemInformation(GL2 gl2, int x, int y)
	{
		DrawUtils.setGL(gl2);
		DrawUtils.setFont(new Font("Lucida Console", Font.PLAIN, 12));

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
