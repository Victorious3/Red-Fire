package vic.rpg.registry;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import org.jnbt.CompoundTag;
import org.jnbt.Tag;

import vic.rpg.Init;
import vic.rpg.combat.Skill;
import vic.rpg.combat.SkillHeal;
import vic.rpg.item.Item;
import vic.rpg.item.ItemApple;
import vic.rpg.item.ItemPeer;
import vic.rpg.item.ItemShield;
import vic.rpg.item.ItemSword;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Logger.LogLevel;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.EntityAppleTree;
import vic.rpg.world.entity.EntityCustom;
import vic.rpg.world.entity.EntityHouse;
import vic.rpg.world.entity.EntityTree;
import vic.rpg.world.entity.living.EntityNPC;
import vic.rpg.world.entity.living.EntityPlayer;
import vic.rpg.world.entity.tile.EntityTile;
import vic.rpg.world.tile.Tile;
import vic.rpg.world.tile.TileChest;
import vic.rpg.world.tile.TileJSON;
import vic.rpg.world.tile.TilePlaceHolder;
import vic.rpg.world.tile.TileTerrain;
import vic.rpg.world.tile.TileTree;
import vic.rpg.world.tile.TileVoid;
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
	public static final TileChest TILE_CHEST = new TileChest();
	
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
		register(TILE_CHEST, 4);
		
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
				Logger.log(LogLevel.WARNING, "[WorldRegistry]: Entity " + e + " couldn't be registered! Id " + id + " is already occupied by " + entityRegistry.get(id));
				return null;
			}			
			register(e, id);
			Logger.log("[WorldRegistry]: Registered Entity " + e.getClass().getSimpleName() + " with ID:" + id);
			return e;
		} catch (Exception e) {
			Logger.log(LogLevel.WARNING, "[WorldRegistry]: Caught error in file " + f + ". Entity could't be loaded!");
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
				Logger.log(LogLevel.WARNING, "[WorldRegistry]: Tile " + tile.getName() + " couldn't be registered! Id " + tile.getSuggestedID() + " is already occupied by " + tileRegistry.get(tile.getSuggestedID()).getName());
				return null;
			}
			register(tile, tile.getSuggestedID());
			Logger.log("[WorldRegistry]: Registered Tile " + tile.getName() + " with ID:" + tile.getSuggestedID());
			return tile;
		} catch (Exception e) {
			Logger.log(LogLevel.WARNING, "[WorldRegistry]: Caught error in file " + f + ". Tile could't be loaded!");
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
		if(id == 0) throw new IllegalArgumentException("The id 0 is reserved for emptly tiles!");
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
		int xCoord = tag.getInt("xCoord", 0);
		int yCoord = tag.getInt("yCoord", 0);
		int dimension = tag.getInt("dim", 0);
		int id = tag.getInt("id", 0);
		
		String uuid = tag.getString("uuid", null);
		
		Entity ent = null;
		Entity entLoad = entityRegistry.get(id);
		
		if(entLoad != null)
		{
			Class entClass = entLoad.getClass();
			try {		
				ent = (Entity) entClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
				ent.UUID = uuid;
				ent.dimension = dimension;
				ent.xCoord = xCoord;
				ent.yCoord = yCoord;
				ent.id = id;
				ent.onCreation();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ent.readFromNBT(tag);
		}
		else
		{
			Logger.log(LogLevel.WARNING, "Entity with ID " + id + " at " + xCoord + ", " + yCoord + " is missing! Skipping...");
		}
		return ent;
	}
	
	public static EntityTile readEntityTileFromNBT(CompoundTag tag, Class<? extends EntityTile> clazz)
	{
		try {
			EntityTile et = clazz.newInstance();
			et.layerID = tag.getInt("layerID", et.layerID);
			et.xCoord = tag.getInt("xCoord", et.xCoord);
			et.yCoord = tag.getInt("yCoord", et.yCoord);
			
			et.readFromNBT(tag, (Object[])null);
			
			return et;
		} catch (InstantiationException | IllegalAccessException e) {
			Logger.log(LogLevel.WARNING, "Error caught while loading EntityTile " + clazz.getSimpleName() + "!");
			return null;
		}
	}
	
	public static CompoundTag writeEntityToNBT(Entity ent)
	{
		CompoundTag tag = new CompoundTag("entity", new HashMap<String, Tag>());
		
		tag.putInt("id", ent.id);
		tag.putInt("dim", ent.dimension);
		tag.putInt("xCoord", ent.xCoord);
		tag.putInt("yCoord", ent.yCoord);
		tag.putString("uuid", ent.UUID);
		 
		tag = ent.writeToNBT(tag);	
		return tag;		
	}
	
	public static CompoundTag writeEntityTileToNBT(EntityTile et)
	{
		CompoundTag tag = new CompoundTag("entity", new HashMap<String, Tag>());
		
		tag.putInt("layerID", et.layerID);
		tag.putInt("xCoord", et.xCoord);
		tag.putInt("yCoord", et.yCoord);
		
		tag = et.writeToNBT(tag);	 
		return tag;	
	}
}
