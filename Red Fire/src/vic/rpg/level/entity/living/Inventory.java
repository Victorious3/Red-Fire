package vic.rpg.level.entity.living;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.Game;
import vic.rpg.Init;
import vic.rpg.combat.Skill;
import vic.rpg.item.Item;
import vic.rpg.item.ItemStack;
import vic.rpg.level.INBTReadWrite;
import vic.rpg.level.entity.EntityEvent;
import vic.rpg.listener.EntityEventListener;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet13InventoryUpdate;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class Inventory implements INBTReadWrite, EntityEventListener
{
	private HashMap<Integer, ItemStack[][]> itemGrids = new HashMap<Integer, ItemStack[][]>();
	private HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	private HashMap<Integer, Skill> skills = new HashMap<Integer, Skill>();
	
	public EntityLiving parentEntity;
	
	@Init(side = Side.BOTH)
	public static void init()
	{
		EntityEvent.registerEntityEvent(new InventoryEvent());
	}
	
	public Inventory(EntityLiving parentEntity)
	{
		this.parentEntity = parentEntity;
		if(parentEntity != null) this.parentEntity.addEventListener(this);
	}
	
	public ItemStack[][] createEmptyItemStackGrid(int width, int height)
	{
		ItemStack[][] grid = new ItemStack[width][height];
		for(int i = 0; i < width; i++)
		{
			ItemStack[] row = new ItemStack[height];
			Arrays.fill(row, new ItemStack());
			grid[i] = row;
		}
		return grid;
	}
	
	public void addSkill(int id)
	{
		skills.put(id, null);
	}
	
	public void setSkill(int id, Skill skill)
	{
		skills.put(id, skill);
	}
	
	public void addItemStackGrid(int id, int width, int height)
	{
		itemGrids.put(id, createEmptyItemStackGrid(width, height));
	}
	
	public void addItemStack(int id)
	{
		items.put(id, new ItemStack());
	}
	
	public void setItemStackGrid(int id, ItemStack[][] items)
	{
		itemGrids.put(id, items);
	}
	
	public void setItemStack(int id, ItemStack item)
	{
		if(item == null) throw new NullPointerException("An ItemStack cannot be null! Use an empty ItemStack instead!");
		items.put(id, item);
	}

	public ArrayList<ItemStack[][]> getAllItemStackGrids()
	{
		return new ArrayList<ItemStack[][]>(itemGrids.values());
	}
	
	public ArrayList<ItemStack> getAllItemStacks()
	{
		return new ArrayList<ItemStack>(items.values());
	}
	
	public ItemStack[][] getItemStackGrid(int id)
	{
		return itemGrids.get(id);
	}
	
	public ItemStack getItemStack(Integer id)
	{
		return items.get(id);
	}
	
	public Skill getSkill(Integer id)
	{
		return skills.get(id);
	}
	
	public ItemStack overlapsWith(ItemStack[][] grid, ItemStack stack, int x, int y)
	{
		if(stack.isEmpty()) return new ItemStack();
		return overlapsWith(grid, stack.getItem().gridWidth, stack.getItem().gridHeight, x, y);
	}
	
	public ItemStack overlapsWith(ItemStack[][] grid, int width, int height, int x, int y)
	{
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				if(!grid[i][j].isEmpty())
				{
					if(overlapsWith(grid[i][j].getItem().gridWidth, grid[i][j].getItem().gridHeight, i, j, width, height, x, y))
					{
						ItemStack item = grid[i][j];
						item.xCoord = i;
						item.yCoord = j;
						return item;
					}
				}
			}
		}
		return new ItemStack();
	}
	
	private boolean overlapsWith(int width1, int height1, int x1, int y1, int width2, int height2, int x2, int y2)
	{			
		for(int x = 0; x < width1; x++)
		{
			for(int y = 0; y < height1; y++)
			{
				int x3 = x + x1; int y3 = y + y1;
				
				for(int x4 = 0; x4 < width2; x4++)
				{
					for(int y4 = 0; y4 < height2; y4++)
					{
						if(x4 + x2 == x3 && y4 + y2 == y3)
						{							
							return true;
						}				
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private boolean overlapsWith(Item item1, int x1, int y1, Item item2, int x2, int y2)
	{
		if(item1 == null || item2 == null) return false;
		return overlapsWith(item1.gridWidth, item1.gridHeight, x1, y1, item2.gridWidth, item2.gridHeight, x2, y2);
	}
	
	public boolean canBePlacedAt(ItemStack[][] grid, int x, int y, ItemStack stack)
	{
		if(stack.isEmpty()) return true;
		if(x + stack.getItem().gridWidth > grid.length || y + stack.getItem().gridHeight > grid[0].length || x < 0 || y < 0)
		{
			return false;
		}
		return overlapsWith(grid, stack, x, y).isEmpty();
	}
	
	public boolean setItemGrid(int id, ItemStack item, int xCoord, int yCoord)
	{
		ItemStack[][] grid = getItemStackGrid(id);

		if(canBePlacedAt(grid, xCoord, yCoord, item))
		{		
			grid[xCoord][yCoord] = item;
			setItemStackGrid(id, grid);
			return true;
		}
		return false;
	}
	
	public void updateInventory()
	{
		if(Utils.getSide() == Side.CLIENT)
		{
			Game.packetHandler.addPacketToSendingQueue(new Packet13InventoryUpdate(this));
		}
		else if(this.parentEntity != null) Server.server.broadcast(new Packet7Entity(this.parentEntity, Packet7Entity.MODE_UPDATE));
	}
	
	public boolean addItemToGrid(int id, ItemStack stack)
	{
		for(int i = 0; i < getItemStackGrid(id).length; i++)
		{
			for(int j = 0; j < getItemStackGrid(id)[0].length; j++)
			{
				if(setItemGrid(id, stack, i, j)) return true;
			}
		}
		return false;
	}
	
	public boolean addToInventory(ItemStack stack)
	{	
		for(int id : itemGrids.keySet())
		{
			if(addItemToGrid(id, stack))
			{
				return true;
			}
		}
		
		for(int id : items.keySet())
		{
			if(getItemStack(id).isEmpty())
			{
				setItemStack(id, stack);
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readFromNBT(CompoundTag tag, Object... args)
	{	
		Map<String, Tag> tagMap = tag.getValue();
		Map<String, Tag> inventoryMap = (Map<String, Tag>) tagMap.get("inventory").getValue();
		List<CompoundTag> slotList = (List<CompoundTag>) inventoryMap.get("slotListTag").getValue();	
		List<CompoundTag> slotGridList = (List<CompoundTag>) inventoryMap.get("slotGridListTag").getValue();
		List<CompoundTag> skillList = (List<CompoundTag>) inventoryMap.get("skillListTag").getValue();
		
		for(CompoundTag t : slotList)
		{
			Map<String, Tag> tMap = t.getValue();
			int slotID = (Integer)tMap.get("slotID").getValue();
			Tag idTag = tMap.get("id");
			
			if(idTag != null)
			{
				int id = (Integer)idTag.getValue();
				int stackSize = (Integer)tMap.get("stackSize").getValue();
				Item i = LevelRegistry.itemRegistry.get(id);
				i.readFromNBT((CompoundTag) tMap.get("data"));
				items.put(slotID, new ItemStack(i, stackSize));		
			}
			else items.put(slotID, new ItemStack());
		}
		
		for(CompoundTag t : skillList)
		{
			Map<String, Tag> tMap = t.getValue();
			int slotID = (Integer)tMap.get("slotID").getValue();
			Tag idTag = tMap.get("id");
			
			if(idTag != null)
			{
				int id = (Integer)idTag.getValue();
				Skill s = LevelRegistry.skillRegistry.get(id).clone();
				skills.put(slotID, s);		
			}
			else skills.put(slotID, null);
		}
		
		for(CompoundTag t : slotGridList)
		{
			Map<String, Tag> tMap = t.getValue();
			int slotID = (Integer)tMap.get("slotGridID").getValue();
			ItemStack[][] its = createEmptyItemStackGrid((Integer)tMap.get("width").getValue(), (Integer)tMap.get("height").getValue());
			
			for(Tag t2 : (List<Tag>)tMap.get("itemList").getValue())
			{
				CompoundTag t3 = (CompoundTag)t2;
				Map<String, Tag> itemMap = t3.getValue();
				
				String coords = (String)itemMap.get("coords").getValue();
				int x = Integer.parseInt(coords.split("-")[0]);
				int y = Integer.parseInt(coords.split("-")[1]);
				int stackSize = (Integer)itemMap.get("stackSize").getValue();
				
				Item it = LevelRegistry.itemRegistry.get((Integer)itemMap.get("id").getValue());
				it.readFromNBT((CompoundTag)itemMap.get("data"));
				its[x][y] = new ItemStack(it, stackSize);
			}
			itemGrids.put(slotID, its);
		}
	}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args)
	{
		HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
		tagMap.putAll(tag.getValue());
		
		HashMap<String, Tag> inventoryMap = new HashMap<String, Tag>();
		ArrayList<CompoundTag> slotList = new ArrayList<CompoundTag>();
		ArrayList<CompoundTag> skillList = new ArrayList<CompoundTag>();
		ArrayList<CompoundTag> slotGridList = new ArrayList<CompoundTag>();
		
		for(int id : items.keySet())
		{
			HashMap<String, Tag> slotMap = new HashMap<String, Tag>();
			Item it = items.get(id).getItem();
			slotMap.put("slotID", new IntTag("slotID", id));
			
			if(it != null) 
			{
				slotMap.put("id", new IntTag("id", it.id));
				slotMap.put("stackSize", new IntTag("stackSize", items.get(id).getStackSize()));
				slotMap.put("data", it.writeToNBT(new CompoundTag("data", new HashMap<String, Tag>())));
			}
			
			CompoundTag slotTag = new CompoundTag("slotTag", slotMap);
			slotList.add(slotTag);
		}
		
		for(int id : skills.keySet())
		{
			HashMap<String, Tag> skillMap = new HashMap<String, Tag>();
			Skill s = skills.get(id);
			skillMap.put("slotID", new IntTag("slotID", id));
			
			if(s != null) 
			{
				skillMap.put("id", new IntTag("id", s.id));
			}
			
			CompoundTag skillTag = new CompoundTag("skillTag", skillMap);
			skillList.add(skillTag);
		}
		
		for(int id : itemGrids.keySet())
		{
			ItemStack[][] its = itemGrids.get(id);
			HashMap<String, Tag> slotGridMap = new HashMap<String, Tag>();
			slotGridMap.put("slotGridID", new IntTag("slotGridID", id));
			slotGridMap.put("width", new IntTag("width", its.length));
			slotGridMap.put("Height", new IntTag("height", its[0].length));
			ArrayList<CompoundTag> itemList = new ArrayList<CompoundTag>();
			
			for(int i = 0; i < its.length; i++)
			{
				for(int j = 0; j < its[0].length; j++)
				{
					HashMap<String, Tag> itemMap = new HashMap<String, Tag>();
					Item it = its[i][j].getItem();
					if(it != null) 
					{
						itemMap.put("coords", new StringTag("coords", i + "-" + j));		
						itemMap.put("id", new IntTag("id", it.id));
						itemMap.put("stackSize", new IntTag("stackSize", its[i][j].getStackSize()));
						itemMap.put("data", it.writeToNBT(new CompoundTag("data", new HashMap<String, Tag>())));
						itemList.add(new CompoundTag("itemTag", itemMap));
					}					
				}
			}
			
			ListTag itemListTag = new ListTag("itemList", CompoundTag.class, itemList);		
			slotGridMap.put("itemList", itemListTag);
			CompoundTag slotGridTag = new CompoundTag("slotGridTag", slotGridMap);
			slotGridList.add(slotGridTag);		
		}
		
		ListTag slotListTag = new ListTag("slotListTag", CompoundTag.class, slotList);
		ListTag slotGridListTag = new ListTag("slotGridListTag", CompoundTag.class, slotGridList);
		ListTag skillListTag = new ListTag("skillListTag", CompoundTag.class, skillList);
		inventoryMap.put("slotListTag", slotListTag);
		inventoryMap.put("slotGridListTag", slotGridListTag);
		inventoryMap.put("skillListTag", skillListTag);
		CompoundTag inventoryTag = new CompoundTag("inventory", inventoryMap);
		tagMap.put("inventory", inventoryTag);
		
		return new CompoundTag(tag.getName(), tagMap);	
	}
	
	//Inventory event
	public static class InventoryEvent extends EntityEvent
	{
		public InventoryEvent(int mode, int id, int sType, int gx, int gy) 
		{
			super(Side.SERVER, 2);
			this.putData("mode", mode);
		}

		public InventoryEvent() 
		{
			super(Side.SERVER, 2);
		}
	}

	public void onItemUse(int id, int x, int y)
	{
		getItemStack(id).getItem().onItemUse(parentEntity, this, x, y);
		parentEntity.postEvent(new InventoryEvent(0, id, 0, 0, 0));
	}
	
	public void onItemUse(int id, int gx, int gy, int x, int y)
	{
		overlapsWith(getItemStackGrid(id), 1, 1, gx, gy).getItem().onItemUse(parentEntity, this, x, y);
		parentEntity.postEvent(new InventoryEvent(0, id, 1, gx, gy));
	}

	@Override
	public void onEventReceived(EntityEvent e) 
	{
		if(e instanceof InventoryEvent)
		{
			if((Integer)e.getData("mode") == 0)
			{
				if((Integer)e.getData("sType") == 0) onItemUse((Integer)e.getData("id"), 0, 0);
				if((Integer)e.getData("sType") == 1) onItemUse((Integer)e.getData("id"), (Integer)e.getData("gx"), (Integer)e.getData("gy"), 0, 0);
			}
		}
	}

	@Override public void onEventPosted(EntityEvent e) {}

	@Override
	public Priority getPriority() 
	{
		return Priority.PRIORITY_MEDIUM;
	}
}
