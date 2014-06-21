package vic.rpg.world;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import vic.rpg.registry.WorldRegistry;
import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.server.io.Connection;
import vic.rpg.server.packet.Packet10TimePacket;
import vic.rpg.server.packet.Packet6World;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.utils.Utils;
import vic.rpg.world.entity.living.EntityPlayer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

/**
 * A World contains multiple {@link Map Maps} that are loaded into the server's RAM.
 * A World saves to a directory containing all the different
 * {@link Map Maps}. Teleporters will be used to change the map for a player and resend the data to the client.
 * @author Victorious3
 */
public class World 
{
	private HashMap<Integer, Map> maps = new HashMap<Integer, Map>();
	private File saveFile;
	private String name;
	private Point spawnLocation;
	private int spawnMap;
	
	public LinkedHashMap<String, Object[]> onlinePlayersMap = new LinkedHashMap<String, Object[]>();
	public LinkedHashMap<String, EntityPlayer> offlinePlayersMap = new LinkedHashMap<String, EntityPlayer>();
	public int time;
	
	/**
	 * Returns the name of a World from a given {@link File}. Used to check weather a file is a valid World file.
	 * @param file
	 * @return String {@link World#name} or {@code null} if the file is not valid.
	 */
	public static String getWorldName(File file)
	{
		try {
			File mapData = new File(file.getAbsoluteFile() + "/mapdata.json");	
			JsonParser parser = new JsonParser();
			JsonObject obj = (JsonObject)parser.parse(new FileReader(mapData));
			return obj.get("name").getAsString();	
		} catch (Exception e) {

		}
		return null;
	}
	
	public Map getMap(Integer mapID)
	{
		return maps.get(mapID);
	}
	
	public EntityPlayer createPlayer(String username)
	{
		if(offlinePlayersMap.containsKey(username))
		{
			EntityPlayer player = offlinePlayersMap.get(username);
			maps.get(player.dimension).addEntity(player);
			onlinePlayersMap.put(username, new Object[]{player.UUID, player.dimension});
			return player;
		}
		else
		{
			EntityPlayer player = new EntityPlayer();
			maps.get(spawnMap).createPlayer(player, username, spawnLocation.x, spawnLocation.y);
			onlinePlayersMap.put(username, new Object[]{player.UUID, player.dimension});
			return player;
		}
	}
	
	public void changeMaps(int dimension, String username, int xCoord, int yCoord)
	{
		EntityPlayer player = removePlayer(username);
		Server.server.broadcastLocally(player.dimension, new Packet7Entity(player, Packet7Entity.MODE_DELETE), player.username);
		player.dimension = dimension;
		player.xCoord = xCoord;
		player.yCoord = yCoord;
		getMap(dimension).addEntity(player);
		onlinePlayersMap.put(username, new Object[]{player.UUID, player.dimension});
		Connection con = Server.connections.get(username);
		con.packetHandler.addPacketToSendingQueue(new Packet6World(getMap(dimension)));
		Server.server.broadcastLocally(dimension, new Packet7Entity(player, Packet7Entity.MODE_CREATE));
		con.STATE = GameState.LOADING;
	}
	
	public boolean isPlayerOnline(String username)
	{
		return onlinePlayersMap.containsKey(username);
	}
	
	public int getDimension(String username)
	{
		if(isPlayerOnline(username))
		{
			return (int)onlinePlayersMap.get(username)[1];
		}
		else return offlinePlayersMap.get(username).dimension;
	}
	
	public String getUUID(String username)
	{
		if(isPlayerOnline(username))
		{
			return (String)onlinePlayersMap.get(username)[0];
		}
		else return offlinePlayersMap.get(username).UUID;
	}
	
	public EntityPlayer removePlayer(String username)
	{
		if(onlinePlayersMap.containsKey(username))
		{
			Object[] obj = onlinePlayersMap.remove(username);
			EntityPlayer player = (EntityPlayer)maps.get(obj[1]).removeEntity((String)obj[0]);
			offlinePlayersMap.put(player.username, player);
			return player;
		}
		return null;
	}
	
	public EntityPlayer getPlayer(String username)
	{
		Object[] obj = onlinePlayersMap.get(username);
		return (EntityPlayer)maps.get(obj[1]).getEntity((String)obj[0]);
	}
	
	/**
	 * Reads this World from a given {@link File}.
	 * @param file
	 * @throws JsonIOException
	 * @throws JsonSyntaxException
	 * @throws IOException 
	 */
	public void readFromFile(File file) throws JsonIOException, JsonSyntaxException, IOException
	{
		FilenameFilter filter = new FilenameFilter() 
		{	
			@Override
			public boolean accept(File dir, String name) 
			{
				return name.endsWith(".map");
			}
		};
		
		File mapData = new File(file.getAbsoluteFile() + "/mapdata.json");	
		if(mapData.exists())
		{
			JsonParser parser = new JsonParser();
			JsonObject obj = (JsonObject)parser.parse(new FileReader(mapData));
			name = obj.get("name").getAsString();
			spawnMap = obj.get("spawnID").getAsInt();
			spawnLocation = new Point(obj.get("spawnX").getAsInt(), obj.get("spawnY").getAsInt());
			time = obj.get("time").getAsInt();
			
			File playerData = new File(file.getAbsoluteFile() + "/players.nbt");
			if(!playerData.exists()) playerData.createNewFile();
			else
			{
				NBTInputStream nbtin = new NBTInputStream(new FileInputStream(playerData));
				ListTag playersList = ((CompoundTag)nbtin.readTag()).getListTag("players");
				for(Tag tag : playersList.getValue())
				{
					EntityPlayer player = (EntityPlayer)WorldRegistry.readEntityFromNBT((CompoundTag)tag);
					offlinePlayersMap.put(player.username, player);
				}
			}
			
			for(File mapFile : file.listFiles(filter))
			{
				try {
					Map map = Map.readFromFile(mapFile);
					maps.put(map.id, map);
				} catch (Exception e) {
					System.out.println("Skipping damaged map " + mapFile.getAbsolutePath());
				}
			}
			saveFile = file;
		}
	}
	
	/**
	 * Writes this World to {@link #saveFile}.
	 * @throws IOException 
	 */
	public void writeToFile() throws IOException
	{
		if(saveFile == null) return;
		File mapData = Utils.getOrCreateFile(saveFile.getAbsoluteFile() + "/mapdata.json");
		
		JsonObject mainObj = new JsonObject();
		mainObj.add("name", new JsonPrimitive(name));
		mainObj.add("spawnID", new JsonPrimitive(spawnMap));
		mainObj.add("spawnX", new JsonPrimitive(spawnLocation.x));
		mainObj.add("spawnY", new JsonPrimitive(spawnLocation.y));
		mainObj.add("time", new JsonPrimitive(time));
		
		FileWriter writer = new FileWriter(mapData);	
		writer.write(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(mainObj));
		writer.flush();
		writer.close();
		
		for(Map map : maps.values())
		{
			map.writeToFile();
		}
		
		File playerData = Utils.getOrCreateFile(saveFile.getAbsoluteFile() + "/players.nbt");
		NBTOutputStream out = new NBTOutputStream(new FileOutputStream(playerData));
		ListTag playersList = new ListTag("players", CompoundTag.class, new ArrayList<Tag>());
		
		for(EntityPlayer player : offlinePlayersMap.values())
		{
			playersList.addTag(WorldRegistry.writeEntityToNBT(player));
		}

		CompoundTag playerDataTag = new CompoundTag("", new HashMap<String, Tag>());
		playerDataTag.putTag(playersList);
		out.writeTag(playerDataTag);
		out.close();
	}

	int tickCounter = 0;
	public void tick() 
	{
		tickCounter++;
		
		if(tickCounter == 10)
		{
			time++;
			if(time >= 10000)
			{
				time = 0;
			}
			Server.server.broadcast(new Packet10TimePacket(time));
			tickCounter = 0;
		}
		
		for(Map map : maps.values())
		{
			map.tick();
		}
	}
}
