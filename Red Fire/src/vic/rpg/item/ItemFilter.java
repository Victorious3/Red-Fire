package vic.rpg.item;

public class ItemFilter 
{
	public boolean isItemValid(Item item)
	{
		return true;
	}
		
	public static class SimpleItemFilter extends ItemFilter
	{
		public Class<? extends Item> allowedItem;
		
		/**
		 * Creates a simple filter that checks if the given item is an instance of the provided Class.
		 * @param Class<? extends Item> 
		 * @return SimpleItemFilter
		 */
		public SimpleItemFilter(Class<? extends Item> allowedItem)
		{
			this.allowedItem = allowedItem;
		}

		@Override
		public boolean isItemValid(Item item) 
		{
			return allowedItem.isInstance(item);
		}
		
	}
}