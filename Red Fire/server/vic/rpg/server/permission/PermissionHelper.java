package vic.rpg.server.permission;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import vic.rpg.server.Server;
import vic.rpg.server.io.Connection;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Logger.LogLevel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class PermissionHelper 
{
	private static JsonObject unparsedPermissions;
	private static HashMap<String, PermissionReceiver> groups = new HashMap<String, PermissionReceiver>();
	private static HashMap<String, PermissionReceiver> players = new HashMap<String, PermissionReceiver>();
	
	@SuppressWarnings("unchecked")
	public static void loadPermissions()
	{
		File file = new File(Utils.getAppdata() + "/permissions.json");
		if(!file.exists())
		{
			Logger.log(LogLevel.WARNING, "[PermissionHelper]: Created permission file " + file + " with standard permission set.");
			try {
				Utils.copyFileFromJar("/vic/rpg/resources/permissions.json", file.getAbsolutePath());
			} catch (IOException e) {
				Logger.log(LogLevel.SEVERE, "[PermissionHelper]: Permission file could not be loaded! Aborting...");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		JsonParser parser = new JsonParser();
		try {
			unparsedPermissions = (JsonObject)parser.parse(new FileReader(file));
			
			JsonArray groups = (JsonArray)unparsedPermissions.get("groups");
			for(Object o : groups)
			{
				JsonObject obj = (JsonObject)o;
				PermissionReceiver rec = new PermissionReceiver();
				rec.name = obj.get("name").getAsString();
				rec.prefix = obj.has("prefix") ? obj.get("prefix").getAsString() : null;
				rec.suffix = obj.has("suffix") ? obj.get("suffix").getAsString() : null;
				rec.permission = parse(obj.get("permissions").getAsJsonArray());
				PermissionHelper.groups.put(rec.name, rec);
			}
			
			JsonArray players = (JsonArray)unparsedPermissions.get("players");
			for(Object o : players)
			{
				JsonObject obj = (JsonObject)o;
				PermissionReceiver rec = new PermissionReceiver();
				rec.name = obj.get("name").getAsString();
				rec.prefix = obj.has("prefix") ? obj.get("prefix").getAsString() : null;
				rec.suffix = obj.has("suffix") ? obj.get("suffix").getAsString() : null;
				rec.permission = obj.has("permissions") ? parse(obj.get("permissions").getAsJsonArray()) : null;
				rec.groups = new Gson().fromJson(obj.get("groups").getAsJsonArray(), ArrayList.class);
				
				PermissionHelper.players.put(rec.name, rec);
			}
			
		} catch (Exception e) {
			Logger.log(LogLevel.SEVERE, "[PermissionHelper]: Permission file is invalid! Aborting...");
			e.printStackTrace();
			System.exit(-1);
		}
		Logger.log(LogLevel.FINEST, "[PermissionHelper]: Sucessfully loaded a total of " + groups.size() + " Groups and " + players.size() + " Players!");
	}
	
	public static void savePermissions()
	{
		JsonArray groupsArray = new JsonArray();
		for(PermissionReceiver group : groups.values())
		{
			JsonObject groupObj = new JsonObject();
			groupObj.addProperty("name", group.name);
			if(group.prefix != null) groupObj.addProperty("prefix", group.prefix);
			if(group.suffix != null) groupObj.addProperty("suffix", group.suffix);
			
			JsonArray permissionArray = new JsonArray();		
			for(String s : group.permission.getAllPermissions().split("\n"))
			{
				permissionArray.add(new JsonPrimitive(s));
			}		
			groupObj.add("permissions", permissionArray);
			
			groupsArray.add(groupObj);
		}
		
		JsonArray playersArray = new JsonArray();
		for(PermissionReceiver player : players.values())
		{
			JsonObject playerObj = new JsonObject();
			playerObj.addProperty("name", player.name);
			if(player.prefix != null) playerObj.addProperty("prefix", player.prefix);
			if(player.suffix != null) playerObj.addProperty("suffix", player.suffix);
			
			if(player.permission != null)
			{
				JsonArray permissionArray = new JsonArray();
				for(String s : player.permission.getAllPermissions().split("\n"))
				{
					permissionArray.add(new JsonPrimitive(s));
				}
				playerObj.add("permissions", permissionArray);
			}
			
			JsonArray playerGroupsArray = new JsonArray();
			for(String s : player.groups)
			{
				playerGroupsArray.add(new JsonPrimitive(s));
			}
			playerObj.add("groups", playerGroupsArray);
			
			playersArray.add(playerObj);
		}
		
		JsonObject mainObj = new JsonObject();
		mainObj.add("groups", groupsArray);
		mainObj.add("players", playersArray);
		
		File out = Utils.getOrCreateFile(Utils.getAppdata() + "/permissions.json");
		try {
			FileWriter writer = new FileWriter(out);
			writer.write(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(mainObj));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Permission getPermissionForGroup(String groupName)
	{
		if(groups.containsKey(groupName))
		{
			return groups.get(groupName).permission;
		}
		return Permission.createRoot(false);
	}

	public static Permission getPermissionForPlayer(String playerName)
	{
		if(Server.isSinglePlayer) return Permission.createRoot(true);
		Permission ret = Permission.createRoot(false);
		if(players.containsKey(playerName))
		{
			PermissionReceiver player = players.get(playerName);

			for(String s : player.groups)
			{
				if(groups.containsKey(s))
				{
					for(String perm : groups.get(s).permission.getAllPermissions().split("\n"))
					{
						ret.add(perm);
					}
				}
				else
				{
					Logger.log(LogLevel.WARNING, "[PermissionHelper]: Player " + playerName + " has a specified group that does not exist: " + s + "!");
				}
			}
			if(player.permission != null)
			{
				for(String perm : player.permission.getAllPermissions().split("\n"))
				{
					ret.add(perm);
				}
			}
		}
		else if(groups.containsKey("default"))
		{
			ret = groups.get("default").permission;
		}
		return ret;
	}
	
	public static boolean setPrefixForPlayer(String playerName, String prefix)
	{
		if(players.containsKey(playerName))
		{
			players.get(playerName).prefix = prefix;
			return true;
		}
		return false;
	}
	
	public static boolean setPrefixForGroup(String groupName, String prefix)
	{
		if(groups.containsKey(groupName))
		{
			groups.get(groupName).prefix = prefix;
			return true;
		}
		return false;
	}
	
	public static boolean setSuffixForPlayer(String playerName, String suffix)
	{
		if(players.containsKey(playerName))
		{
			players.get(playerName).suffix = suffix;
			return true;
		}
		return false;
	}
	
	public static boolean setSuffixForGroup(String groupName, String suffix)
	{
		if(groups.containsKey(groupName))
		{
			groups.get(groupName).suffix = suffix;
			return true;
		}
		return false;
	}
	
	public static boolean hasPlayer(String playerName)
	{
		return players.containsKey(playerName);
	}
	
	public static boolean hasGroup(String groupName)
	{
		return groups.containsKey(groupName);
	}
	
	public static boolean addPermissionToPlayer(String playerName, String permission)
	{
		if(players.containsKey(playerName))
		{
			if(players.get(playerName).permission == null) players.get(playerName).permission = Permission.createRoot(false);
			players.get(playerName).permission.add(permission);
			return true;
		}
		return false;
	}
	
	public static boolean addPermissionToGroup(String groupName, String permission)
	{
		if(groups.containsKey(groupName))
		{
			groups.get(groupName).permission.add(permission);
			return true;
		}
		return false;
	}
	
	public static boolean createPlayer(String playerName)
	{
		if(players.containsKey(playerName)) return false;
		
		PermissionReceiver rec = new PermissionReceiver();
		rec.permission = getPermissionForPlayer(playerName);
		rec.groups = new ArrayList<String>();
		
		players.put(playerName, rec);
		return true;
	}
	
	public static boolean createGroup(String groupName)
	{
		if(groups.containsKey(groupName)) return false;
		
		PermissionReceiver rec = new PermissionReceiver();
		rec.permission = Permission.createRoot(false);
		
		groups.put(groupName, rec);
		return true;
	}
	
	public static boolean removePlayer(String playerName)
	{
		if(!players.containsKey(playerName)) return false;
		players.remove(playerName);
		return true;
	}
	
	public static boolean removeGroup(String groupName)
	{
		if(!groups.containsKey(groupName)) return false;
		groups.remove(groupName);
		return true;
	}
	
	public static String getSuffix(String playerName)
	{
		String ret = "";
		if(players.containsKey(playerName))
		{
			PermissionReceiver player = players.get(playerName);
			if(player.getSuffix().length() > 0) return player.getSuffix();
			for(String s : player.groups)
			{
				if(groups.containsKey(s))
				{
					PermissionReceiver group = groups.get(s);
					if(group.getSuffix().length() > 0) ret = group.getSuffix();
				}
				else
				{
					Logger.log(LogLevel.WARNING, "[PermissionHelper]: Player " + playerName + " has a specified group that does not exist: " + s + "!");
				}
			}
		}
		return ret;
	}
	
	public static String getGroups()
	{
		String s = "";
		Iterator<String> iter = groups.keySet().iterator();
		while(iter.hasNext())
		{
			s += iter.next() + (iter.hasNext() ? ", " : "");
		}
		return s;
	}
	
	public static String getGroupsForPlayer(String playerName)
	{
		String s = "";
		if(!players.containsKey(playerName)) return s;
		
		Iterator<String> iter = players.get(playerName).groups.iterator();
		while(iter.hasNext())
		{
			s += iter.next() + (iter.hasNext() ? ", " : "");
		}
		return s;
	}
	
	public static void addGroupToPlayer(String playerName, String groupName)
	{
		if(players.containsKey(groupName) && groups.containsKey(groupName))
		{
			if(players.get(playerName).groups == null) players.get(playerName).groups = new ArrayList<String>();
			players.get(playerName).groups.add(groupName);
		}
	}
	
	public static void removeGroupFromPlayer(String playerName, String groupName)
	{
		if(players.containsKey(groupName))
		{
			players.get(playerName).groups.remove(groupName);
		}
	}
	
	public static void reload()
	{
		Logger.log("Reloading permissions...");
		synchronized(Server.connections) 
		{
			for(Connection con : Server.getConnections().values())
			{
				con.permission = getPermissionForPlayer(con.username);
				con.prefix = getPrefix(con.username);
				con.suffix = getSuffix(con.username);
			}
		}
		Logger.log("done!");
	}
	
	public static String getPrefix(String playerName)
	{
		String ret = "";
		if(players.containsKey(playerName))
		{
			PermissionReceiver player = players.get(playerName);
			if(player.getPrefix().length() > 0) return player.getPrefix();
			for(String s : player.groups)
			{
				if(groups.containsKey(s))
				{
					PermissionReceiver group = groups.get(s);
					if(group.getPrefix().length() > 0) ret = group.getPrefix();
				}
				else
				{
					Logger.log(LogLevel.WARNING, "[PermissionHelper]: Player " + playerName + " has a specified group that does not exist: " + s + "!");
				}
			}
		}
		return ret;
	}
	
	private static Permission parse(JsonArray perm)
	{
		Permission ret = Permission.createRoot(false);
		for(Object o : perm)
		{
			ret.add(((JsonPrimitive)o).getAsString());
		}
		return ret;
	}
}
