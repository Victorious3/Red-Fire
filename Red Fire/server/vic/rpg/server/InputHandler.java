package vic.rpg.server;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import vic.rpg.server.io.Connection;
import vic.rpg.server.packet.Packet20Chat;

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
		if(command.equalsIgnoreCase("stop"))
		{			
			System.out.println("________________________________________________");
			System.err.println("Stopping Server ...");
			System.out.println("done!");
			Server.server.stopServer();			
		}
		else if(command.equalsIgnoreCase("online"))
		{
			String out = "";
			out += "Online Players: " + Server.actConnections + " (";
			
			for(Connection con : Server.connections.values())
			{
				out += con.player + ", ";
			}
			
			out += ")";
			System.out.println(out);
		}
		else if(command.equalsIgnoreCase("say"))
		{
			if(args.get(0) != null)
			{
				Server.server.broadcast(new Packet20Chat(args.get(0), "server"));
				System.out.println("{SERVER}: " + args.get(0));
			}
		}
		else if(command.equalsIgnoreCase("time"))
		{
			if(args.size() > 0 && args.get(0).equals("set"))
			{
				if(args.size() > 1 && args.get(1) != null) 
				{
					String time = args.get(1);
					try {
						int t2 = Integer.parseInt(time);
						ServerLoop.level.time = t2;
						System.out.println("Time set to " + t2);
					} catch (NumberFormatException e) {
						System.err.println("Time has to be numeric!");
					}				
				}
				else
				{
					System.err.println("Usage: /time set <time>");
				}
			}
			else System.out.println("The current game time is " + ServerLoop.level.time);
		}
		else
		{
			System.err.println("Unknown Command: " + command);
		}
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
