package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.Server;

public class CommandStop extends Command 
{
	public CommandStop() 
	{
		super("stop");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		System.err.println("Stopping Server ...");
		Server.server.stopServer();	
		System.out.println("done!");
	}

	@Override
	public String getUsage() 
	{
		return "/stop";
	}
}
