package vic.rpg.server.command;

import vic.rpg.server.permission.Permission;

public interface CommandSender 
{
	public void print(String string);
	public void error(String string);
	public Permission getPermission();
}
