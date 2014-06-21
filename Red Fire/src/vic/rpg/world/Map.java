package vic.rpg.world;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import vic.rpg.Game;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.Screen;
import vic.rpg.registry.WorldRegistry;
import vic.rpg.server.ServerLoop;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Vector3;
import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.living.EntityLiving;
import vic.rpg.world.entity.living.EntityPlayer;
import vic.rpg.world.entity.tile.EntityTile;
import vic.rpg.world.path.NodeMap;
import vic.rpg.world.tile.Tile;
import vic.rpg.world.tile.TilePlaceHolder;

/**
 * A map is the map all players are interacting with. It is stored twice, one is located in {@link Game#map} for rendering with the Client
 * and the other one in {@link ServerLoop#map} where all Server calculations are performed. A Map can be saved to a {@link File}, read from a {@link File}
 * and send via an {@link NBTOutputStream}. The Editor is used to modify the contents of a Map e.g {@link Tile Tiles} and {@link Entity Entities}.
 * <br><br>
 * Everything is stored with its Cartesian coordinates and is converted to Isometric coordinates on rendering to allow an isometric projection. <b>Always figure out
 * which coordinate system is used at the time and convert with {@link Utils#convCartToIso(Point)} and {@link Utils#convIsoToCart(Point)} if necessary!</b>
 * @author Victorious3
 */
public class Map implements INBTReadWrite
{
	/**
	 * The width of the Map in tiles.
	 */
	public int width;
	/**
	 * The height of the Map in tiles.
	 */
	public int height;
	
	private ArrayList<Integer[][][]> layers;
	public HashMap<Integer, Boolean> layerVisibility;
	
	public LinkedHashMap<String, Entity> entityMap = new LinkedHashMap<String, Entity>();
	public HashMap<Vector3, EntityTile> tilesMap = new HashMap<Vector3, EntityTile>();

	public NodeMap nodeMap = new NodeMap(this);
	public File saveFile;
	
	//Gameplay
	public int time = 5000;
	@Editable public boolean isAmbientLighting = true;
	@Editable public String name = "NO_NAME";
	@Editable public int spawnX = 0;
	@Editable public int spawnY = 0;
	@Editable public int id = 0;

	/**
	 * Creates a new Map.
	 * @param width
	 * @param height
	 * @param name
	 */
	public Map(int width, int height, String name) 
	{	
		this.width = width;
		this.height = height;
		this.name = name;
		this.layers = new ArrayList<Integer[][][]>();
		this.layers.add(new Integer[width][height][2]);
		this.layerVisibility = new HashMap<Integer, Boolean>();
		this.layerVisibility.put(0, true);
	}
	
	/**
	 * Creates an empty Map that has to be read with {@link #readFromNBT(CompoundTag, Object...)}.
	 */
	public Map() 
	{
		this.width = 0;
		this.height = 0;
		this.name = "NO_NAME!";
		this.layers = new ArrayList<Integer[][][]>();
		this.layerVisibility = new HashMap<Integer, Boolean>();
	}

	/**
	 * Called when the mouse is moved.
	 * @param x
	 * @param y
	 */
	public void onMouseMoved(int x, int y)
	{
		Entity ent = intersectOnRender(x - Screen.xOffset, y - Screen.yOffset); 
		if(ent != null)
		{
			ent.onMouseHovered(x - Screen.xOffset - ent.xCoord, y - Screen.yOffset - ent.yCoord, Game.getPlayer());
		}
	}
	
	/**
	 * Called when any mouse key was clicked.
	 * @param x
	 * @param y
	 * @param mouseEvent
	 */
	public void onMouseClicked(int x, int y, int mouseEvent)
	{
		Entity ent = intersectOnRender(x - Screen.xOffset, y - Screen.yOffset); 
		if(ent != null)
		{
			ent.onMouseClicked(x - Screen.xOffset - ent.xCoord, y - Screen.yOffset - ent.yCoord, Game.getPlayer(), mouseEvent);
		}
	}
	
	/**
	 * Returns the actual width of this Map (in Cartesian coordinates) by multiplying {@link #width} with {@link #CELL_SIZE}.
	 * @return
	 */
	public int getWidth()
	{
		return width * CELL_SIZE / 2;
	}
	
	/**
	 * Returns the actual height of this Map (in Cartesian coordinates) by multiplying {@link #height} with {@link #CELL_SIZE}.
	 * @return
	 */
	public int getHeight()
	{
		return height * CELL_SIZE / 2;
	}
	
	/**
	 * Called when a key was pressed.
	 * @param key
	 */
	public void onKeyPressed(KeyEvent key)
	{
		for(Entity e : entityMap.values())
		{
			e.onKeyPressed(key);
		}
	}
	
	/**
	 * Fill a complete layer with the specified {@link Tile} id and data.
	 * @param id
	 * @param data
	 * @param layerID
	 */
	public void fill(int id, int data, int layerID)
	{
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < height; y++)
			{
				setTile(id, x, y, data, layerID);
			}
		}
	}
	
	/**
	 * Called by the Client to render in native Resolution
	 * @param gl2
	 */
	public void render(GL2 gl2) 
	{
		render(gl2, -Screen.xOffset, -Screen.yOffset, Game.WIDTH, Game.HEIGHT);
	}
	
	/**
	 * Returns the {@link Tile} located at x|y. Uses the currently active layer.
	 * @param x
	 * @param y
	 * @return
	 */
	public Tile getTileAt(int x, int y)
	{
		Tile t = WorldRegistry.tileRegistry.get(layers.get(getLayer())[x][y][0]);
		if(t instanceof TilePlaceHolder) return null;
		return t;
	}
	
	/**
	 * Returns the data associated with the {@link Tile} at x|y. Uses the currently active layer.
	 * @param x
	 * @param y
	 * @return
	 */
	public Integer getTileDataAt(int x, int y)
	{
		return layers.get(getLayer())[x][y][1];
	}
	
	/**
	 * Returns the {@link Tile} located at layerID[x|y].
	 * @param x
	 * @param y
	 * @param layerID
	 * @return
	 */
	public Tile getTileAt(int x, int y, int layerID)
	{
		Tile t = WorldRegistry.tileRegistry.get(layers.get(layerID)[x][y][0]);
		if(t instanceof TilePlaceHolder) return null;
		return t;
	}
	
	/**
	 * Returns all {@link Tile Tiles} located at x|y. It cycles through all layers.
	 * If the place is empty on a layer, {@code null} is stored in the array.
	 * @param x
	 * @param y
	 * @return
	 */
	public Tile[] getTilesAt(int x, int y)
	{
		Tile[] tiles = new Tile[this.layers.size()];
		for(int l = 0; l < this.layers.size(); l++)
		{
			tiles[l] = getTileAt(x, y, l);
		}
		return tiles;
	}
	
	/**
	 * Returns the data associated with the {@link Tile} at layerID[x|y].
	 * @param x
	 * @param y
	 * @param layerID
	 * @return
	 */
	public Integer getTileDataAt(int x, int y, int layerID)
	{
		return layers.get(layerID)[x][y][1];
	}
	
	/**
	 * Returns the amount of total layers.
	 * @return
	 */
	public int getLayerAmount()
	{
		return layers.size();
	}

	/**
	 * Renders the Map. It does some Cartesian to Isometric conversions to render in the right place.
	 * The xOffset and yOffset is used to set the viewpoint. Width and height specify how much of the Map
	 * should be rendered.
	 * @param gl2
	 * @param xOffset
	 * @param yOffset
	 * @param width
	 * @param height
	 */
	public void render(GL2 gl2, int xOffset, int yOffset, int width, int height)
	{
		DrawUtils.setGL(gl2);
		HashMap<Point, ArrayList<Entity>> entitiesForRender = sortEntitiesByZMap();
		
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
						int tileHeight = tile.getHeight(x, y, data, i, this);
						Point tilePos = Utils.convCartToIso(new Point(x * CELL_SIZE / 2 - xOffset, y * CELL_SIZE / 2 - yOffset));		
						
						if(tilePos.x + CELL_SIZE > 0 && tilePos.y + CELL_SIZE > 0 && tilePos.x < width + CELL_SIZE && tilePos.y < height + CELL_SIZE * tileHeight)
						{
							Point texPos = tile.getTextureCoord(x, y, data, i, this);
							if(isLayerVisible(i)) DrawUtils.drawTextureWithOffset(tilePos.x - Map.CELL_SIZE / 2, tilePos.y - ((Map.CELL_SIZE / 2) * (tileHeight - 1) * 2) - Map.CELL_SIZE / 2, texPos.x * Map.CELL_SIZE, (texPos.y - tileHeight + 1) * Map.CELL_SIZE, Map.CELL_SIZE, Map.CELL_SIZE * tileHeight, tile.getTexture(x, y, data, i, this));			
							if(i == 0)
							{
								if(entitiesForRender.get(new Point(x, y)) != null)
								{
									for(Entity e : entitiesForRender.get(new Point(x, y)))
									{
										Point entPos = Utils.convCartToIso(new Point(e.xCoord - xOffset, e.yCoord - yOffset));
										Dimension entOffset = e.getRenderOffset();
										entPos.x -= entOffset.width;
										entPos.y -= entOffset.height;
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
	
	/**
	 * Called every 0.2 seconds. Updates every {@link Tile}, {@link Entity} and the server time.
	 */
	public void tick()
	{	
		/*if(Utils.getSide() == Side.CLIENT)
		{
			SoundEngine.adjustClipRelativeToPlayer("Map.MainLoop", 400, 400, 2F);
		}*/
		for(int l = 0; l < layers.size(); l++)
		{
			Integer[][][] layer = layers.get(l);
			this.setLayer(l);
			for(int x = 0; x < width; x++)
			{
				for(int y = 0; y < height; y++)
				{
					Tile t = WorldRegistry.tileRegistry.get(layer[x][y][0]);
					if(t != null)
					{
						t.tick(x, y, layer[x][y][1], l, this);
					}
				}
			}
		}
		
		for(Entity e : entityMap.values())
		{
			e.tick();
		}
	}
	
	/**
	 * The width of one {@link Tile}. The height is {@code CELL_SIZE / 2}.
	 */
	public static final int CELL_SIZE = 64;	
	
	private int currentLayer = 0;
	
	/**
	 * Returns the id of the currently active layer.
	 * @return
	 */
	public int getLayer()
	{
		return currentLayer;
	}
	
	/**
	 * Sets the currently active layer to layerID.
	 * @param layerID
	 */
	public void setLayer(int layerID)
	{
		this.currentLayer = layerID;
	}
	
	/**
	 * Add a new empty layer to the layer list.
	 */
	public void addLayer()
	{
		this.layers.add(new Integer[width][height][2]);	
		this.layerVisibility.put(this.layers.size() - 1, true);
	}
	
	/**
	 * Remove a layer from the layer list
	 * @param layerID
	 */
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
	
	/**
	 * Set weather a given layer should be rendered or not.
	 * @param layerID
	 * @param visiblility
	 */
	public void setLayerVisibility(int layerID, boolean visiblility)
	{
		this.layerVisibility.put(layerID, visiblility);
	}
	
	/**
	 * Checks if a given layer should be rendered or not.
	 * @param layerID
	 * @return
	 */
	public boolean isLayerVisible(int layerID)
	{
		Boolean bool = this.layerVisibility.get(layerID);
		return bool != null ? bool : false;
	}
	
	/**
	 * Sets a {@link Tile} at x|y. Uses the currently active layer.
	 * @param id
	 * @param x
	 * @param y
	 * @param data
	 */
	public void setTile(Integer id, int x, int y)
	{
		setTile(id, x, y, getLayer());
	}
	
	/**
	 * Sets a {@link Tile} at x|y and its data. Uses the currently active layer.
	 * @param id
	 * @param x
	 * @param y
	 * @param data
	 */
	public void setTile(Integer id, int x, int y, int data)
	{
		setTile(id, x, y, data, getLayer());
	}
	
	/**
	 * Sets a {@link Tile} at layerID[x|y] and its data.
	 * @param id
	 * @param x
	 * @param y
	 * @param data
	 * @param layerID
	 */
	public void setTile(Integer id, int x, int y, int data, int layerID)
	{
		if(x < 0 || y < 0 || x >= width || y >= height) return;
		this.setLayer(layerID);
		Integer[][][] layer = layers.get(getLayer());
		
		//TODO No support for bigger tile sizes yet!
		/*if(layer[x][y][0] != null && layer[x][y][0] == MapRegistry.TILE_PLACEHOLDER.id)
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
		
		/*Tile t = MapRegistry.tileRegistry.get(id);
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
						layer[x + x1][y + y1][0] = MapRegistry.TILE_PLACEHOLDER.id;
						layer[x + x1][y + y1][1] = Utils.conv2Dto1Dint(x, y, width);
					}
				}
			}
		}*/	
		
		layer[x][y][0] = id;
		layer[x][y][1] = data;
		
		if(WorldRegistry.tileRegistry.get(id).hasTileEntity() && getEntityTileAt(x, y, layerID) == null)
		{
			try {
				EntityTile et = WorldRegistry.tileRegistry.get(id).getTileEntity().newInstance();
				et.mapObj = this;
				et.layerID = layerID;
				et.xCoord = x;
				et.yCoord = y;
				setEntityTileAt(et, x, y, layerID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public EntityTile getEntityTileAt(int x, int y, int layerID)
	{
		return tilesMap.get(new Vector3(x, y, layerID));
	}
	
	public void setEntityTileAt(EntityTile et, int x, int y, int layerID)
	{
		tilesMap.put(new Vector3(x, y, layerID), et);
	}
	
	/**
	 * Creates a the new {@link Entity} associated with the given id. It places it at x|y.
	 * @param id
	 * @param x
	 * @param y
	 */
	public void addEntity(int id, int x, int y)
	{
		Entity e = WorldRegistry.entityRegistry.get(id).clone();
		UUID uuid = UUID.randomUUID();
		e.xCoord = x;
		e.yCoord = y;
		e.UUID = uuid.toString();
		e.mapObj = this;
		if(e instanceof EntityLiving)
		{
			((EntityLiving)e).formatInventory();
		}
		entityMap.put(uuid.toString(), e);
	}
	
	/**
	 * Adds an {@link Entity}. Only {@link Entity#mapObj} is changed.
	 * @param ent
	 */
	public void addEntity(Entity ent)
	{
		ent.dimension = id;
		ent.mapObj = this;
		entityMap.put(ent.UUID, ent);
	}
	
	/**
	 * Adds an {@link Entity} and places it at x|y.
	 * @param id
	 * @param x
	 * @param y
	 */
	public void addEntity(Entity ent, int x, int y)
	{	
		if(x > getWidth() || x < 0 || y > getHeight() || y < 0) return;
		if(ent instanceof EntityLiving)
		{
			((EntityLiving)ent).formatInventory();
		}
		UUID uuid = UUID.randomUUID();
		ent.xCoord = x;
		ent.yCoord = y;
		ent.UUID = uuid.toString();
		ent.dimension = id;
		ent.mapObj = this;
		entityMap.put(uuid.toString(), ent);
	}

	/**
	 * Creates a new {@link EntityPlayer} with a username and places it at x|y.
	 * @param player
	 * @param username
	 * @param x
	 * @param y
	 */
	public void createPlayer(EntityPlayer player, String username, int x, int y)
    {
        UUID uuid = UUID.randomUUID();
        player.xCoord = x;
        player.yCoord = y;
        player.username = username;
        player.UUID = uuid.toString();
        player.dimension = id;
        player.mapObj = this;
        player.formatInventory();
        entityMap.put(player.UUID, player);
    }
	
	/**
	 * Removes an {@link Entity}.
	 * @param uuid
	 * @return Entity
	 */
	public Entity removeEntity(String uuid)
	{
		return entityMap.remove(uuid);
	}
	
	/**
	 * Gets an {@link Entity}.
	 * @param uuid
	 * @return Entity
	 */
	public Entity getEntity(String uuid)
	{
		return entityMap.get(uuid);
	}
		
	/**
	 * There is no zMap supported at the moment.
	 * @return
	 */
	@Deprecated
	public HashMap<Point, ArrayList<Entity>> sortEntitiesByZMap()
	{
		HashMap<Point, ArrayList<Entity>> ent2 = new HashMap<Point, ArrayList<Entity>>();
		for(Entity e : entityMap.values())
		{
			int x = (int)((float)(e.xCoord) / (float)Map.CELL_SIZE * 2);
			int y = (int)((float)(e.yCoord) / (float)Map.CELL_SIZE * 2);
			Point pnt = new Point(x, y);
			if(ent2.get(pnt) == null) ent2.put(pnt, new ArrayList<Entity>());
			ent2.get(pnt).add(e);
		}
		return ent2;
	}
	
	/**
	 * Returns an Entity given its Isometric coordinates.
	 * If no entity can be found, null is returned.
	 * @return Entity
	 */
	public Entity intersectOnRender(int x, int y)
	{
		for(Entity ent : entityMap.values())
		{
			Point p = Utils.convCartToIso(new Point(ent.xCoord, ent.yCoord));
			Dimension off = ent.getRenderOffset();
			p.x -= off.width;
			p.y -= off.height;
			if(x >= p.x && x <= p.x + ent.getWidth() && y >= p.y && y <= p.y + ent.getHeight())
			{
				return ent;
			}
		}
		return null;
	}
	
	/**
	 * Returns an array of all entities that intersect with a given shape in Isometric coordinates.
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
	public void readFromNBT(CompoundTag tag, Object... args)
	{
		this.id = tag.getInt("id", 0);
		this.width = tag.getInt("width", 0);
		this.height = tag.getInt("height", 0);
		this.isAmbientLighting = tag.getBoolean("isAmbientLighting", true);
		this.spawnX = tag.getInt("spawnX", 0);
		this.spawnY = tag.getInt("spawnY", 0);
		this.name = tag.getString("name", "NO_NAME");
		
		List<Tag> entityList = (List<Tag>)tag.getListTag("entities").getValue();
		List<Tag> layerList = (List<Tag>)tag.getListTag("layers").getValue();
		List<Tag> entityTileList = (List<Tag>)tag.getListTag("entityTiles").getValue();

		ArrayList<Integer[][][]> layers = new ArrayList<Integer[][][]>();
		for(Tag layerTag : layerList)
		{
			CompoundTag tileTag = ((CompoundTag)layerTag).getCompoundTag("tiles");
			CompoundTag dataTag = ((CompoundTag)layerTag).getCompoundTag("data");
			
			Integer[][][] layer = new Integer[width][height][2];		
			
			for(int x = 0; x < width; x++)
			{
				int[] tiles = tileTag.getIntArray(String.valueOf(x), new int[height]);
				int[] data = dataTag.getIntArray(String.valueOf(x), new int[height]);
				
				for(int y = 0; y < height; y++)
				{
					layer[x][y][0] = tiles[y] != 0 ? tiles[y] : null;
					layer[x][y][1] = data[y];
				}
			}
			
			layers.add(layer);
		}
		
		LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
		for(Tag entityTag : entityList)
		{
			Entity ent = WorldRegistry.readEntityFromNBT((CompoundTag)entityTag);
			if(ent != null)
			{
				ent.mapObj = this;
				entities.put(ent.UUID, ent);
			}
		}
		
		HashMap<Vector3, EntityTile> entityTiles = new HashMap<Vector3, EntityTile>();
		for(Tag entityTag : entityTileList)
		{
			CompoundTag tag2 = (CompoundTag) entityTag;
			
			int layerID = tag2.getInt("layerID", 0);
			int xCoord = tag2.getInt("xCoord", 0);
			int yCoord = tag2.getInt("yCoord", 0);
			
			EntityTile te = WorldRegistry.readEntityTileFromNBT(tag2, WorldRegistry.tileRegistry.get(layers.get(layerID)[xCoord][yCoord][0]).getTileEntity());
			if(te != null)
			{
				te.mapObj = this;
				entityTiles.put(new Vector3(layerID, xCoord, yCoord), te);
			}
		}
		
		this.entityMap = entities;
		this.layers = layers;
		this.tilesMap = entityTiles;
		
		for(int i = 0; i < this.layers.size(); i++)
		{
			this.layerVisibility.put(i, true);
		}
		
		this.nodeMap.recreate(this);
	}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args)
	{
		CompoundTag mapTag = new CompoundTag("map", new HashMap<String, Tag>());
		
		mapTag.putInt("id", id);
		mapTag.putInt("width", width);
		mapTag.putInt("height", height);
		mapTag.putBoolean("isAmbientLighting", isAmbientLighting);
		mapTag.putInt("spawnX", spawnX);
		mapTag.putInt("spawnY", spawnY);
		mapTag.putString("name", name);
		
		ListTag layerListTag = new ListTag("layers", CompoundTag.class, new ArrayList<Tag>());
		ListTag entityListTag = new ListTag("entities", CompoundTag.class, new ArrayList<Tag>());
		ListTag entityTilesListTag = new ListTag("entityTiles", CompoundTag.class, new ArrayList<Tag>());
		
		for(int l = 0; l < layers.size(); l++)
		{
			Integer[][][] layer = layers.get(l);		
			CompoundTag layerTag = new CompoundTag("layer", new HashMap<String, Tag>());
			CompoundTag tileTag = new CompoundTag("tiles", new HashMap<String, Tag>());
			CompoundTag dataTag = new CompoundTag("data", new HashMap<String, Tag>());
			
			for(int x = 0; x < width; x++)
			{
				int[] tiles = new int[height];
				int[] data = new int[height];			
				for(int y = 0; y < height; y++)
				{
					tiles[y] = layer[x][y][0] != null ? layer[x][y][0] : 0;
					data[y] = layer[x][y][1] != null ? layer[x][y][1] : 0;
				}		
				tileTag.putIntArray(String.valueOf(x), tiles);
				dataTag.putIntArray(String.valueOf(x), data);
			}
			layerTag.putTag(tileTag);
			layerTag.putTag(dataTag);
			
			layerListTag.addTag(layerTag);
		}
		
		for(Entity e : entityMap.values())
		{
			CompoundTag enitiyTag = WorldRegistry.writeEntityToNBT(e);
			entityListTag.addTag(enitiyTag);
		}
		
		for(EntityTile te : tilesMap.values())
		{
			CompoundTag teTag = WorldRegistry.writeEntityTileToNBT(te);
			entityTilesListTag.addTag(teTag);
		}
		
		mapTag.putTag(layerListTag);
		mapTag.putTag(entityListTag);
		mapTag.putTag(entityTilesListTag);
		
		return mapTag;
	}
	
	/**
	 * Writes this Map to {@link #saveFile}
	 * @param file
	 */
	public void writeToFile()
	{
		try {
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(saveFile));
			out.writeTag(writeToNBT(null));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads this Map from a given {@link File}.
	 * @param file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Map readFromFile(File file) throws FileNotFoundException, IOException
	{
		NBTInputStream in = new NBTInputStream(new FileInputStream(file));
		Map map = new Map();
		map.readFromNBT((CompoundTag) in.readTag());
		in.close();
		map.saveFile = file;
		return map;
	}
}
