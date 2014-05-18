package vic.rpg.registry;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.Init;
import vic.rpg.combat.Skill;
import vic.rpg.combat.SkillHeal;
import vic.rpg.item.Item;
import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemPeer;
import vic.rpg.item.ItemShield;
import vic.rpg.item.ItemSword;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.EntityAppleTree;
import vic.rpg.world.entity.EntityCustom;
import vic.rpg.world.entity.EntityHouse;
import vic.rpg.world.entity.EntityTree;
import vic.rpg.world.entity.living.EntityNPC;
import vic.rpg.world.entity.living.EntityPlayer;
import vic.rpg.world.tiles.Tile;
import vic.rpg.world.tiles.TileJSON;
import vic.rpg.world.tiles.TilePlaceHolder;
import vic.rpg.world.tiles.TileTerrain;
import vic.rpg.world.tiles.TileTree;
import vic.rpg.world.tiles.TileVoid;
import bsh.Interpreter;

/**
 * The WorldRegistry contains all game objects like {@link Item Items}, {@link Tile Tiles} and {@link Entity Entities}.
 * @author Victorious3
 */
public class WorldRegistry 
{
	public static HashMap<Integer, Entity> entityRegistry = new HashMap<Integer, Entity>();
	public static HashMap<Integer, Tile> tileRegistry = new HashMap<Integer, Tile>();
	public static HashMap<Integer, Item> itemRegistry = new HashMap<Integer, Item>();
	public static HashMap<Integer, Skill> skillRegistry = new HashMap<Integer, Skill>();
	
	public static final TileTerrain TILE_TERRAIN = new TileTerrain();
	public static final TileVoid TILE_VOID = new TileVoid();
	public static final TileTree TILE_BOAT = new TileTree();
	public static final TilePlaceHolder TILE_PLACEHOLDER = new TilePlaceHolder();
	
	public static final EntityTree ENTITY_TREE = new EntityTree();
	public static final EntityAppleTree ENTITY_APLTREE = new EntityAppleTree();
	public static final EntityHouse ENTITY_HOUSE = new EntityHouse();
	public static final EntityPlayer ENTITY_LIVING_PLAYER = new EntityPlayer();
	public static final EntityNPC ENTITY_LIVING_NPC = new EntityNPC();	
	
	public static final Item ITEM_APPLE = new ItemApple();
	public static final Item ITEM_PEER = new ItemPeer();
	public static final Item ITEM_SWORD = new ItemSword();
	public static final Item ITEM_SHIELD = new ItemShield();
	
	public static final Skill SKILL_HEAL = new SkillHeal();
	
	@Init(side = Side.BOTH)
	public static void init()
	{		
		register(TILE_PLACEHOLDER, -1);
		register(TILE_TERRAIN, 1);
		register(TILE_VOID, 2);
		register(TILE_BOAT, 3);
		
		register(ENTITY_TREE, 1);
		register(ENTITY_HOUSE, 2);
		register(ENTITY_APLTREE, 4);
		register(ENTITY_LIVING_PLAYER, 0);
		register(ENTITY_LIVING_NPC, 3);
		
		register(ITEM_APPLE, 1);
		register(ITEM_PEER, 2);
		register(ITEM_SWORD, 3);
		register(ITEM_SHIELD, 4);
		
		register(SKILL_HEAL, 0);
		
		File f = Utils.getOrCreateFile(Utils.getAppdata() + "/resources/entities/");
		
		for(File f2 : f.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".bsh");
			}
		})){
			addNewEntity(f2);
		}
		
		f = Utils.getOrCreateFile(Utils.getAppdata() + "/resources/tiles/");
		
		for(File f2 : f.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		})){
			addNewTile(f2);
		}
	}
	
	/**
	 * Adds a new custom created {@link EntityCustom} to the list of {@link Entity Entities}.
	 * @param f
	 * @return
	 */
	public static Entity addNewEntity(File f)
	{
		Interpreter i = new Interpreter();
		try {
			i.source(f.getAbsolutePath());				
			EntityCustom e = (EntityCustom) i.get("instance");
			int id = e.getSuggestedID();
			if(entityRegistry.containsKey(id) && !entityRegistry.get(id).getClass().equals(e.getClass()))
			{
				System.err.println("[WorldRegistry]: Entity " + e + " couldn't be registered! Id " + id + " is already occupied by " + entityRegistry.get(id));
				return null;
			}			
			register(e, id);
			System.out.println("[WorldRegistry]: Registered Entity " + e.getClass().getSimpleName() + " with ID:" + id);
			return e;
		} catch (Exception e) {
			System.err.println("[WorldRegistry]: Caught error in file " + f + ". Entity could't be loaded!");
			e.printStackTrace();
		}
		return null;
	}
	
	public static Tile addNewTile(File f)
	{
		try {
			TileJSON tile = TileJSON.parse(f);
			if(tileRegistry.containsKey(tile.getSuggestedID()))
			{
				System.err.println("[WorldRegistry]: Tile " + tile.getName() + " couldn't be registered! Id " + tile.getSuggestedID() + " is already occupied by " + tileRegistry.get(tile.getSuggestedID()).getName());
				return null;
			}
			register(tile, tile.getSuggestedID());
			System.out.println("[WorldRegistry]: Registered Tile " + tile.getName() + " with ID:" + tile.getSuggestedID());
			return tile;
		} catch (Exception e) {
			System.err.println("[WorldRegistry]: Caught error in file " + f + ". Tile could't be loaded!");
			e.printStackTrace();
		}
		return null;
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
	
	public static void register(Skill skill, int id)
	{
		skill.id = id;
		skillRegistry.put(id, skill);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Entity readEntityFromNBT(CompoundTag tag)
	{	
		Map<String, Tag> map = tag.getValue();
		
		int xCoord = (Integer)(map.get("xcoord")).getValue();
		int yCoord = (Integer)(map.get("ycoord")).getValue();
		int id = (Integer)(map.get("id")).getValue();
		
		String uuid = (String)(map.get("uuid")).getValue();
		
		Entity ent = null;
		Entity entLoad = entityRegistry.get(id);
		
		if(entLoad != null)
		{
			Class entClass = entLoad.getClass();
			try {		
				ent = (Entity) entClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
				ent.xCoord = xCoord;
				ent.yCoord = yCoord;
				ent.UUID = uuid;
				ent.id = id;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ent.readFromNBT(tag);
		}
		else
		{
			System.err.println("Entity with ID " + id + " at " + xCoord + ", " + yCoord + " is missing! Skipping...");
		}
		return ent;
	}
	
	public static CompoundTag writeEntityToNBT(Entity ent)
	{
		int id = ent.id;	
		
		Map<String, Tag> map = new HashMap<String, Tag>(); 	
		map.put("id", new IntTag("id", id));		
		map.put("xcoord", new IntTag("xcoord", ent.xCoord));
		map.put("ycoord", new IntTag("ycoord", ent.yCoord));
		map.put("uuid", new StringTag("uuid", ent.UUID));
		
		CompoundTag tag = new CompoundTag("entity", map);		
		tag = ent.writeToNBT(tag);
		
		return tag;		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Tile readTileFromNBT(CompoundTag tag)
	{
		Map<String, Tag> map = tag.getValue();
		
		if(map.containsKey("id"))
		{
			int data = (Integer)(map.get("data")).getValue();
			int id = (Integer)(map.get("id")).getValue(); 
			
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
		return null;
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
