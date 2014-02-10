package vic.rpg.server;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import vic.rpg.ClassFinder;
import vic.rpg.Init;
import vic.rpg.PostInit;
import vic.rpg.level.Level;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.registry.GameRegistry;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.server.command.CommandSender;
import vic.rpg.server.gui.ServerGui;
import vic.rpg.server.io.Connection;
import vic.rpg.server.io.Listener;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet1ConnectionRefused;
import vic.rpg.server.packet.Packet6World;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class Server extends Thread implements CommandSender
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

	public static int STATE = GameState.LOADING;
	
	public static boolean nogui = false;
	
	public static void main(final String[] args)
	{			
		List<String> argList = Arrays.asList(args);
		if(!GraphicsEnvironment.isHeadless())
		{		
			if(argList.contains("-nogui") || argList.contains("-splayer"))
			{
				nogui = true;
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
				ServerLoop.file = f;
			}
			catch(Exception e)
			{
				System.err.println("File " + file + "is not valid!");
				return;
			}
		}
		
		final int port;
		boolean succsess = true;
		
		if(argList.size() == 0)
		{
			succsess = false;
		}
		else if(argList.get(0) != null)
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
					
					server = new Server();			
					if(!nogui) ServerGui.setup();
					
					System.out.println("Starting -~/RedFire\\~- Server on Port " + port);
					System.out.println("Performing init operations...");
					server.init();
					System.out.println("done!");
					
					server.serverSocket = new ServerSocket(port);
					server.listener = new Listener(new Server());
					server.inputHandler = new InputHandler();
					server.serverLoop = new ServerLoop();
					
					System.out.println("Loading level...");
					if(ServerLoop.file != null) ServerLoop.level = Level.readFromFile(ServerLoop.file);
					
					if(ServerLoop.level == null)
					{
						ServerLoop.level = new Level(100, 100, "New Level");
						ServerLoop.level.populate();
					}
					System.out.println("done!");
					
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
					STATE = GameState.RUNNING;
					System.out.println("done!");
				}
				catch(BindException e) {
					System.err.println("Server port is already in use! Please choose an other one.");
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}				
		};
		thr.setName("Server StartThread");
		thr.start();
	}
	
	private void init()
	{
		List<Class<?>> cls;
		try {
			cls = ClassFinder.getClasses("vic.rpg", (String)null);
			for(Class<?> c : cls)
			{
				for(Method m : c.getDeclaredMethods())
				{
					if(m.getAnnotation(Init.class) != null && Modifier.isStatic(m.getModifiers()))
					{
						Init init = m.getAnnotation(Init.class);
						if(init.side() == Side.SERVER || (init.side() == Side.BOTH && !isSinglePlayer))
						{
							m.setAccessible(true);
							try {
								m.invoke(null, (Object[])null);
								System.out.println("init: " + c.getName() + "." + m.getName() + "()");
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			for(Class<?> c : cls)
			{
				for(Method m : c.getDeclaredMethods())
				{
					if(m.getAnnotation(PostInit.class) != null && Modifier.isStatic(m.getModifiers()))
					{
						PostInit init = m.getAnnotation(PostInit.class);
						if(init.side() == Side.SERVER || (init.side() == Side.BOTH && !isSinglePlayer))
						{
							m.setAccessible(true);
							try {
								m.invoke(null, (Object[])null);
								System.out.println("postinit: " + c.getName() + "." + m.getName() + "()");
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
		    	
		    	if(ServerLoop.level.offlinePlayersMap.containsKey(player))
		    	{
		    		playerEntity = ServerLoop.level.offlinePlayersMap.remove(player);	    		
		    		ServerLoop.level.onlinePlayersMap.put(playerEntity.username, playerEntity.UUID);
		    		ServerLoop.level.entityMap.put(playerEntity.UUID, playerEntity);
		    	}	    	
		    	else ServerLoop.level.createPlayer(playerEntity, player, ServerLoop.level.spawnX, ServerLoop.level.spawnY);
		    		
		    	con.packetHandler.addPacketToSendingQueue(new Packet6World(ServerLoop.level));
		    	broadcast(new Packet7Entity(playerEntity, Packet7Entity.MODE_CREATE));
		    	con.STATE = GameState.LOADING;
		    	
		    	if(!nogui) ServerGui.updatePlayers();
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
	    cleanup();
	}
	
	private void cleanup()
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
		
		System.out.println("Waiting on active connections...");
		
		while(true)
		{
			boolean brk = true;
			for(Connection c : connections.values())
			{
				if(c.isAlive()) brk = false;
			}
			if(brk) break;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("done!");
		
		System.out.println("Saving to file...");
		serverLoop.stop();
		
		if(ServerLoop.file != null)
		{
			ServerLoop.level.writeToFile(ServerLoop.file);
		}
		else
		{
			ServerLoop.level.writeToFile(Utils.getOrCreateFile(Utils.getAppdata() + "/saves/" + ServerLoop.level.name + ".lvl"));
		}
		
		System.out.println("done!");
		
		STATE = GameState.QUIT;
		
		//FIXME without this line, the awt-enventqueue gets stuck at Usafe.park.
		if(!isSinglePlayer) System.exit(0);
	}
	
	public synchronized void stopServer()
	{
		this.isRunning = false;
	}

	public synchronized void delConnection(Connection c, String reason) 
	{		
	    try {
	    	if(!connections.containsValue(c)) return;
	    	actConnections--;	    	
	    	c.connected = false;
	    	if(reason.length() > 0) System.out.println("Disconnecting player " + c.username + " Reason: " + reason);
	    	broadcast(new Packet7Entity(ServerLoop.level.entityMap.get(ServerLoop.level.onlinePlayersMap.get(c.username)), Packet7Entity.MODE_DELETE), c.username);
	    	connections.remove(c.username);
	    	c.socket.close(); 
	    	ServerLoop.level.offlinePlayersMap.put(c.username, (EntityPlayer)ServerLoop.level.entityMap.remove(ServerLoop.level.onlinePlayersMap.remove(c.username)));
	    	if(!nogui) ServerGui.updatePlayers();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2){}	    
	}
	
	public void broadcast(Packet p, String... withoutPlayer) 
	{
		for(Connection con : connections.values()) 
	    { 		
			try
			{
				if(!Arrays.asList(withoutPlayer).contains(con.username)) con.packetHandler.addPacketToSendingQueue(p);
			} catch (Exception e) {
				e.printStackTrace();
			}  		
	    }		
	}

	@Override
	public void print(String string) 
	{
		System.out.println(string);
	}
}
