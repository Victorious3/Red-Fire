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
	public void cast(List<String> args) 
	{
		if(args.size() > 0 && args.get(0).equals("set"))
		{
			if(args.size() > 1 && args.get(1) != null) 
			{
				String time = args.get(1);
				try {
					int t2 = Integer.parseInt(time);
					ServerLoop.level.time = t2;
					System.out.println("Time set to " + t2);
				} catch (NumberFormatException e) {
					System.err.println("Time has to be numeric!");
				}				
			}
			else
			{
				help();
			}
		}
		else System.out.println("The current game time is " + ServerLoop.level.time);
	}

	@Override
	public String getUsage() 
	{
		return "/time [set <time>]";
	}
}
