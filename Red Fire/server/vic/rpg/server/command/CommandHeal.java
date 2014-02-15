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
			if(args.size() > 1)
			{
				try {
					float amount = Float.parseFloat(args.get(1));
					if(ServerLoop.level.onlinePlayersMap.containsKey(args.get(0)))
					{
						EntityController.setHealth(ServerLoop.level.getPlayer(args.get(0)), amount);
					}
					else
					{
						commandSender.print("&4No player named \"" + args.get(0) + "\" online!");
					}
				} catch (Exception e) {
					commandSender.print("&4Health has to be a float from 0.0 to 1.0!");
				}
			}
			else 
			{
				if(ServerLoop.level.onlinePlayersMap.containsKey(args.get(0)))
				{
					EntityController.setHealth(ServerLoop.level.getPlayer(args.get(0)), 1F);
				}
				else
				{
					commandSender.print("&4No player named \"" + args.get(0) + "\" online!");
				}
			}
		}
		else commandSender.print(getUsage()); 
	}

	@Override
	public String getUsage() 
	{
		return "/heal <player> [<health>]";
	}
}
