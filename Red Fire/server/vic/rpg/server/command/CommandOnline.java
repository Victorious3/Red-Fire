package vic.rpg.server.command;

import java.util.Iterator;
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
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(!commandSender.getPermission().hasPermission("utility.online"))
		{
			noPermission(commandSender);
			return;
		}
		
		String out = "";
		out += "Online Players: " + Server.actConnections + " (";
		
		Iterator<Connection> iter = Server.connections.values().iterator();
		while(iter.hasNext())
		{
			out += iter.next().username + (iter.hasNext() ? ", " : "");
		}
		
		out += ")";
		commandSender.print(out);
	}

	@Override
	public String getUsage() 
	{
		return "/online";
	}
}
