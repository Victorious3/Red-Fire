package vic.rpg.server.command;

import java.util.List;

import vic.rpg.item.Item;
import vic.rpg.item.ItemStack;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.server.ServerLoop;

public class CommandGive extends Command
{
	public CommandGive() 
	{
		super("give");
	}

	@Override
	public void cast(List<String> args, CommandSender commandSender) 
	{
		if(args.size() > 1)
		{
			int amount = 1;
			int id = 0;
			Item item;
			
			if(args.size() > 1)
			{
				try {
				amount = Integer.parseInt(args.get(2));
				} catch (NumberFormatException e) {
					commandSender.print("&4Amount has to be an int!");
					return;
				}
			}
			
			try {
				id = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e) {
				commandSender.print("&4Id has to be an int!");
				return;
			}
			
			if(LevelRegistry.itemRegistry.containsKey(id))
			{
				item = LevelRegistry.itemRegistry.get(id);
				
				if(ServerLoop.level.onlinePlayersMap.containsKey(args.get(0)))
				{
					EntityPlayer player = ServerLoop.level.getPlayer(args.get(0));
					player.inventory.addToInventory(new ItemStack(item, amount));
					player.inventory.updateInventory();
				}
				else
				{
					commandSender.print("&4No player named \"" + args.get(0) + "\" online!");
					return;
				}
			}
			else 
			{
				commandSender.print("&4No item extists with the id " + id + "!");
				return;
			}
		}
		else commandSender.print(getUsage()); 
	}

	@Override
	public String getUsage() 
	{
		return "/give <username> <id> [<amount>]";
	}
}
