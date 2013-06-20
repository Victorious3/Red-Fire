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
			List<String> args2 = new LinkedList<String>(Arrays.asList(args));
			args2.remove(0);
			args = args2.toArray(args);
			handleCommand(command, args);
		}
	}
	
	public void handleCommand(String command, String[] args)
	{
		if(command.equalsIgnoreCase("stop"))
		{			
			System.out.println("___________________________________________________");
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
			if(args[0] != null)
			{
				Server.server.broadcast(new Packet20Chat(args[0], "server"));
				System.out.println("{SERVER}: " + args[0]);
			}
		}
		else if(command.equalsIgnoreCase("time"))
		{
			System.out.println("The current gametime is " + ServerLoop.level.time);
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
			} catch(Exception e){}
		}  	
	}
}
