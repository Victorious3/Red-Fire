package vic.rpg.server.io;

import vic.rpg.server.command.CommandSender;
import vic.rpg.server.permission.Permission;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Logger.LogLevel;

//TODO This should become a Bot someday.
public class BotConnection implements CommandSender
{	
	public String username;
	public String prefix = "";
	public String suffix = "";
	
	public Permission permission;
	
	public BotConnection(String username)
	{
		this.username = username;
	}
	
	@Override
	public void print(String string) 
	{
		Logger.log(username + "");
	}
	
	@Override
	public void error(String string) 
	{
		Logger.log(LogLevel.SEVERE, username + "");
	} 

	@Override
	public Permission getPermission() 
	{
		return permission;
	}
	
	public void finalize()
	{
		
	}
}
