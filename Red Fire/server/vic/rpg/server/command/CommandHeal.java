package vic.rpg.server.command;

import java.util.List;

import vic.rpg.level.entity.living.EntityController;
import vic.rpg.server.ServerLoop;

public class CommandHeal extends Command 
{	
	public CommandHeal() 
	{
		super("heal");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(args.size() > 0)
		{
			if(ServerLoop.level.onlinePlayersMap.containsKey(args.get(0)))
			{
				EntityController.setHealth(ServerLoop.level.onlinePlayersMap.get(args.get(0)), 1F);
			}
			else
			{
				commandSender.print("No player named \"" + args.get(0) + "\" online!");
			}
		}
		else Command.getHelp(commandSender); 
	}

	@Override
	public String getUsage() 
	{
		return "/heal [<player>]";
	}
}
