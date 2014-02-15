package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.Server;
import vic.rpg.server.ServerLoop;

public class CommandKick extends Command
{
	public CommandKick() 
	{
		super("kick");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(args.size() > 0)
		{
			if(ServerLoop.level.onlinePlayersMap.containsKey(args.get(0)))
			{
				Server.server.delConnection(Server.connections.get(args.get(0)), "kick");
			}
			else
			{
				commandSender.print("&4No player named \"" + args.get(0) + "\" online!");
			}
		}
		else commandSender.print(getUsage()); 
	}

	@Override
	public String getUsage() 
	{
		return "/kick <username> [<reason>]";
	}
	
}
