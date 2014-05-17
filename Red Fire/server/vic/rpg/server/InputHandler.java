package vic.rpg.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vic.rpg.server.command.Command;
import vic.rpg.server.command.CommandSender;

public class InputHandler extends Thread 
{
	public InputHandler()
	{
		this.setName("Server InputHandler");
		this.setDaemon(true);
	}
	
	public void handleInput(String s)
	{
		if(s.startsWith("/"))
		{
			List<String> list = new ArrayList<String>();
			Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(s);
			
			while(m.find())
			{
				list.add(m.group(1).replace("\"",""));
			}
			
			String command = list.remove(0);
			command = command.replace("/", "");
			handleCommand(command, list, Server.server);
		}
	}
	
	public void handleCommand(String command, List<String> args, CommandSender commandSender)
	{
		Command c = Command.commands.get(command);
		if(c == null)
		{
			System.err.println("No command named \"" + command + "\"!");
			Command.commands.get("help").cast(new ArrayList<String>(), commandSender);
		}
		else c.cast(args, commandSender);
	}
		
	@Override
	public void run()
	{	
		while(Server.server.isRunning)
		{
			try {
				String s = Server.server.console.readLine();
				handleInput(s);
				Thread.sleep(1);
			} catch(Exception e){
				e.printStackTrace();
			}
		}  	
	}
}
