package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Logger.LogLevel;

public class CommandStop extends Command 
{
	public CommandStop() 
	{
		super("stop");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(!commandSender.getPermission().hasPermission("operator.stop"))
		{
			noPermission(commandSender);
			return;
		}
		
		Logger.log(LogLevel.SEVERE, "Stopping Server...");
		Server.server.stopServer();
		while(Server.STATE != GameState.QUIT)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.log("Server stopped!");
	}

	@Override
	public String getUsage() 
	{
		return "/stop";
	}
}
