package vic.rpg.server.command;

import java.util.HashMap;
import java.util.List;

public abstract class Command 
{
	public static HashMap<String, Command> commands = new HashMap<String, Command>();
	
	static
	{
		commands.put("help", new Help());
		commands.put("online", new Online());
		commands.put("say", new Say());
		commands.put("stop", new Stop());
		commands.put("time", new Time());
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
