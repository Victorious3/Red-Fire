package vic.rpg.item;

public class ItemStack 
{
	private Item item;
	private int stackSize;
	
	public int slotID;
	public int xCoord;
	public int yCoord;
	
	public ItemStack(Item item)
	{
		this.item = item;
		this.stackSize = 1;
	}
	
	public ItemStack(Item item, int stackSize)
	{
		this.item = item;
		if(item.isStackable)
		{
			if(stackSize < item.maxStackSize) this.stackSize = stackSize;
			else this.stackSize = item.maxStackSize;
		}
		else this.stackSize = 1;
	}
	
	public ItemStack()
	{
		this.item = null;
		this.stackSize = 0;
	}
	
	public boolean isEmpty()
	{
		return item == null;
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public int getStackSize()
	{
		return stackSize;
	}
	
	public void setStackSize(int stackSize)
	{
		if(getItem().isStackable) this.stackSize = stackSize;
		if(this.stackSize < 1) 
		{
			this.item = null;
			this.stackSize = 0;
		}
	}
}
