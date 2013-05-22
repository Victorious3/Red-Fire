package vic.rpg.server;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
		if (!GraphicsEnvironment.isHeadless())
		{
			if(args.length > 1)
			{
				if(args[1] != "nogui" && args[1] != "splayer")
				{
					ServerGui.setup();
				}
				if(args[1] == "splayer") isSinglePlayer = true; 
			}
			else ServerGui.setup();
	    } 
				
		Thread thr = new Thread()
		{
			@Override
			public void run() 
			{
				try {
					System.setProperty("file.encoding", "UTF-8");
					
					System.out.println("Starting -~/RedFire\\~- Server on Port " + args[0]);
					System.out.println("___________________________________________________");
							
					server = new Server();
					server.serverSocket = new ServerSocket(Integer.parseInt(args[0]));
					server.listener = new Listener(new Server());
					server.inputHandler = new InputHandler();
					server.serverLoop = new ServerLoop();
					
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
