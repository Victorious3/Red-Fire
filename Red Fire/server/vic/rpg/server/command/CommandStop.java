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
	public void cast(List<String> args) 
	{
		System.out.println("________________________________________________");
		System.err.println("Stopping Server ...");
		System.out.println("done!");
		Server.server.stopServer();	
	}

	@Override
	public String getUsage() 
	{
		return "/stop";
	}
}