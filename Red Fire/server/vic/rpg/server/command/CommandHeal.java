package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.ServerLoop;
import vic.rpg.server.io.Connection;
import vic.rpg.world.entity.living.EntityController;

public class CommandHeal extends Command 
{	
	public CommandHeal() 
	{
		super("heal");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(!commandSender.getPermission().hasPermission("world.heal"))
		{
			noPermission(commandSender);
			return;
		}
		if(args.size() == 0 && commandSender instanceof Connection)
		{
			Connection con = (Connection)commandSender;
			args.add(con.username);
			args.add("1");
		}
		if(args.size() > 0)
		{
			if(args.size() > 1)
			{
				try {
					float amount = Float.parseFloat(args.get(1));
					if(ServerLoop.world.isPlayerOnline(args.get(0)))
					{
						EntityController.setHealth(ServerLoop.world.getPlayer(args.get(0)), amount);
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
				if(ServerLoop.world.isPlayerOnline(args.get(0)))
				{
					EntityController.setHealth(ServerLoop.world.getPlayer(args.get(0)), 1F);
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
