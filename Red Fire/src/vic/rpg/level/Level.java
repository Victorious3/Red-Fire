package vic.rpg.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.Game;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.level.path.NodeMap;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.render.Screen;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet10TimePacket;
import vic.rpg.utils.Utils;

public class Level
{
	public int width;
	public int height;
			
	public int[][][] worldobjects; //x-y-(id,data)
	
	public LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
	public LinkedHashMap<String, EntityPlayer> playerList = new LinkedHashMap<String, EntityPlayer>();
	
	public NodeMap nodeMap = new NodeMap(this);
	
	// Gameplay
	@Editable public int time = 5000;
	@Editable public String name = "NO_NAME";
	
	// Client stuff
	public ArrayList<Entity> entitiesForRender;
	
	public Area lightningArea;
	public ArrayList<Area> lightningSources;
	
	public Level(int width, int height, String name) 
	{	
		this.width = width;
		this.height = height;
		this.name = name;
		this.worldobjects = new int[width][height][2];
		
		if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{
			lightningArea = new Area(new Rectangle(Game.WIDTH, Game.HEIGHT));
			lightningSources = new ArrayList<Area>();
		}
	}
		
	public void onMouseMoved(int x, int y)
	{
		Entity ent = intersect(x - Screen.xOffset, y - Screen.yOffset); 
		if(ent != null)
		{
			ent.onMouseHovered(x - Screen.xOffset - ent.xCoord, y - Screen.yOffset - ent.yCoord, Game.thePlayer);
		}
	}
	
	public void onMouseClicked(int x, int y, int mouseEvent)
	{
		Entity ent = intersectOnRender(x - Screen.xOffset, y - Screen.yOffset); 
		if(ent != null)
		{
			ent.onMouseClicked(x - Screen.xOffset - ent.xCoord, y - Screen.yOffset - ent.yCoord, Game.thePlayer, mouseEvent);
		}
	}
	
	public int getWidth()
	{
		return width * CELL_SIZE;
	}
	
	public int getHeight()
	{
		return height * CELL_SIZE;
	}
	
	public void onKeyPressed(KeyEvent key)
	{
		for(Entity e : entities.values())
		{
			e.onKeyPressed(key);
		}
	}
	
	public void populate()
	{			
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				setTile(LevelRegistry.TILE_GRASS.id, x, y, 4);
			}
		}
		
		for(int x = 12; x < 19; x++)
		{
			for(int y = 2; y < 8; y++)
			{
				setTile(LevelRegistry.TILE_WATER.id, x, y);
			}
		}

		Random rand = new Random();
		int amount = rand.nextInt(51);
		amount += 50;
		
		for (int i = 0; i < amount; i++)
		{
			int randX = rand.nextInt(width * CELL_SIZE + 1);
			int randY = rand.nextInt(height * CELL_SIZE + 1);

			addEntity(LevelRegistry.ENTITY_TREE.id, randX, randY);		
		}
		addEntity(LevelRegistry.ENTITY_HOUSE.id, 200, 200);
		addEntity(LevelRegistry.ENTITY_APLTREE.id, 700, 400);
		addEntity(LevelRegistry.ENTITY_LIVING_NPC.id, 200, 200);
		
		nodeMap.recreate(this);
	}
	
	public void fill(int id, int data)
	{
		for(int i = 0; i < worldobjects.length; i++)
		{
			for(int j = 0; j < worldobjects[0].length; j++)
			{
				worldobjects[i][j] = new int[]{id, data};
			}
		}
	}
	
	public void render(Graphics2D g2d) 
	{
		render(g2d, -Screen.xOffset, -Screen.yOffset, Game.WIDTH, Game.HEIGHT, -Screen.xOffset, -Screen.yOffset);
	}

	public void render(Graphics2D g2d, int xOffset, int yOffset, int width, int height, int xOffset2, int yOffset2)
	{
		for(int x = 0; x < this.width; x++)
		{
			for(int y = 0; y < this.height; y++)
			{
				if(x * CELL_SIZE >= xOffset - CELL_SIZE && x * CELL_SIZE <= xOffset + width && y * CELL_SIZE >= yOffset - CELL_SIZE && y * CELL_SIZE <= yOffset + height)
				{
					g2d.drawImage(LevelRegistry.tileRegistry.get(worldobjects[x][y][0]).getRender(x, y, worldobjects[x][y][1]).img, x * CELL_SIZE - xOffset2, y * CELL_SIZE - yOffset2, null);
				}				
			}
		}
		
		for(Entity e : sortEntitiesByZLevel())
		{
			if(e.xCoord + e.getWidth() >= xOffset && e.xCoord <= xOffset + width && e.yCoord + e.getHeight() >= yOffset && e.yCoord <= yOffset + height)
			{
				e.render(g2d);
				g2d.drawImage(e.img, e.xCoord - xOffset2, e.yCoord - yOffset2, null);
			}
		}
	}
	
	int tickCounter = 0;
	
	public void tick()
	{	
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				LevelRegistry.tileRegistry.get(worldobjects[x][y][0]).tick(x, y, worldobjects[x][y][1]);	
			}
		}
		
		for(Entity e : entities.values())
		{
			e.tick();
		}
		
		if(Utils.getSide().equals(Utils.SIDE_SERVER))
		{
			for(EntityPlayer player : playerList.values())
			{
				player.tick();
			}
			
			tickCounter++;
			
			if(tickCounter == 10)
			{
				time++;
				if(time == 10000)
				{
					time = 0;
				}
				Server.server.broadcast(new Packet10TimePacket(time));
				tickCounter = 0;
			}
		}
	}
	
	public static final int CELL_SIZE = 50;
	
	public static void unload(){}
	
	public void setTile(int id, int x, int y)
	{
		worldobjects[x][y][0] = id;
		worldobjects[x][y][1] = 0;
	}
	
	public void setTile(int id, int x, int y, int data)
	{
		Tile tile = LevelRegistry.tileRegistry.get(id);
		tile.data = data;
		worldobjects[x][y][0] = id;
		worldobjects[x][y][1] = data;
	}
	
	public void addEntity(int id, int x, int y)
	{
		Entity e = LevelRegistry.entityRegistry.get(id).clone();
		UUID uuid = UUID.randomUUID();
		e.xCoord = x;
		e.yCoord = y;
		e.uniqueUUID = uuid.toString();
		e.levelObj = this;
		entities.put(uuid.toString(), e);
	}
	
	public void addEntity(Entity ent, int x, int y)
	{	
		if(x > getWidth() || x < 0 || y > getHeight() || y < 0) return;
		UUID uuid = UUID.randomUUID(); 
		ent.xCoord = x;
		ent.yCoord = y;
		ent.uniqueUUID = uuid.toString();
		ent.levelObj = this;
		entities.put(uuid.toString(), ent);
	}
	
	public void addPlayer(EntityPlayer player, String username, int x, int y)
	{
		UUID uuid = UUID.randomUUID();
		player.xCoord = x;
		player.yCoord = y;
		player.username = username;
		player.uniqueUUID = uuid.toString();
		player.levelObj = this;
		playerList.put(username, player);		
	}

	public ArrayList<Entity> sortEntitiesByZLevel()
	{
		ArrayList<Entity> ent2 = new ArrayList<Entity>(entities.values());
		Collections.sort(ent2, new Entity.EntityComperator());
		return ent2;
	}
	
	/**
	 * Searches for transparent pixels, doesn't use the collision box.
	 * @param x
	 * @param y
	 * @return Entity
	 */
	public Entity intersect(int x, int y)
	{
		for(Entity ent : entities.values())
		{
			Rectangle rect = new Rectangle(ent.xCoord, ent.yCoord, ent.getWidth(), ent.getHeight());
			if(rect.contains(x, y))
			{
				if(((ent.img.getRGB(x - rect.x, y - rect.y)>>24) & 0xff) != 0)
				{				
					return ent;
				}
			}
		}
		return null;
	}
	
	public Entity intersectOnRender(int x, int y)
	{
		for(Entity ent : entities.values())
		{
			if(x >= ent.xCoord && x <= ent.xCoord + ent.getWidth() && y >= ent.yCoord && y <= ent.yCoord + ent.getHeight())
			{
				return ent;
			}
		}
		return null;
	}
	
	public ArrayList<Entity> intersectOnRender(Shape shape)
	{
		Area a1 = new Area(shape);
		ArrayList<Entity> retEnts = new ArrayList<Entity>();
		
		for(Entity ent : entities.values())
		{
			Area a2 = new Area(new Rectangle(ent.xCoord, ent.yCoord, ent.getWidth(), ent.getHeight()));
			a2.intersect(a1);
			if(!a2.isEmpty())
			{
				retEnts.add(ent);
			}
		}
		
		return retEnts;
	}
	
	@SuppressWarnings("unchecked")
	public static Level readFromNBT(CompoundTag tag)
	{
		Map<String, Tag> levelMap = tag.getValue();
		
		int width = (int)levelMap.get("width").getValue();
		int height = (int)levelMap.get("height").getValue();
		int time = (int)levelMap.get("time").getValue();
		String name = (String)levelMap.get("name").getValue();
		
		List<Tag> entityList = (List<Tag>)levelMap.get("entities").getValue();
		List<Tag> tileList = (List<Tag>)levelMap.get("tiles").getValue();
		
		Level level = new Level(width, height, name);
		
		int[][][] worldObjects = new int[width][height][2];		
		int x = 0; int y = 0;
		for(Tag tileTag : tileList)
		{
			Tile obj = LevelRegistry.readTileFromNBT((CompoundTag)tileTag);
			worldObjects[x][y][0] = obj.id;
			worldObjects[x][y][1] = obj.data;
			y++;
			if(y == height)
			{
				y = 0;
				x++;
				if(x == width) break;
			}
		}
		
		LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
		for(Tag entityTag : entityList)
		{
			Entity ent = LevelRegistry.readEntityFromNBT((CompoundTag)entityTag);
			ent.levelObj = level;
			entities.put(ent.uniqueUUID, ent);
		}
		
		level.worldobjects = worldObjects;
		level.entities = entities;
		level.time = time;
		
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) level.entitiesForRender = level.sortEntitiesByZLevel();
		
		level.nodeMap.recreate(level);
		
		return level;
	}
	
	public CompoundTag writeToNBT(boolean send)
	{
		IntTag widthTag = new IntTag("width", width);
		IntTag heightTag = new IntTag("height", height);
		IntTag timeTag = new IntTag("time", time);
		StringTag nameTag = new StringTag("name", name);
		
		Map<String, Tag> levelMap = new HashMap<String, Tag>();
		List<Tag> entityList = new ArrayList<Tag>();
		List<Tag> tileList = new ArrayList<Tag>();
		
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				Tile t = LevelRegistry.tileRegistry.get(worldobjects[x][y][0]);
				t.data = worldobjects[x][y][1];
				CompoundTag tileTag = LevelRegistry.writeTileToNBT(t);
				tileList.add(tileTag);
			}
		}
		
		for(Entity e : entities.values())
		{
			CompoundTag enitiyTag = LevelRegistry.writeEntityToNBT(e);
			entityList.add(enitiyTag);
		}
		
		if(send)
		{
			for(EntityPlayer e : playerList.values())
			{
				CompoundTag enitiyTag = LevelRegistry.writeEntityToNBT(e);
				entityList.add(enitiyTag);
			}
		}
		
		ListTag tileListTag = new ListTag("tiles", CompoundTag.class, tileList);
		ListTag entityListTag = new ListTag("entities", CompoundTag.class, entityList);
		
		levelMap.put("width", widthTag);
		levelMap.put("height", heightTag);
		levelMap.put("time", timeTag);
		levelMap.put("name", nameTag);
		levelMap.put("tiles", tileListTag);
		levelMap.put("entities", entityListTag);
		
		return new CompoundTag("level", levelMap);
	}
	
	public void writeToFile(File file)
	{
		try {
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(file));
			out.writeTag(writeToNBT(false));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Level readFromFile(File file)
	{
		try {
			NBTInputStream in = new NBTInputStream(new FileInputStream(file));
			Level level = readFromNBT((CompoundTag) in.readTag());
			in.close();
			return level;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void drawLightMap(Graphics2D g2d)
	{	
		int lightValue = time / 60;
		Color darkness = new Color(0, 0, 0, lightValue);
			
		g2d.setColor(darkness);
		g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
	}
	
	public void updateLightMap()
	{
		lightningArea = new Area(new Rectangle(Game.WIDTH, Game.HEIGHT));
		
		for(Area a : lightningSources)
		{
			lightningArea.subtract(a);
		}
	}
}
