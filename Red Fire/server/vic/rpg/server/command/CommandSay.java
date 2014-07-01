package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.utils.Logger;

public class CommandSay extends Command
{
	public CommandSay() 
	{
		super("say");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(!commandSender.getPermission().hasPermission("operator.say"))
		{
			noPermission(commandSender);
			return;
		}
		
		if(args.size() > 0)
		{		
			String s = args.get(0);
			args.remove(0);
			for(String s2 : args)
			{
				s += " " + s2;
			}
			Logger.log("{SERVER}: " + s);
			Server.server.broadcast(new Packet20Chat(s, "SERVER"));
		}
		else help(null);
	}

	@Override
	public String getUsage() 
	{
		return "/say <message [...]>";
	}
}
