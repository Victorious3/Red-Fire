package vic.rpg.server.command;

import java.util.List;

public class CommandKick extends Command
{
	public CommandKick() 
	{
		super("kick");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		//TODO Add kick.
	}

	@Override
	public String getUsage() 
	{
		return "/kick <username> [<reason>]";
	}
	
}
