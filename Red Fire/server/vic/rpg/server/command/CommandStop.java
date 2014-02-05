package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.GameState;
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
		System.out.println("Stopping Server ...");
		Server.server.stopServer();
		while(Server.STATE != GameState.QUIT)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Server stopped!");
	}

	@Override
	public String getUsage() 
	{
		return "/stop";
	}
}
