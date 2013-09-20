package vic.rpg.level.entity.living;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.Game;
import vic.rpg.item.Item;
import vic.rpg.item.SlotGrid;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.server.packet.Packet8PlayerUpdate;
import vic.rpg.utils.Utils;

//TODO Messy code!!!
public class Inventory 
{
	private HashMap<Integer, Item[][]> slotGrids = new HashMap<Integer, Item[][]>();
	private HashMap<Integer, Item> slots = new HashMap<Integer, Item>();

	public EntityLiving parentEntity;
	
	public Inventory(EntityLiving parentEntity)
	{
		this.parentEntity = parentEntity;
	}
	
	public void add(int id, int width, int height)
	{
		slotGrids.put(id, new Item[width][height]);
	}
	
	public void add(int id)
	{
		slots.put(id, null);
	}
	
	public void addItemGrid(int id, Item[][] items)
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
	
	public boolean setItem(int id, Item item, int xCoord, int yCoord)
	{
		Item[][] grid = getItemGrid(id);
		
		SlotGrid temp = new SlotGrid(grid, 0, 0, -2, null);
		
		if(temp.setItemAndConfirm(xCoord, yCoord, item))
		{		
			setItemGrid(id, temp.items.clone());
			temp = null;
			return true;
		}
		setItemGrid(id, temp.items.clone());
		temp = null;
		return false;
	}
	
	public boolean setItem(int id, Item item)
	{		
		addItem(id, item);
		if(this.parentEntity != null)
		{
			if(Utils.getSide().equals(Utils.SIDE_CLIENT))
			{
				System.out.println("Sending Slot " + id + " with item " + item);
				Game.packetHandler.addPacketToSendingQueue(new Packet8PlayerUpdate((EntityPlayer)this.parentEntity, Packet7Entity.MODE_UPDATE));
			}
			else Server.server.broadcast(new Packet7Entity(this.parentEntity, Packet7Entity.MODE_UPDATE));
		}
		return true;
	}
	
	public void setItemGrid(int id, Item[][] items) 
	{
		addItemGrid(id, items);
		if(this.parentEntity != null)
		{
			if(Utils.getSide().equals(Utils.SIDE_CLIENT))
			{
				System.out.println("Sending SlotGrid " + id);
				Game.packetHandler.addPacketToSendingQueue(new Packet8PlayerUpdate((EntityPlayer)this.parentEntity, Packet7Entity.MODE_UPDATE));
			}
			else Server.server.broadcast(new Packet7Entity(this.parentEntity, Packet7Entity.MODE_UPDATE));
		}
	}
	
	public boolean addToInventory(Item item)
	{	
		for(int id : slotGrids.keySet())
		{
			SlotGrid temp = new SlotGrid(slotGrids.get(id), 0, 0, -2, null);
			if(temp.addItemToGrid(item))
			{
				setItemGrid(id, temp.items);
				return true;
			}
		}
		for(int id : slots.keySet())
		{
			if(setItem(id, item))
			{
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void readFromNBT(CompoundTag tag)
	{	
		Map<String, Tag> tagMap = tag.getValue();
		Map<String, Tag> inventoryMap = (Map<String, Tag>) tagMap.get("inventory").getValue();
		List<CompoundTag> slotList = (List<CompoundTag>) inventoryMap.get("slotListTag").getValue();	
		List<CompoundTag> slotGridList = (List<CompoundTag>) inventoryMap.get("slotGridListTag").getValue();
		
		for(CompoundTag t : slotList)
		{
			Map<String, Tag> tMap = t.getValue();
			int slotID = (Integer)tMap.get("slotID").getValue();
			Tag idTag = tMap.get("id");
			
			if(idTag != null)
			{
				int id = (Integer)idTag.getValue();
				Item i = LevelRegistry.itemRegistry.get(id).clone();
				i.readFromNBT((CompoundTag) tMap.get("data"));
				slots.put(slotID, i);		
			}
			else slots.put(slotID, null);
		}
		
		for(CompoundTag t : slotGridList)
		{
			Map<String, Tag> tMap = t.getValue();
			int slotID = (Integer)tMap.get("slotGridID").getValue();
			Item[][] its = new Item[(Integer)tMap.get("width").getValue()][(Integer)tMap.get("height").getValue()];
			
			for(Tag t2 : (List<Tag>)tMap.get("itemList").getValue())
			{
				CompoundTag t3 = (CompoundTag)t2;
				Map<String, Tag> itemMap = t3.getValue();
				
				String coords = (String)itemMap.get("coords").getValue();
				int x = Integer.parseInt(coords.split("-")[0]);
				int y = Integer.parseInt(coords.split("-")[1]);
				
				Item it = LevelRegistry.itemRegistry.get((Integer)itemMap.get("id").getValue()).clone();
				it.readFromNBT((CompoundTag)itemMap.get("data"));
				its[x][y] = it;
			}
			slotGrids.put(slotID, its);
		}
	}
	
	public CompoundTag writeToNBT(CompoundTag tag)
	{
		HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
		tagMap.putAll(tag.getValue());
		
		HashMap<String, Tag> inventoryMap = new HashMap<String, Tag>();
		ArrayList<CompoundTag> slotList = new ArrayList<CompoundTag>();		
		ArrayList<CompoundTag> slotGridList = new ArrayList<CompoundTag>();
		
		for(int id : slots.keySet())
		{
			HashMap<String, Tag> slotMap = new HashMap<String, Tag>();
			Item it = slots.get(id);
			slotMap.put("slotID", new IntTag("slotID", id));
			
			if(it != null) 
			{
				slotMap.put("id", new IntTag("id", it.id));
				slotMap.put("data", it.writeToNBT(new CompoundTag("data", new HashMap<String, Tag>())));
			}
			
			CompoundTag slotTag = new CompoundTag("slotTag", slotMap);
			slotList.add(slotTag);
		}
		
		for(int id : slotGrids.keySet())
		{
			Item[][] its = slotGrids.get(id);
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
					Item it = its[i][j];
					if(it != null) 
					{
						itemMap.put("coords", new StringTag("coords", i + "-" + j));		
						itemMap.put("id", new IntTag("id", it.id));
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
		inventoryMap.put("slotListTag", slotListTag);
		inventoryMap.put("slotGridListTag", slotGridListTag);
		CompoundTag inventoryTag = new CompoundTag("inventory", inventoryMap);
		tagMap.put("inventory", inventoryTag);
		
		return new CompoundTag(tag.getName(), tagMap);	
	}		
}
