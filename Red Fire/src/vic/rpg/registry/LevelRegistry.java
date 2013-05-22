package vic.rpg.registry;

import java.util.HashMap;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.item.Item;
import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemPeer;
import vic.rpg.item.ItemShield;
import vic.rpg.item.ItemSword;
import vic.rpg.level.Entity;
import vic.rpg.level.Tile;
import vic.rpg.level.entity.EntityAppleTree;
import vic.rpg.level.entity.EntityHouse;
import vic.rpg.level.entity.EntityTree;
import vic.rpg.level.entity.living.EntityNPC;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.level.tiles.TileGrass;
import vic.rpg.level.tiles.TileWater;

public class LevelRegistry 
{
	public static HashMap<Integer, Entity> entityRegistry = new HashMap<Integer, Entity>();
	public static HashMap<Integer, Tile> tileRegistry = new HashMap<Integer, Tile>();
	public static HashMap<Integer, Item> itemRegistry = new HashMap<Integer, Item>();
	
	public static final TileGrass TILE_GRASS = new TileGrass();
	public static final TileWater TILE_WATER = new TileWater();
	
	public static final EntityTree ENTITY_TREE = new EntityTree();
	public static final EntityAppleTree ENTITY_APLTREE = new EntityAppleTree();
	public static final EntityHouse ENTITY_HOUSE = new EntityHouse();
	public static final EntityPlayer ENTITY_LIVING_PLAYER = new EntityPlayer();
	public static final EntityNPC ENTITY_LIVING_NPC = new EntityNPC();
	
	public static final Item ITEM_APPLE = new ItemApple();
	public static final Item ITEM_PEER = new ItemPeer();
	public static final Item ITEM_SWORD = new ItemSword();
	public static final Item ITEM_SHIELD = new ItemShield();
	
	static
	{		
		register(TILE_GRASS, 1);
		register(TILE_WATER, 2);
		
		register(ENTITY_TREE, 1);
		register(ENTITY_HOUSE, 2);
		register(ENTITY_APLTREE, 4);
		register(ENTITY_LIVING_PLAYER, 0);
		register(ENTITY_LIVING_NPC, 3);
		
		register(ITEM_APPLE, 1);
		register(ITEM_PEER, 2);
		register(ITEM_SWORD, 3);
		register(ITEM_SHIELD, 4);
	}
	
	public static void register(Entity ent, int id)
	{
		ent.id = id;
		entityRegistry.put(id, ent);
	}
	
	public static void register(Tile obj, int id)
	{
		obj.id = id;
		tileRegistry.put(id, obj);
	}
	
	public static void register(Item item, int id)
	{
		item.id = id;
		itemRegistry.put(id, item);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Entity readEntityFromNBT(CompoundTag tag)
	{	
		Map<String, Tag> map = tag.getValue();
		
		int xCoord = (int)(map.get("xcoord")).getValue();
		int yCoord = (int)(map.get("ycoord")).getValue();
		int id = (int)(map.get("id")).getValue();
		int zLevel = (int)(map.get("zLevel")).getValue(); 
		
		String uuid = (String)(map.get("uuid")).getValue();
		
		Entity ent = null;
		Class entClass = entityRegistry.get(id).getClass();
		try {		
			ent = (Entity) entClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
			ent.xCoord = xCoord;
			ent.yCoord = yCoord;
			ent.uniqueUUID = uuid;
			ent.id = id;
			ent.zLevel = zLevel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ent.readFromNBT(tag);
		
		return ent;
	}
	
	public static CompoundTag writeEntityToNBT(Entity ent)
	{
		int id = ent.id;	
		
		Map<String, Tag> map = new HashMap<String, Tag>(); 	
		map.put("id", new IntTag("id", id));		
		map.put("xcoord", new IntTag("xcoord", ent.xCoord));
		map.put("ycoord", new IntTag("ycoord", ent.yCoord));
		map.put("zLevel", new IntTag("zLevel", ent.zLevel));
		map.put("uuid", new StringTag("uuid", ent.uniqueUUID));
		
		CompoundTag tag = new CompoundTag("entity", map);		
		tag = ent.writeToNBT(tag);
		
		return tag;		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Tile readTileFromNBT(CompoundTag tag)
	{
		Map<String, Tag> map = tag.getValue();
		
		int data = (int)(map.get("data")).getValue();
		int id = (int)(map.get("id")).getValue(); 
		
		Tile obj = null;	
		Class objClass = tileRegistry.get(id).getClass();
		try {		
			obj = (Tile) objClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		obj.data = data;
		obj.id = id;
		
		return obj;
	}
	
	public static CompoundTag writeTileToNBT(Tile obj)
	{
		Map<String, Tag> map = new HashMap<String, Tag>(); 	
		map.put("id", new IntTag("id", obj.id));		
		map.put("data", new IntTag("data", obj.data));
		
		CompoundTag tag = new CompoundTag("tile", map);		
		
		return tag;		
	}
}
