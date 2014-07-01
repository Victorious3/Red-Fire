package vic.rpg.server.io;

import java.net.Socket;
import java.net.SocketException;

import vic.rpg.server.Server;
import vic.rpg.server.permission.PermissionHelper;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Logger.LogLevel;

public class Listener extends Thread 
{
	Server server;
	  
    public Listener(Server server) 
    {
    	this.server = server;
    	this.setName("Server Listener");
    }
    
    @Override
    public void run() 
    {
    	Socket socket;
    	Connection con;
 	
		while(Server.server.isRunning) 
    	{
	        try {	  
	        	socket = Server.server.serverSocket.accept();        	
	        	
	        	con = new Connection(socket, "NO_USERNAME");
	        	
	        	if (con.connected)
	        	{	        			        		
		        	String player = "";
		        	String version = "";
		        	
		        	int trys = 0;		        	
		        	do {
		        		trys++;
		        		if(trys == 10)
			        	{
			        		Logger.log(LogLevel.WARNING, socket.getInetAddress() + " lost connection.");
			        		break;
			        	}
		        		Listener.sleep(10);
		        		
		        		player = con.in.readUTF();
		        		version = con.in.readUTF();
			        	
		        	} while(player.length() == 0 || version.length() == 0);
		        	
		        	if(player.length() != 0 || version.length() != 0)
		        	{
		        		con.username = player;
		        		con.permission = PermissionHelper.getPermissionForPlayer(player);
		        		con.prefix = PermissionHelper.getPrefix(player);
		        		con.suffix = PermissionHelper.getSuffix(player);
		        		con.ip = socket.getInetAddress();
		        		con.start();
			        	server.addConnection(con, player, version);
		        	}
		        	else
		        	{
		        		con.connected = false;
		        	}
	        	}        	
	        } catch (SocketException e) {
	        	
	        	if(!Server.server.serverSocket.isClosed())
	        	{
	        		e.printStackTrace();
	        		Server.server.stopServer();		
	        	}
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	Server.server.stopServer();	
	        }
    	}
    }
}
