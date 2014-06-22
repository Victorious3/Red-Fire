package vic.rpg.server.command;

import java.util.ArrayList;
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
		commands.put("permission", new CommandPermission());
		commands.put("tp", new CommandTeleport());
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
	
	//TODO No support for different lengths.
	protected List<Object> requireArguments(List<String> args, Class<?>... classes) throws CommandException
	{
		List<Object> list = new ArrayList<Object>();
		if(args.size() != classes.length) throw new CommandException(this, classes.length, args.size());
		
		int i = 0;
		for(String s : args)
		{
			if(classes[i] != null)
			{
				try {
					if(classes[i] == Integer.class) list.add(Integer.parseInt(s));
					else if(classes[i] == Float.class) list.add(Float.parseFloat(s));
					else if(classes[i] == Double.class) list.add(Double.parseDouble(s));
					else if(classes[i] == Byte.class) list.add(Byte.parseByte(s));
					else if(classes[i] == Boolean.class) list.add(Boolean.parseBoolean(s));
					else if(classes[i] == String.class) list.add(s);
					else throw new IllegalArgumentException("Wrong parameter type. Allowed ones are Integer, Float, Double, Byte, Boolean & String");
				} catch (NumberFormatException e) {
					throw new CommandException(this, classes, i);
				}
			}
			else list.add(s);				
			i++;
		}
		return list;
	}
	
	public Command(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void help(CommandSender commandSender)
	{
		commandSender.error("/" + name + ":");
		commandSender.error("Usage: " + getUsage());
	}
	
	public void noPermission(CommandSender commandSender)
	{
		commandSender.error("You have no permission!");
	}
	
	public abstract void cast(List<String> args, CommandSender commandSender) throws CommandException;
	
	public abstract String getUsage();
}
