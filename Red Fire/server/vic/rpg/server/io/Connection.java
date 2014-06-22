package vic.rpg.server.io;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.server.command.CommandSender;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.server.packet.PacketHandlerMP;
import vic.rpg.server.permission.Permission;

public class Connection extends BotConnection implements CommandSender, Runnable
{   	
	public Socket socket;
    public DataInputStream in;
    public DataOutputStream out;    
    public long time;       
    public boolean connected = true;
    public PacketHandlerMP packetHandler;
    
    public int STATE = GameState.RUNNING;
    
    public String username;
	public InetAddress ip;
	
	public Permission permission;
	public String prefix;
	public String suffix;
    
	public Thread conThread;
	
    public Connection(Socket socket, String username) 
    {
    	super(username);
    	this.socket = socket;
    	
    	try {      
    		in = new DataInputStream(socket.getInputStream());
    		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    		time = new Date().getTime();
    	}
    	catch (IOException e) {
    		connected = false;
    	}
    }
    
    public Connection()
    {
    	super("NO_USERNAME");
    }

	@Override
	public void run() 
	{
		String exc = "";
		while(connected)
		{
			if(isTimeout())
			{
				exc = "Connection Timeout";
				connected = false;
			}
			readData();
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Server.server.delConnection(this, exc);
	}
	
	public synchronized void start() 
	{
		conThread = new Thread(this);
		conThread.setName("Server Connection for player " + username);
		packetHandler = new PacketHandlerMP(this);
		packetHandler.start();
		conThread.setDaemon(true);
		conThread.start();
	}

	public boolean isTimeout() 
	{	    
	    if(new Date().getTime() > (time + 10000)) 
	    {	    	    	
	    	return true;
	    }
	    return false;
	}

	private boolean available()
    {
    	try {
			return in.available() > 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
    }
	
	public void readData() 
	{		
		try {
	    	if (available())
	    	{  			
				time = new Date().getTime();
				
				int id = in.readByte();				
				Packet p = Packet.getPacket(id);
				p.readData(in);
    			packetHandler.addPacketToQueue(p);	    		
	    	}
	    }
	    catch (IOException e)
	    {
	    	Server.server.delConnection(this, e.getMessage());
	    }
	}
  
	@Override
    public void finalize() 
    {
    	try {
    		connected = false;
    		in.close();
    		out.close();
    		socket.close();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    }

	@Override
	public void print(String string) 
	{
		packetHandler.addPacketToSendingQueue(new Packet20Chat(string, "SERVER"));
	}
	
	@Override
	public void error(String string) 
	{
		print("&4" + string);
	} 

	@Override
	public Permission getPermission() 
	{
		return permission;
	}
}