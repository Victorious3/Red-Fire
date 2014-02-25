package vic.rpg.server.command;

import java.util.HashMap;
import java.util.List;

import vic.rpg.Init;
import vic.rpg.utils.Utils.Side;

public abstract class Command 
{
	public static HashMap<String, Command> commands = new HashMap<String, Command>();
	
	@Init(side = Side.SERVER)
	public static void init()
	{
		commands.put("help", new CommandHelp());
		commands.put("online", new CommandOnline());
		commands.put("say", new CommandSay());
		commands.put("stop", new CommandStop());
		commands.put("time", new CommandTime());
		commands.put("heal", new CommandHeal());
		commands.put("ban", new CommandBan());
		commands.put("kick", new CommandKick());
		commands.put("give", new CommandGive());
	}
	
	public static void getHelp(CommandSender commandSender)
	{
		commandSender.print("---- Help: -----------");
		for(String s : commands.keySet())
		{
			Command c = commands.get(s);
			commandSender.print(s + ": " + c.getUsage());
		}
		commandSender.print("----------------------");
	}
	
	private String name;
	
	public Command(String name)
	{
		this.name = name;
	}
	
	public void help(CommandSender commandSender)
	{
		commandSender.print("&4/" + name + ":");
		commandSender.print("&4Usage: " + getUsage());
	}
	
	public abstract void cast(List<String> args, CommandSender commandSender);
	
	public abstract String getUsage();
}
