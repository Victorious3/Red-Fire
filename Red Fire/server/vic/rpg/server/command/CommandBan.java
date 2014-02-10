package vic.rpg.server.command;

import java.util.List;

public class CommandBan extends Command
{
	public CommandBan() 
	{
		super("ban");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		//TODO Add ban.
	}

	@Override
	public String getUsage() 
	{
		return "/ban <username> [<reason>]";
	}
}
