package vic.rpg.level.entity.living;

import java.util.ArrayList;
import java.util.HashMap;

import org.jnbt.CompoundTag;

import vic.rpg.item.Item;
import vic.rpg.item.SlotGrid;

//TODO Messy code!!!
public class Inventory 
{
	private HashMap<Integer, Item[][]> slotGrids = new HashMap<Integer, Item[][]>();
	private HashMap<Integer, Item> slots = new HashMap<Integer, Item>();
	
	public Inventory()
	{
		
	}
	
	public void add(int id, int width, int height)
	{
		slotGrids.put(id, new Item[width][height]);
	}
	
	public void add(int id)
	{
		slots.put(id, null);
	}
	
	public void addItems(int id, Item[][] items)
	{
		slotGrids.put(id, items);
	}
	
	public void addItem(int id, Item item)
	{
		slots.put(id, item);
	}

	public ArrayList<Item[][]> getAllItemGrids()
	{
		return new ArrayList<Item[][]>(slotGrids.values());
	}
	
	public ArrayList<Item> getAllItems()
	{
		return new ArrayList<Item>(slots.values());
	}
	
	public Item[][] getItemGrid(int id)
	{
		return slotGrids.get(id);
	}
	
	public Item getItem(int id)
	{
		return slots.get(id);
	}
	
	public boolean setStack(int id, Item item, int xCoord, int yCoord)
	{
		Item[][] grid = getItemGrid(id);
		
		SlotGrid temp = new SlotGrid(grid, 0, 0, -2, null);
		
		if(temp.canBePlacedAt(item, xCoord, yCoord))
		{
			temp.items[xCoord][yCoord] = item;
			addItems(id, temp.items.clone());
			temp = null;
			return true;
		}
		temp = null;
		return false;
	}
	
	public boolean setStack(int id, Item item)
	{		
		addItem(id, item);
		return true;
	}
	
	public void setStack(int id, Item[][] items) 
	{
		slotGrids.put(id, items);
	}
	
	public boolean addToInventory(Item item)
	{	
		for(int id : slotGrids.keySet())
		{
			SlotGrid temp = new SlotGrid(slotGrids.get(id), 0, 0, -2, null);
			if(temp.addItemToGrid(item))
			{
				addItems(id, temp.items);
				return true;
			}
		}
		for(int id : slots.keySet())
		{
			if(setStack(id, item))
			{
				return true;
			}
		}
		return false;
	}
	
	public void readFromNBT(CompoundTag tag)
	{
		
	}
	
	public CompoundTag writeToNBT(CompoundTag tag)
	{
		return tag;		
	}		
}
