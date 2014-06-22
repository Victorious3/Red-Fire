package vic.rpg.server.command;

import java.util.List;

import vic.rpg.server.ServerLoop;

public class CommandTeleport extends Command
{
	public CommandTeleport() 
	{
		super("tp");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(args.size() > 3)
		{
			try {
				String playerName = args.get(0);
				int dimension = Integer.parseInt(args.get(1));
				int xCoord = Integer.parseInt(args.get(2));
				int yCoord = Integer.parseInt(args.get(3));
				
				if(!ServerLoop.world.isPlayerOnline(playerName))
				{
					commandSender.error("No player named " + playerName + " online!");
					return;
				}
				if(ServerLoop.world.getMap(dimension) == null)
				{
					commandSender.error("There is no map with the id of " + dimension);
					return;
				}
				
				ServerLoop.world.changeMaps(dimension, playerName, xCoord, yCoord);
				
			} catch (NumberFormatException e) {
				commandSender.error("<mapid>, <xCoord> and <yCoord> have to be numeric!");
			}
		}
		else help(commandSender);
	}

	@Override
	public String getUsage() 
	{
		return "/tp <username> <mapid> <xCoord> <yCoord>";
	}
}
