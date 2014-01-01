package vic.rpg.level;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.Game;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.level.path.NodeMap;
import vic.rpg.level.tiles.TilePlaceHolder;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.Screen;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet10TimePacket;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class Level implements INBTReadWrite
{
	public int width;
	public int height;
	
	private ArrayList<Integer[][][]> layers;
	public HashMap<Integer, Boolean> layerVisibility;
	
	public LinkedHashMap<String, Entity> entityMap = new LinkedHashMap<String, Entity>();
	public LinkedHashMap<String, EntityPlayer> onlinePlayersMap = new LinkedHashMap<String, EntityPlayer>();
	public LinkedHashMap<String, EntityPlayer> offlinePlayersMap = new LinkedHashMap<String, EntityPlayer>();
	
	public NodeMap nodeMap = new NodeMap(this);
	
	//Gameplay
	@Editable public int time = 5000;
	@Editable public String name = "NO_NAME";
	@Editable public int spawnX = 0;
	@Editable public int spawnY = 0;

	public Level(int width, int height, String name) 
	{	
		this.width = width;
		this.height = height;
		this.name = name;
		this.layers = new ArrayList<Integer[][][]>();
		this.layers.add(new Integer[width][height][2]);
		this.layerVisibility = new HashMap<Integer, Boolean>();
		this.layerVisibility.put(0, true);
	}
		
	public Level() 
	{
		this.width = 0;
		this.height = 0;
		this.name = "NO_NAME!";
		this.layers = new ArrayList<Integer[][][]>();
		this.layerVisibility = new HashMap<Integer, Boolean>();
	}

	public void onMouseMoved(int x, int y)
	{
		Entity ent = intersectOnRender(x - Screen.xOffset, y - Screen.yOffset); 
		if(ent != null)
		{
			ent.onMouseHovered(x - Screen.xOffset - ent.xCoord, y - Screen.yOffset - ent.yCoord, Game.getPlayer());
		}
	}
	
	public void onMouseClicked(int x, int y, int mouseEvent)
	{
		Entity ent = intersectOnRender(x - Screen.xOffset, y - Screen.yOffset); 
		if(ent != null)
		{
			ent.onMouseClicked(x - Screen.xOffset - ent.xCoord, y - Screen.yOffset - ent.yCoord, Game.getPlayer(), mouseEvent);
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
		for(Entity e : entityMap.values())
		{
			e.onKeyPressed(key);
		}
	}
	
	@Deprecated
	public void populate()
	{			
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				setTile(LevelRegistry.TILE_TERRAIN.id, x, y, 29);
			}
		}
		
		for(int x = 30; x < 60; x++)
		{
			for(int y = 30; y < 60; y++)
			{
				setTile(LevelRegistry.TILE_VOID.id, x, y);
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
		addEntity(LevelRegistry.ENTITY_LIVING_NPC.id, 700, 200);
		
		nodeMap.recreate(this);
	}
	
	public void fill(int id, int data, int layerID)
	{
		Integer[][][] layer = layers.get(layerID);
		
		for(int i = 0; i < layer.length; i++)
		{
			for(int j = 0; j < layer[0].length; j++)
			{
				layer[i][j] = new Integer[]{id, data};
			}
		}
	}
	
	public void render(GL2 gl2) 
	{
		render(gl2, -Screen.xOffset, -Screen.yOffset, Game.WIDTH, Game.HEIGHT);
	}
	
	public Tile getTileAt(int x, int y)
	{
		Tile t = LevelRegistry.tileRegistry.get(layers.get(getLayer())[x][y][0]);
		if(t != null) t.setWorldObj(this);
		else if(t instanceof TilePlaceHolder) return null;
		return t;
	}
	
	public Integer getTileDataAt(int x, int y)
	{
		return layers.get(getLayer())[x][y][1];
	}
	
	public Tile getTileAt(int x, int y, int layerID)
	{
		Tile t = LevelRegistry.tileRegistry.get(layers.get(layerID)[x][y][0]);
		if(t != null) t.setWorldObj(this);
		else if(t instanceof TilePlaceHolder) return null;
		return t;
	}
	
	public Tile[] getTilesAt(int x, int y)
	{
		Tile[] tiles = new Tile[this.layers.size()];
		for(int l = 0; l < this.layers.size(); l++)
		{
			tiles[l] = getTileAt(x, y, l);
		}
		return tiles;
	}
	
	public Integer getTileDataAt(int x, int y, int layerID)
	{
		return layers.get(layerID)[x][y][1];
	}
	
	public int getLayerAmount()
	{
		return layers.size();
	}

	public void render(GL2 gl2, int xOffset, int yOffset, int width, int height)
	{
		DrawUtils.setGL(gl2);
		HashMap<Point, ArrayList<Entity>> entitiesForRender = sortEntitiesByZLevel();
		
		for(int x = 0; x < this.width; x++)
		{
			for(int y = 0; y < this.height; y++)
			{				
				for(int i = 0; i < getLayerAmount(); i++)
				{
					Integer data = getTileDataAt(x, y, i);
					Tile tile = getTileAt(x, y, i);
					if(tile != null)
					{
						int tileHeight = tile.getHeight(x, y, data);
						Point tilePos = Utils.convCartToIso(new Point(x * CELL_SIZE / 2 - xOffset, y * CELL_SIZE / 2 - yOffset));		
						
						if(tilePos.x + CELL_SIZE > 0 && tilePos.y + CELL_SIZE > 0 && tilePos.x < width + CELL_SIZE && tilePos.y < height + CELL_SIZE * tileHeight)
						{
							Point texPos = tile.getTextureCoord(x, y, data);
							if(isLayerVisible(i)) DrawUtils.drawTextureWithOffset(tilePos.x - Level.CELL_SIZE / 2, tilePos.y - ((Level.CELL_SIZE / 2) * (tileHeight - 1) * 2) - Level.CELL_SIZE / 2, texPos.x * Level.CELL_SIZE, (texPos.y - tileHeight + 1) * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE * tileHeight, tile.getTexture(x, y, data));			
							if(i == 0)
							{
								if(entitiesForRender.get(new Point(x,y)) != null)
								{
									for(Entity e : entitiesForRender.get(new Point(x,y)))
									{
										Point entPos = Utils.convCartToIso(new Point(e.xCoord - xOffset, e.yCoord - yOffset));
										e.render(gl2);
										DrawUtils.drawTexture(entPos.x, entPos.y, e.getTexture());
									}
								}
							}
						}							
					}
				}
			}
		}
	}
	
	int tickCounter = 0;
	
	public void tick()
	{	
		/*if(Utils.getSide() == Side.CLIENT)
		{
			SoundEngine.adjustClipRelativeToPlayer("Level.MainLoop", 400, 400, 2F);
		}*/
		for(int l = 0; l < layers.size(); l++)
		{
			Integer[][][] layer = layers.get(l);
			this.setLayer(l);
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					Tile t = LevelRegistry.tileRegistry.get(layer[x][y][0]);
					if(t != null)
					{
						t.setWorldObj(this);
						t.tick(x, y, layer[x][y][1]);
					}
				}
			}
		}
		
		for(Entity e : entityMap.values())
		{
			e.tick();
		}
		
		if(Utils.getSide() == Side.SERVER)
		{
			for(EntityPlayer player : onlinePlayersMap.values())
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
	
	public static final int CELL_SIZE = 64;	
	
	private int currentLayer = 0;
	
	public int getLayer()
	{
		return currentLayer;
	}
	
	public void setLayer(int layerID)
	{
		this.currentLayer = layerID;
	}
	
	public void addLayer()
	{
		this.layers.add(new Integer[width][height][2]);	
		this.layerVisibility.put(this.layers.size() - 1, true);
	}
	
	public void removeLayer(int layerID)
	{
		if(layerID == 0) return;
		this.layers.remove(layerID);
		this.layerVisibility.remove(layerID);
		if(layerID == getLayer())
		{
			setLayer(this.layers.size() - 1);
		}
	}
	
	public void setLayerVisibility(int layerID, boolean visiblility)
	{
		this.layerVisibility.put(layerID, visiblility);
	}
	
	public boolean isLayerVisible(int layerID)
	{
		Boolean bool = this.layerVisibility.get(layerID);
		return bool != null ? bool : false;
	}
	
	public void setTile(Integer id, int x, int y)
	{
		setTile(id, x, y, 0);
	}
	
	public void setTile(Integer id, int x, int y, int data)
	{
		setTile(id, x, y, data, getLayer());
	}
	
	public void setTile(Integer id, int x, int y, int data, int layerID)
	{
		if(x < 0 || y < 0 || x >= width || y >= height) return;
		this.setLayer(layerID);
		Integer[][][] layer = layers.get(getLayer());
		
		//TODO No support for bigger tile sizes yet!
		/*if(layer[x][y][0] != null && layer[x][y][0] == LevelRegistry.TILE_PLACEHOLDER.id)
		{
			int loc = getTileDataAt(x, y, layerID);
			Point p = Utils.conv1Dto2DPoint(loc, width);
			Tile t2 = getTileAt(p.x, p.y, layerID);
			
			Dimension dim = t2.getDimension(x, y, data);
			if(dim.getWidth() > 1 || dim.getHeight() > 1)
			{
				for(int x1 = 0; x1 < dim.getWidth(); x1++)
				{
					for(int y1 = 0; y1 < dim.getHeight(); y1++)
					{
						layer[p.x + x1][p.y + y1][0] = null;
					}
				}
			}
		}*/
		
		/*Tile t = LevelRegistry.tileRegistry.get(id);
		if(t != null)
		{
			Dimension dim = t.getDimension(x, y, data);
			if(dim.getWidth() > 1 || dim.getHeight() > 1)
			{
				if(layerID == 0) return;
				for(int x1 = 0; x1 < dim.getWidth(); x1++)
				{
					for(int y1 = 0; y1 < dim.getHeight(); y1++)
					{
						layer[x + x1][y + y1][0] = LevelRegistry.TILE_PLACEHOLDER.id;
						layer[x + x1][y + y1][1] = Utils.conv2Dto1Dint(x, y, width);
					}
				}
			}
		}*/	
		layer[x][y][0] = id;
		layer[x][y][1] = data;
	}
	
	public void addEntity(int id, int x, int y)
	{
		Entity e = LevelRegistry.entityRegistry.get(id).clone();
		UUID uuid = UUID.randomUUID();
		e.xCoord = x;
		e.yCoord = y;
		e.UUID = uuid.toString();
		e.levelObj = this;
		if(e instanceof EntityLiving)
		{
			((EntityLiving)e).formatInventory();
		}
		entityMap.put(uuid.toString(), e);
	}
	
	public void addEntity(Entity ent, int x, int y)
	{	
		if(x > getWidth() || x < 0 || y > getHeight() || y < 0) return;
		UUID uuid = UUID.randomUUID(); 
		ent.xCoord = x;
		ent.yCoord = y;
		ent.UUID = uuid.toString();
		ent.levelObj = this;
		entityMap.put(uuid.toString(), ent);
	}
	
	public void addPlayer(EntityPlayer player, String username)
	{
		player.username = username;
		player.levelObj = this;
		onlinePlayersMap.put(username, player);		
	}
	
	public void createPlayer(EntityPlayer player, String username, int x, int y)
    {
        UUID uuid = UUID.randomUUID();
        player.xCoord = x;
        player.yCoord = y;
        player.username = username;
        player.UUID = uuid.toString();
        player.levelObj = this;
        player.formatInventory();
        onlinePlayersMap.put(username, player);                
    }
	
	public HashMap<Point, ArrayList<Entity>> sortEntitiesByZLevel()
	{
		HashMap<Point, ArrayList<Entity>> ent2 = new HashMap<Point, ArrayList<Entity>>();
		for(Entity e : entityMap.values())
		{
			Point p = Utils.convCartToIso(new Point(e.xCoord, e.yCoord));
			p.x += e.getWidth() / 2;
			p.y += e.getHeight();
			Point p2 = Utils.convIsoToCart(p);
			int x = (int)((float)(p2.x) / (float)Level.CELL_SIZE * 2);
			int y = (int)((float)(p2.y) / (float)Level.CELL_SIZE * 2);
			Point pnt = new Point(x, y);
			if(ent2.get(pnt) == null) ent2.put(pnt, new ArrayList<Entity>());
			ent2.get(pnt).add(e);
		}
		return ent2;
	}
	
	/**
	 * This method returns an Entity given two isometric coordinates.<br>
	 * If no entity can be found, null is returned.
	 * @return Entity
	 */
	public Entity intersectOnRender(int x, int y)
	{
		for(Entity ent : entityMap.values())
		{
			Point p = Utils.convCartToIso(new Point(ent.xCoord, ent.yCoord));
			if(x >= p.x && x <= p.x + ent.getWidth() && y >= p.y && y <= p.y + ent.getHeight())
			{
				return ent;
			}
		}
		return null;
	}
	
	/**
	 * This method returns an array of all entities that intersect with a given shape in isometric coordinates.<br>
	 * If no entity can be found, an empty list is returned.
	 * @return ArrayList&lt;Entity&gt;
	 */
	public ArrayList<Entity> intersectOnRender(Shape shape)
	{
		Area a1 = new Area(shape);
		ArrayList<Entity> retEnts = new ArrayList<Entity>();
		
		for(Entity ent : entityMap.values())
		{
			Point p = Utils.convCartToIso(new Point(ent.xCoord, ent.yCoord));
			Area a2 = new Area(new Rectangle(p.x, p.y, ent.getWidth(), ent.getHeight()));
			a2.intersect(a1);
			if(!a2.isEmpty())
			{
				retEnts.add(ent);
			}
		}
		
		return retEnts;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void readFromNBT(CompoundTag tag, Object... args)
	{
		Map<String, Tag> levelMap = tag.getValue();
		
		int width = (int)levelMap.get("width").getValue();
		int height = (int)levelMap.get("height").getValue();
		int time = (int)levelMap.get("time").getValue();
		int spawnX = (int)levelMap.get("spawnX").getValue();
		int spawnY = (int)levelMap.get("spawnY").getValue();
		String name = (String)levelMap.get("name").getValue();
		
		List<Tag> entityList = (List<Tag>)levelMap.get("entities").getValue();
		List<Tag> layerList = (List<Tag>)levelMap.get("layers").getValue();
		
		this.width = width;
		this.height = height;
		this.name = name;

		ArrayList<Integer[][][]> layers = new ArrayList<Integer[][][]>();
		for(Tag layerTag : layerList)
		{
			List<Tag> tileList = (List<Tag>)(layerTag.getValue());
			Integer[][][] layer = new Integer[width][height][2];		
			int x = 0; int y = 0;
			for(Tag tileTag : tileList)
			{
				Tile obj = LevelRegistry.readTileFromNBT((CompoundTag)tileTag);
				if(obj != null)
				{
					layer[x][y][0] = obj.id;
					layer[x][y][1] = obj.data;
				}
				y++;
				if(y == height)
				{
					y = 0;
					x++;
					if(x == width) break;
				}
			}
			layers.add(layer);
		}
		
		LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
		for(Tag entityTag : entityList)
		{
			Entity ent = LevelRegistry.readEntityFromNBT((CompoundTag)entityTag);
			if(ent != null)
			{
				ent.levelObj = this;
				entities.put(ent.UUID, ent);
			}
		}
		
		this.layers = layers;
		
		for(int i = 0; i < this.layers.size(); i++)
		{
			this.layerVisibility.put(i, true);
		}
		
		this.entityMap = entities;
		this.time = time;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		
		if(levelMap.containsKey("players"))
		{
			List<Tag> playerList = (List<Tag>)levelMap.get("players").getValue();
			LinkedHashMap<String, EntityPlayer> players = new LinkedHashMap<String, EntityPlayer>();
			for(Tag playerTag : playerList)
			{
				EntityPlayer ent = (EntityPlayer) LevelRegistry.readEntityFromNBT((CompoundTag)playerTag);
				ent.levelObj = this;
				players.put(ent.username, ent);
			}
			this.offlinePlayersMap = players;
		}
		
		this.nodeMap.recreate(this);
	}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args)
	{
		IntTag widthTag = new IntTag("width", width);
		IntTag heightTag = new IntTag("height", height);
		IntTag timeTag = new IntTag("time", time);
		IntTag spawnXTag = new IntTag("spawnX", spawnX);
		IntTag spawnYTag = new IntTag("spawnY", spawnY);
		StringTag nameTag = new StringTag("name", name);
		
		Map<String, Tag> levelMap = new HashMap<String, Tag>();
		List<Tag> entityList = new ArrayList<Tag>();
		List<Tag> layerList = new ArrayList<Tag>();
		
		for(int l = 0; l < layers.size(); l++)
		{
			Integer[][][] layer = layers.get(l);
			
			List<Tag> tileList = new ArrayList<Tag>();
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					Tile t = LevelRegistry.tileRegistry.get(layer[x][y][0]);
					if(t != null)
					{
						t.data = layer[x][y][1];
						CompoundTag tileTag = LevelRegistry.writeTileToNBT(t);
						tileList.add(tileTag);
					}
					else
					{
						CompoundTag tileTag = new CompoundTag("tile", new HashMap<String, Tag>());
						tileList.add(tileTag);
					}
				}
			}
			ListTag layerTag = new ListTag("layer", CompoundTag.class, tileList);
			layerList.add(layerTag);
		}
		
		for(Entity e : entityMap.values())
		{
			CompoundTag enitiyTag = LevelRegistry.writeEntityToNBT(e);
			entityList.add(enitiyTag);
		}
		
		if(args[0] != null && args[0] == Boolean.TRUE)
		{
			for(EntityPlayer e : onlinePlayersMap.values())
			{
				CompoundTag enitiyTag = LevelRegistry.writeEntityToNBT(e);
				entityList.add(enitiyTag);
			}
		}
		else
		{
			//Player saves
			ArrayList<CompoundTag> playerList = new ArrayList<CompoundTag>();
			
			for(EntityPlayer e : onlinePlayersMap.values())
			{
				CompoundTag entityTag = LevelRegistry.writeEntityToNBT(e);
				playerList.add(entityTag);
			}
			for(EntityPlayer e : offlinePlayersMap.values())
			{
				CompoundTag entityTag = LevelRegistry.writeEntityToNBT(e);
				playerList.add(entityTag);
			}
			
			ListTag playerListTag = new ListTag("players", CompoundTag.class, playerList);
			levelMap.put("players", playerListTag);
		}
		
		ListTag tileListTag = new ListTag("layers", ListTag.class, layerList);
		ListTag entityListTag = new ListTag("entities", CompoundTag.class, entityList);
		
		levelMap.put("width", widthTag);
		levelMap.put("height", heightTag);
		levelMap.put("time", timeTag);
		levelMap.put("name", nameTag);
		levelMap.put("spawnX", spawnXTag);
		levelMap.put("spawnY", spawnYTag);
		levelMap.put("tiles", tileListTag);
		levelMap.put("entities", entityListTag);

		return new CompoundTag("level", levelMap);
	}
	
	public void writeToFile(File file)
	{
		try {
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(file));
			out.writeTag(writeToNBT(null, false));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Level readFromFile(File file)
	{
		try {
			NBTInputStream in = new NBTInputStream(new FileInputStream(file));
			Level level = new Level();
			level.readFromNBT((CompoundTag) in.readTag());
			in.close();
			return level;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
