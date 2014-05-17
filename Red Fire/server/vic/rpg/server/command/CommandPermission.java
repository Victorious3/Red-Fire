package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.io.Connection;
import vic.rpg.server.permission.PermissionHelper;

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
			permissionHelp(commandSender);
		}
		else
		{
			if(args.get(0).equals("reload"))
			{
				if(commandSender.getPermission().hasPermission("permissions.reload"))
				{
					PermissionHelper.reload();
				}
				else noPermission(commandSender);
			}
			else if(args.get(0).equals("set") || args.get(0).equals("get") || args.get(0).equals("prefix") || args.get(0).equals("suffix"))
			{
				int flag1 = 0;
				String permission = "";
				
				switch(args.get(0))
				{
				case "set" :
					flag1 = 1;
					permission = "permission.set";
					break;
				case "get" :
					flag1 = 2;
					permission = "permission.get";
					break;
				case "prefix" :
					flag1 = 3;
					permission = "permission.prefix";
					break;
				case "suffix" :
					flag1 = 4;
					permission = "permission.suffix";
					break;
				}
				
				if(args.size() > 1)
				{
					int flag2 = 0;
					switch(args.get(1))
					{
					case "player" :
						flag2 = 1;
						permission += ".player";
						break;
					case "group" :
						flag2 = 2;
						permission += ".group";
						break;
					default : commandSender.print("&4Missing argument 1: Specify type (player/group)."); return;
					}
					
					if(commandSender.getPermission().hasPermission(permission))
					{
						if(args.size() > 2)
						{
							String arg2 = args.get(2);
							if(args.size() > 3)
							{
								String arg3 = args.get(3);
								if(flag1 == 1 && flag2 == 1)
								{
									if(commandSender instanceof Connection && ((Connection)commandSender).username.equals(arg2) && !commandSender.getPermission().hasPermission("permissions.set.self")) noPermission(commandSender);
									else
									{
										if(PermissionHelper.addPermissionToPlayer(arg2, arg3))
										{
											commandSender.print("Permission " + arg3 + " was sucessfully added to player " + arg2 + ".");
										}
										else commandSender.print("&4There is no player named " + arg2 + ". Please create it first via '/permission player create <player>'");
									}
								}
								else if(flag1 == 1 && flag2 == 2)
								{
									if(PermissionHelper.addPermissionToGroup(arg2, arg3))
									{
										commandSender.print("Permission " + permission + " was sucessfully added to group " + arg2 + ".");
									}
									else commandSender.print("&4There is no group named " + arg2 + ". Please create it first via '/permission group create <group>'");
								}
								else if(flag1 == 2 && flag2 == 1)
								{
									commandSender.print("Got " + PermissionHelper.getPermissionForPlayer(arg2).hasPermission(arg3) + " for player " + arg2 + " and permission " + arg3 + ".");
								}
								else if(flag1 == 2 && flag2 == 2)
								{
									if(PermissionHelper.hasGroup(arg2))
									{
										commandSender.print("Got " + PermissionHelper.getPermissionForGroup(arg2).hasPermission(arg3) + " for group " + arg2 + " and permission " + permission + ".");
									}
									else commandSender.print("&4There is no group named " + arg2 + "!");
								}
								else if(flag1 == 3 && flag2 == 1)
								{
									if(PermissionHelper.setPrefixForPlayer(arg2, arg3))
									{
										commandSender.print("Prefix " + arg3 + " was sucsessfully added to player " + arg2);
									}		
									else commandSender.print("&4There is no player named " + arg2 + ". Please create it first via '/permission player create <player>'");
								}
								else if(flag1 == 3 && flag2 == 2)
								{
									if(PermissionHelper.setPrefixForGroup(arg2, arg3))
									{
										commandSender.print("Prefix " + arg3 + " was sucsessfully added to group " + arg2);
									}		
									else commandSender.print("&4There is no group named " + arg2 + ". Please create it first via '/permission group create <group>'");
								}
								else if(flag1 == 4 && flag2 == 1)
								{
									if(PermissionHelper.setSuffixForPlayer(arg2, arg3))
									{
										commandSender.print("Suffix " + arg3 + " was sucsessfully added to player " + arg2);
									}		
									else commandSender.print("&4There is no player named " + arg2 + ". Please create it first via '/permission player create <player>'");
								}
								else if(flag1 == 4 && flag2 == 2)
								{
									if(PermissionHelper.setSuffixForGroup(arg2, arg3))
									{
										commandSender.print("Suffix " + arg3 + " was sucsessfully added to group " + arg2);
									}		
									else commandSender.print("&4There is no group named " + arg2 + ". Please create it first via '/permission group create <group>'");
								}
							}
							else 
							{
								if(flag1 == 1) commandSender.print("&4Missing argument 3: Specify the permission to set."); 
								else if(flag1 == 2) commandSender.print("&4Missing argument 3: Specify the permission to check.");
								else if(flag1 == 3) commandSender.print("&4Missing argument 3: Specify the prefix to set.");
								else if(flag1 == 4) commandSender.print("&4Missing argument 3: Specify the suffix to set.");
							}
						}
						else 
						{
							if(flag2 == 1) commandSender.print("&4Missing argument 2: Specify name of the player.");
							else commandSender.print("&4Missing argument 2: Specify name of the group.");
						}
					}
					else noPermission(commandSender);
				}
				else commandSender.print("&4Missing argument 1: Specify type (player/group).");
			}
			else if(args.get(0).equals("group"))
			{
				if(args.size() > 1)
				{
					if(args.get(1).equals("list"))
					{
						commandSender.print("Current groups are: " + PermissionHelper.getGroups());
					}
					else if(args.get(1).equals("create"))
					{
						if(args.size() > 2)
						{
							if(PermissionHelper.createGroup(args.get(2)))
							{
								commandSender.print("Group " + args.get(2) + " was sucsessfully created.");
							}
							else commandSender.print("&4There is already a group named " + args.get(2) + "!");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the group.");
					}
					else if(args.get(1).equals("delete"))
					{
						if(args.size() > 2)
						{
							if(PermissionHelper.removeGroup(args.get(2)))
							{
								commandSender.print("Group " + args.get(2) + " was sucsessfully deleted.");
							}
							else commandSender.print("&4There is no group named " + args.get(2) + "!");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the group.");
					}
					else commandSender.print("&4Missing argument 1: list/create/delete");
				}
				else commandSender.print("&4Missing argument 1: list/create/delete");
			}
			else if(args.get(0).equals("player"))
			{
				if(args.size() > 1)
				{
					if(args.get(1).equals("create"))
					{
						if(args.size() > 2)
						{
							if(PermissionHelper.createPlayer(args.get(2)))
							{
								commandSender.print("Player " + args.get(2) + " was sucsessfully created.");
							}
							else commandSender.print("&4There is already a player named " + args.get(2) + "!");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the player.");
					}
					else if(args.get(1).equals("delete"))
					{
						if(args.size() > 2)
						{
							if(PermissionHelper.removePlayer(args.get(2)))
							{
								commandSender.print("Player " + args.get(2) + " was sucsessfully deleted.");
							}
							else commandSender.print("&4There is no player named " + args.get(2) + "!");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the player.");
					}
					else if(args.get(1).equals("add_group"))
					{
						if(args.size() > 2)
						{
							if(args.size() > 3)
							{
								if(PermissionHelper.hasPlayer(args.get(2)))
								{
									if(PermissionHelper.hasGroup(args.get(3)))
									{
										PermissionHelper.addGroupToPlayer(args.get(2), args.get(3));
										commandSender.print("Group " + args.get(3) + " was sucsessfully added to player " + args.get(2) + ".");
									}
									else commandSender.print("&4There is no group named " + args.get(3) + "!");
								}
								else commandSender.print("&4There is no player named " + args.get(2) + "!");
							}
							else commandSender.print("&4Missing argument 3: Specify name of the group to add.");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the player.");
					}
					else if(args.get(1).equals("remove_group"))
					{
						if(args.size() > 2)
						{
							if(args.size() > 3)
							{
								if(PermissionHelper.hasPlayer(args.get(2)))
								{
									PermissionHelper.removeGroupFromPlayer(args.get(2), args.get(3));
									commandSender.print("Group " + args.get(3) + " was sucsessfully removed from player " + args.get(2) + " if it existed before.");
								}
								else commandSender.print("&4There is no player named " + args.get(2) + "!");
							}
							else commandSender.print("&4Missing argument 3: Specify name of the group to remove.");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the player.");
					}
					else if(args.get(1).equals("list_groups"))
					{
						if(args.size() > 2)
						{
							if(PermissionHelper.hasPlayer(args.get(2)))
							{
								commandSender.print("Player " + args.get(2) + " has the following groups: " + PermissionHelper.getGroupsForPlayer(args.get(2)));
							}
							else commandSender.print("&4There is no player named " + args.get(2) + "!");
						}
						else commandSender.print("&4Missing argument 2: Specify name of the player.");
					}
					else commandSender.print("&4Missing argument 1: create/delete/add_group/remove_group/list_groups");
				}
				else commandSender.print("&4Missing argument 1: create/delete/add_group/remove_group/list_groups");
			}
			else permissionHelp(commandSender);
		}
	}
	
	private void permissionHelp(CommandSender commandSender)
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
			commandSender.print("---------------------");
		}
		else noPermission(commandSender);
	}

	@Override
	public String getUsage() 
	{
		return "/permission <par1> [...] (Use /permission help for further information)";
	}
}
