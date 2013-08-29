package vic.rpg.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import vic.rpg.server.command.Command;

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
			String[] args = s.split(" ");
			String command = args[0];
			command = command.replace("/", "");
			LinkedList<String> args2 = new LinkedList<String>(Arrays.asList(args));
			args2.remove(0);
			handleCommand(command, args2);
		}
	}
	
	public void handleCommand(String command, List<String> args)
	{
		Command c = Command.commands.get(command);
		if(c == null)
		{
			System.err.println("No command named \"" + command + "\"!");
			Command.commands.get("help").cast(new ArrayList<String>());
		}
		else c.cast(args);
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
