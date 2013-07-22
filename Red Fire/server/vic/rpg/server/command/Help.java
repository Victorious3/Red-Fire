package vic.rpg.server.command;

import java.util.List;

public class Help extends Command 
{
	public Help() 
	{
		super("help");
	}

	@Override
	public void cast(List<String> args) 
	{
		if(args.size() > 0)
		{
			if(Command.commands.get(args.get(0)) != null)
			{
				Command.commands.get(args.get(0)).help();
			}
			else 
			{
				System.err.println("No command named \"" + args.get(0) + "\"! Try /help");
				return;
			}
		}
		else Command.getHelp(); 
	}

	@Override
	public String getUsage() 
	{
		return "/help [<command>]";
	}
}
