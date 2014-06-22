package vic.rpg.server.command;

import java.util.List;

public class CommandHelp extends Command 
{
	public CommandHelp() 
	{
		super("help");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(!commandSender.getPermission().hasPermission("utility.help"))
		{
			noPermission(commandSender);
			return;
		}
		if(args.size() > 0)
		{
			if(Command.commands.get(args.get(0)) != null)
			{
				Command.commands.get(args.get(0)).help(commandSender);
			}
			else 
			{
				commandSender.error("No command named \"" + args.get(0) + "\"! Try /help");
				return;
			}
		}
		else Command.getHelp(commandSender); 
	}

	@Override
	public String getUsage() 
	{
		return "/help [<command>]";
	}
}
