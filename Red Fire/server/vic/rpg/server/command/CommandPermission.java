package vic.rpg.server.command;

import java.util.List;

public class CommandPermission extends Command 
{
	protected CommandPermission() 
	{
		super("permission");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(args.size() == 0 || args.get(0).equals("help"))
		{
			if(commandSender.getPermission().hasPermission("permissions.help"))
			{
				commandSender.print("---- Permission: -----");
				commandSender.print("/permission reload: Reloads the permissions for every player.");
				commandSender.print("/permission help: Displays this page.");
				commandSender.print("/permission set group/player <group/player> <permission>: Sets the given permission for the specified group/player.");
				commandSender.print("/permission get group/player <group/player> <permission>: Checks the given permission for the specified group/player.");
				commandSender.print("/permission prefix group/player <group/player> <prefix>: Sets the prefix for the specified group/player.");
				commandSender.print("/permission suffix group/player <group/player> <suffix>: Sets the suffix for the specified group/player.");
				commandSender.print("/permission group list: Lists all currently active groups.");
				commandSender.print("/permission group create/delete <group>: Creates or deletes the specified group");
				commandSender.print("/permission player create/delete <player>: Creates or deletes the specified player");
				commandSender.print("/permission player add_group/remove_group <player> <group>: Adds or removes a group from the given player.");
				commandSender.print("/permission player list_groups <player>: Lists all the groups that the specified player is in.");
			}
			else noPermission(commandSender);
		}
		else
		{
			
		}
	}

	@Override
	public String getUsage() 
	{
		return "/permission <par1> [...] (Use /permission help for further information)";
	}
}
