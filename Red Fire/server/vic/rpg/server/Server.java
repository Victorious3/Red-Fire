package vic.rpg.server;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import vic.rpg.level.Level;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.registry.GameRegistry;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.server.gui.ServerGui;
import vic.rpg.server.io.Connection;
import vic.rpg.server.io.Listener;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet1ConnectionRefused;
import vic.rpg.server.packet.Packet6World;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.utils.Utils;

public class Server extends Thread 
{	
	public static Server server;
	
	public ServerSocket serverSocket;	
	public Listener listener;	
	public BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	public InputHandler inputHandler;
	public ServerLoop serverLoop;
	
	public static int MAX_CONNECTIONS = 10;	
	public static int actConnections = 0;
	public static HashMap<String, Connection> connections = new LinkedHashMap<String, Connection>();
	public static boolean isSinglePlayer = false;
	
	public static void main(final String[] args)
	{			
		List<String> argList = Arrays.asList(args);
		if(!GraphicsEnvironment.isHeadless())
		{		
			if(!argList.contains("-nogui") && !argList.contains("-splayer"))
			{
				ServerGui.setup();
			}
				
			if(argList.contains("-splayer")) isSinglePlayer = true; 		
	    }
		if(argList.indexOf("-file") != -1)
		{
			String file = argList.get(argList.indexOf("-file") + 1);			
			if(file == null)
			{
				System.err.println("Parameter -file <Path> not given!");
				return;
			}
			File f = new File(file);
			if(!f.exists())
			{
				System.err.println("File " + file + "doesn't exist!");
				return;
			}
			try
			{
				ServerLoop.level = Level.readFromFile(f);
				ServerLoop.file = f;
			}
			catch(Exception e)
			{
				System.err.println("File " + file + "in not valid!");
				return;
			}
		}
		
		final int port;
		boolean succsess = true;
		
		if(argList.get(0) != null)
		{		
			try
			{
				Integer.parseInt(argList.get(0));
			} catch(NumberFormatException e) {
				succsess = false;
			}
		}
		else
		{
			succsess = false;
		}
		
		if(succsess)
		{
			port = Integer.parseInt(argList.get(0));
		}
		else
		{
			port = 29598;
		}
		
		Thread thr = new Thread()
		{		
			@Override
			public void run() 
			{
				try {
					System.setProperty("file.encoding", "UTF-8");
					
					System.out.println("Starting -~/RedFire\\~- Server on Port " + port);
					System.out.println("___________________________________________________");
							
					server = new Server();
					server.serverSocket = new ServerSocket(port);
					server.listener = new Listener(new Server());
					server.inputHandler = new InputHandler();
					server.serverLoop = new ServerLoop();
					if(ServerLoop.level == null)
					{
						ServerLoop.level = new Level(100, 100, "New Level");
						ServerLoop.level.populate();
					}
					
					System.out.println("Starting Thread: Server");
					server.listener.start();
					System.out.println("Starting Thread: Listener");
					server.start();
					
					if(!isSinglePlayer)
					{
						System.out.println("Starting Thread: InputHandler");
						server.inputHandler.start();
					}
					
					System.out.println("Starting Thread: GameLoop");
					server.serverLoop.start();
				}
				catch(BindException e) {
					System.err.println("Server port is already in use! Please choose an other one.");
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}				
		};
		thr.setName("Server MainThread");
		thr.start();
	}
	
	public Server()
	{
		this.setName("Server");
	}
	
	public void addConnection(Connection con, String player, String version) 
	{	    
		if(player.equals("server"))
		{
			con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("HAHAHAHAHAHA"));
			System.out.println("Disconnecting Player " + player + " Reason: Tried to be funny");
			con = null;
		}
		else if(!version.equals(GameRegistry.VERSION))
		{
			con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("Wrong Version! Your Version: " + version + "; Server Version: " + GameRegistry.VERSION));
			System.out.println("Disconnecting Player " + player + " Reason: Wrong Version (" + version + ")");
			con = null;
		}
		else if (actConnections < MAX_CONNECTIONS) 
	    {
	    	if(connections.get(player) == null)
	    	{
				connections.put(player, con);
		    	actConnections++;
		    	
		    	EntityPlayer playerEntity = (EntityPlayer) LevelRegistry.ENTITY_LIVING_PLAYER.clone();
		    	ServerLoop.level.addPlayer(playerEntity, player, 550, 550);
		    	
		    	con.packetHandler.addPacketToSendingQueue(new Packet6World(ServerLoop.level));
		    	broadcast(new Packet7Entity(playerEntity, Packet7Entity.MODE_CREATE));
		    	
		    	System.out.println("Player " + player + " connected to the Server.");
	    	}
	    	else System.out.println("Disconnecting Player " + player + " Reason: Multiple Login");
	    }
	    else 
	    {      
	    	con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("Max. amount of connections is reached"));
	    	System.out.println("Disconnecting Player " + player + " Reason: Max. amount of connections is reached (" + MAX_CONNECTIONS + ")");
	    	con = null;
	    }
	}
	
	public boolean isRunning = true;
	
	@Override
	public void run() 
	{
	    while (isRunning) 
	    {	    	
	    	try {
				Server.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }	    
	}
	
	public synchronized void stopServer()
	{
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(Connection c : connections.values())
		{
			c.finalize();
		}
		this.isRunning = false;
		if(ServerLoop.file != null)
		{
			ServerLoop.level.writeToFile(ServerLoop.file);
		}
		else
		{
			ServerLoop.level.writeToFile(Utils.getOrCreateFile(Utils.getAppdata() + "/saves/" + ServerLoop.level.name + ".lvl"));
		}
		
		if(ServerGui.frame != null)
		{
			ServerGui.frame.setEnabled(false);
			ServerGui.frame.dispose();
		}	
	}

	public synchronized void delConnection(Connection c, Exception e) 
	{		
	    try {
	    	if(!connections.containsValue(c)) return;
	    	actConnections--;	    	
	    	c.connected = false;
	    	System.out.println("Disconnecting player " + c.player + " Reason: " + e.getMessage());
	    	broadcast(new Packet7Entity(ServerLoop.level.playerList.get(c.player), Packet7Entity.MODE_DELETE), c.player);
	    	connections.remove(c.player);
	    	c.socket.close(); 
	    	ServerLoop.level.playerList.remove(c.player);

		} catch (IOException e2) {
			e.printStackTrace();
		} catch (Exception e2){}	    
	}
	
	public void broadcast(Packet p, String... withoutPlayer) 
	{
		for(Connection con : connections.values()) 
	    { 		
			try
			{
				if(!Arrays.asList(withoutPlayer).contains(con.player)) con.packetHandler.addPacketToSendingQueue(p);
			} catch (Exception e) {
				e.printStackTrace();
			}  		
	    }		
	}
}
