package vic.rpg.server.io;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.PacketHandlerMP;

public class Connection extends Thread
{   
	public Socket socket;
    public DataInputStream in;
    public DataOutputStream out;    
    public long time;       
    public boolean connected = true;
    public PacketHandlerMP packetHandler = new PacketHandlerMP(this);
    
    public String player;
    
    public Connection(Socket socket) 
    {
    	this.socket = socket;
    	this.player = "NO_PLAYER!";
    	
    	try {      
    		in = new DataInputStream(socket.getInputStream());
    		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    		time = new Date().getTime();
    		packetHandler.start();
    	}
    	catch (IOException e) {
    		connected = false;
    	}
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
		Server.server.delConnection(this, new Exception(exc));
	}
	
	@Override
	public synchronized void start() 
	{
		this.setName("Connection for player " + player);
		this.setDaemon(true);
		super.start();
	}

	public boolean isTimeout() 
	{	    
	    if (new Date().getTime() > (time + 10000)) 
	    {	    	    	
	    	return true;
	    }
	    return false;
	}

	public boolean available()
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
	    catch (Exception e)
	    {
	    	Server.server.delConnection(this, e);
	    }
	}
  
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
}