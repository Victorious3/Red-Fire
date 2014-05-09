package vic.rpg.server.permission;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import vic.rpg.server.Server;
import vic.rpg.utils.Utils;

public class PermissionHelper 
{
	private static JSONObject unparsedPermissions;
	private static HashMap<String, PermissionReceiver> groups = new HashMap<String, PermissionReceiver>();
	private static HashMap<String, PermissionReceiver> players = new HashMap<String, PermissionReceiver>();
	
	@SuppressWarnings("unchecked")
	public static void loadPermissions()
	{
		File file = new File(Utils.getAppdata() + "/permissions.json");
		if(!file.exists())
		{
			try {
				Files.copy(Utils.getFileFromJar("/vic/rpg/resources/permissions.json").toPath(), file.toPath());
			} catch (IOException e) {
				System.out.println("[PermissionHelper]: Permission file could not be loaded! Aborting...");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		JSONParser parser = new JSONParser();
		try {
			unparsedPermissions = (JSONObject)parser.parse(new FileReader(file));
			
			JSONArray groups = (JSONArray)unparsedPermissions.get("groups");
			for(Object o : groups)
			{
				JSONObject obj = (JSONObject)o;
				PermissionReceiver rec = new PermissionReceiver();
				rec.name = (String)obj.get("name");
				rec.prefix = obj.containsKey("prefix") ? (String)obj.get("prefix") : null;
				rec.suffix = obj.containsKey("suffix") ? (String)obj.get("suffix") : null;
				rec.permission = parse((JSONArray)obj.get("permissions"));
				PermissionHelper.groups.put(rec.name, rec);
			}
			
			JSONArray players = (JSONArray)unparsedPermissions.get("players");
			for(Object o : players)
			{
				JSONObject obj = (JSONObject)o;
				PermissionReceiver rec = new PermissionReceiver();
				rec.name = (String)obj.get("name");
				rec.prefix = obj.containsKey("prefix") ? (String)obj.get("prefix") : null;
				rec.suffix = obj.containsKey("suffix") ? (String)obj.get("suffix") : null;
				rec.permission = obj.containsKey("permissions") ? parse((JSONArray)obj.get("permissions")) : null;
				rec.groups = (String[]) ((JSONArray)obj.get("groups")).toArray(new String[((JSONArray)obj.get("groups")).size()]);
				
				PermissionHelper.players.put(rec.name, rec);
			}
			
		} catch (Exception e) {
			System.out.println("[PermissionHelper]: Permission file is invalid! Aborting...");
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("[PermissionHelper]: Sucessfully loaded a total of " + groups.size() + " Groups and " + players.size() + " Players!");
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
					System.out.println("[PermissionHelper]: Player " + playerName + " has a specified group that does not exist: " + s + "!");
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
					System.out.println("[PermissionHelper]: Player " + playerName + " has a specified group that does not exist: " + s + "!");
				}
			}
		}
		return ret;
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
					System.out.println("[PermissionHelper]: Player " + playerName + " has a specified group that does not exist: " + s + "!");
				}
			}
		}
		return ret;
	}
	
	private static Permission parse(JSONArray perm)
	{
		Permission ret = Permission.createRoot(false);
		for(Object o : perm)
		{
			ret.add((String)o);
		}
		return ret;
	}
}
