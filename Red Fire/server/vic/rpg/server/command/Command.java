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
	}
	
	public static void getHelp()
	{
		System.out.println("---- Help: -----------");
		for(String s : commands.keySet())
		{
			Command c = commands.get(s);
			System.out.println(s + ": " + c.getUsage());
		}
		System.out.println("----------------------");
	}
	
	private String name;
	
	public Command(String name)
	{
		this.name = name;
	}
	
	public void help()
	{
		System.err.println("/" + name + ":");
		System.err.println("Usage: " + getUsage());
	}
	
	public abstract void cast(List<String> args);
	
	public abstract String getUsage();
}
