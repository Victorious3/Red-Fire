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
import java.util.Map.Entry;

import vic.rpg.ClassFinder;
import vic.rpg.Init;
import vic.rpg.PostInit;
import vic.rpg.event.EventBus;
import vic.rpg.registry.GameRegistry;
import vic.rpg.server.command.CommandSender;
import vic.rpg.server.gui.ServerGui;
import vic.rpg.server.io.BotConnection;
import vic.rpg.server.io.Connection;
import vic.rpg.server.io.Listener;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet1ConnectionRefused;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.server.packet.Packet6World;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.server.permission.Permission;
import vic.rpg.server.permission.PermissionHelper;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Logger.LogLevel;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.World;
import vic.rpg.world.entity.living.EntityPlayer;

public class Server extends Thread implements CommandSender
{	
	public static Server server;
	
	public ServerSocket serverSocket;	
	public Listener listener;	
	public BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	public InputHandler inputHandler;
	public ServerLoop serverLoop;
	public Permission permission = Permission.createRoot(true);
	
	public static int MAX_CONNECTIONS = 10;	
	public static int actConnections = 0;
	public static HashMap<String, BotConnection> connections = new LinkedHashMap<String, BotConnection>();
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
				Logger.log(LogLevel.SEVERE, "Parameter -file <Path> not given!");
				return;
			}
			File f = new File(file);
			if(!f.exists())
			{
				Logger.log(LogLevel.SEVERE, "File " + file + " doesn't exist!");
				return;
			}
			try
			{
				ServerLoop.file = f;
			}
			catch(Exception e)
			{
				Logger.log(LogLevel.SEVERE, "File " + file + " is not valid!");
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
					
					Logger.log("Starting -~/RedFire\\~- Server on Port " + port);
					Logger.log("Performing init operations...");
					server.init();
					Logger.log("done!");
					
					Logger.log("Loading Permissions...");
					PermissionHelper.loadPermissions();
					
					server.serverSocket = new ServerSocket(port);
					server.listener = new Listener(new Server());
					server.inputHandler = new InputHandler();
					server.serverLoop = new ServerLoop();
					
					EventBus.clearServer();
					
					Logger.log("Loading map...");
					if(ServerLoop.file != null) 
					{
						ServerLoop.world = new World();
						ServerLoop.world.readFromFile(ServerLoop.file);
					}
					
					if(ServerLoop.world == null)
					{
						Logger.log(LogLevel.SEVERE, "Server start aborted! No File selected");
						System.exit(-1);
					}
					Logger.log("done!");
					
					Logger.log("Starting Thread: Server");
					server.listener.start();
					Logger.log("Starting Thread: Listener");
					server.start();
					
					if(!isSinglePlayer)
					{
						Logger.log("Starting Thread: InputHandler");
						server.inputHandler.start();
					}
					
					Logger.log("Starting Thread: GameLoop");
					server.serverLoop.start();
					STATE = GameState.RUNNING;
					Logger.log("done!");
				}
				catch(BindException e) {
					Logger.log(LogLevel.SEVERE, "Server port is already in use! Please choose an other one.");
				}
				catch(Exception e) {
					e.printStackTrace();
					try {
						server.serverSocket.close();
					} catch (IOException e1) {
					}
				}
			}				
		};
		thr.setName("Server Startup Thread");
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
								Logger.log("init: " + c.getName() + "." + m.getName() + "()");
								m.invoke(null, (Object[])null);
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
								Logger.log("postinit: " + c.getName() + "." + m.getName() + "()");
								m.invoke(null, (Object[])null);
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
		if(player.equalsIgnoreCase("server"))
		{
			con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("HAHAHAHAHAHA"));
			Logger.log(LogLevel.WARNING, "Disconnecting Player " + player + " Reason: Tried to be funny");
		}
		else if(player.contains(" "))
		{
			con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("Sorry but your name is invalid. Don't take it personally."));
			Logger.log(LogLevel.WARNING, "Disconnecting Player " + player + " Reason: Bad bad name.");
		}
		else if(!version.equals(GameRegistry.VERSION))
		{
			con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("Wrong Version! Your Version: " + version + ", Server Version: " + GameRegistry.VERSION));
			Logger.log(LogLevel.WARNING, "Disconnecting Player " + player + " Reason: Wrong Version (" + version + ")");
		}
		else if (actConnections < MAX_CONNECTIONS) 
	    {
	    	if(connections.get(player) == null)
	    	{    	
	    		connections.put(player, con);
		    	actConnections++;
	    		
		    	EntityPlayer playerEntity = ServerLoop.world.createPlayer(player);
		    	
		    	con.packetHandler.addPacketToSendingQueue(new Packet6World(ServerLoop.world.getMap(playerEntity.dimension)));
		    	broadcastLocally(playerEntity.dimension, new Packet7Entity(playerEntity, Packet7Entity.MODE_CREATE));  	
		    	con.STATE = GameState.LOADING;
		    	
		    	if(!nogui) ServerGui.updatePlayers();
		    	Logger.log("Player " + player + " connected to the Server.");
		    	broadcast(new Packet20Chat("Player " + player + " connected to the Server.", "SERVER"), player);
		    	
	    	}
	    	else Logger.log(LogLevel.WARNING, "Disconnecting Player " + player + " Reason: Multiple Login");
	    }
	    else 
	    {      
	    	con.packetHandler.addPacketToSendingQueue(new Packet1ConnectionRefused("Max. amount of connections is reached"));
	    	Logger.log(LogLevel.WARNING, "Disconnecting Player " + player + " Reason: Max. amount of connections is reached (" + MAX_CONNECTIONS + ")");
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
		
		for(Connection c : getConnections().values())
		{
			c.finalize();
		}	
		
		Logger.log("Waiting on active connections...");
		
		while(true)
		{
			boolean brk = true;
			for(Connection c : getConnections().values())
			{
				if(c.conThread.isAlive()) brk = false;
			}
			if(brk) break;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
		Logger.log("done!");
		
		Logger.log("Saving permissions...");
		PermissionHelper.savePermissions();
		
		Logger.log("Saving to file...");
		serverLoop.stop();
		
		try {
			ServerLoop.world.writeToFile();
			Logger.log("done!");
		} catch (IOException e) {
			Logger.log(LogLevel.SEVERE, "Error while saving to file!");
			e.printStackTrace();
		}
		
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
	    	if(reason.length() > 0) 
	    	{
	    		Logger.log(LogLevel.WARNING, "Disconnecting player " + c.username + " Reason: " + reason);
	    		broadcast(new Packet20Chat("Disconnecting player " + c.username + ".", "SERVER"));
	    	}
	    	broadcastLocally(ServerLoop.world.getDimension(c.username), new Packet7Entity(ServerLoop.world.removePlayer(c.username), Packet7Entity.MODE_DELETE), c.username);
	    	connections.remove(c.username);
	    	c.socket.close(); 
	    	if(!nogui) ServerGui.updatePlayers();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (Exception e2){}	    
	}
	
	public void broadcastLocally(int map, Packet p, String... withoutPlayer)
	{
		for(Connection con : getConnections().values()) 
	    { 		
			try {
				if(!Arrays.asList(withoutPlayer).contains(con.username)) 
				{
					if(ServerLoop.world.getPlayer(con.username).dimension == map) con.packetHandler.addPacketToSendingQueue(p);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}  		
	    }	
	}
	
	public void broadcast(Packet p, String... withoutPlayer) 
	{
		for(Connection con : getConnections().values()) 
	    { 		
			try {
				if(!Arrays.asList(withoutPlayer).contains(con.username)) con.packetHandler.addPacketToSendingQueue(p);
			} catch (Exception e) {
				e.printStackTrace();
			}  		
	    }		
	}

	@Override
	public void print(String string) 
	{
		Logger.log(string);
	}
	
	@Override
	public void error(String string) 
	{
		Logger.log(LogLevel.SEVERE, string);
	}

	@Override
	public Permission getPermission() 
	{
		return permission;
	}
	
	/**
	 * This will filter any {@link BotConnection} from the {@link #connections}.
	 * @return
	 */
	public static HashMap<String, Connection> getConnections()
	{
		HashMap<String, Connection> temp = new HashMap<String, Connection>();
		for(Entry<String, BotConnection> set : connections.entrySet())
		{
			if(set.getValue() instanceof Connection) temp.put(set.getKey(), (Connection)set.getValue());
		}
		return temp;
	}
	
	public static boolean isBot(BotConnection bot)
	{
		return !(bot instanceof Connection);
	}
	
	public static Connection getConnection(BotConnection con)
	{
		return (Connection)con;
	}
}
