package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.Server;
import vic.rpg.server.io.Connection;

public class CommandOnline extends Command 
{
	public CommandOnline() 
	{
		super("online");
	}

	@Override
	public void cast(List<String> args) 
	{
		String out = "";
		out += "Online Players: " + Server.actConnections + " (";
		
		for(Connection con : Server.connections.values())
		{
			out += con.username + ", ";
		}
		
		out += ")";
		System.out.println(out);
	}

	@Override
	public String getUsage() 
	{
		return "/online";
	}
}
