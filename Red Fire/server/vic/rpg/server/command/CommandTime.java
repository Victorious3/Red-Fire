package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.ServerLoop;

public class CommandTime extends Command
{
	public CommandTime() 
	{
		super("time");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(args.size() > 0 && args.get(0).equals("set"))
		{
			if(commandSender.getPermission().hasPermission("world.time.set"))
			{
				if(args.size() > 1 && args.get(1) != null) 
				{
					String time = args.get(1);
					try {
						int t2 = Integer.parseInt(time);
						ServerLoop.world.time = t2;
						commandSender.print("Time set to " + t2);
					} catch (NumberFormatException e) {
						commandSender.print("Time has to be numeric!");
					}				
				}
				else
				{
					help(commandSender);
				}
			}
			else noPermission(commandSender);
		}
		else if(commandSender.getPermission().hasPermission("world.time.get")) commandSender.print("The current game time is " + ServerLoop.world.time);
		else noPermission(commandSender);
	}

	@Override
	public String getUsage() 
	{
		return "/time [set <time>]";
	}
}
