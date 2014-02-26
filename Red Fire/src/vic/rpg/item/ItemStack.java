package vic.rpg.item;

/**
 * An ItemStack is an extra container which contains an {@link Item}. Apart from {@link Item}, ItemStack is getting instanced. 
 * Also an ItemStack should never be {@code null}. It can have <b>no</b> {@link Item} type specified and the {@link #stackSize} <b>can be 0</b>.
 * If that's the case, {@link #isEmpty()} returns {@code true}.
 * @author Victorious3
 */
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
	
	/**
	 * Creates an ItemStack with the given item and stack size.
	 * @param item
	 * @param stackSize
	 */
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
	
	/**
	 * Creates an empty ItemStack.
	 */
	public ItemStack()
	{
		this.item = null;
		this.stackSize = 0;
	}
	
	/**
	 * Returns {@code true} if the item type is {@code null} or the stack size is 0.
	 * @return
	 */
	public boolean isEmpty()
	{
		return item == null || stackSize < 1;
	}
	
	/**
	 * Returns the item type or {@code null} if there is none.
	 * @return
	 */
	public Item getItem()
	{
		return item;
	}
	
	/**
	 * Returns the stack size.
	 * @return
	 */
	public int getStackSize()
	{
		return stackSize;
	}
	
	/**
	 * Sets the stack size. if it is less that 1, the item type is set to {@code null}.
	 * @param stackSize
	 */
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
