package vic.rpg.client.net;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import vic.rpg.Game;
import vic.rpg.client.packet.PacketHandlerSP;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiError;
import vic.rpg.gui.GuiMain;
import vic.rpg.registry.GameRegistry;
import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet0StateUpdate;

public class NetHandler extends Thread 
{
	public Socket socket;
	public DataInputStream in;
	public DataOutputStream out;
	public boolean connected;
	public boolean IS_SINGLEPLAYER = false;
	public String lastError = "";
	
	public int STATE = GameState.RUNNING; 
	
	public NetHandler()
	{
		this.setName("Network Thread");
	}
	
	public boolean connect(String adress, int port, String username)
	{		
		Game.USERNAME = username;
		
		try {								
			connected = true;
			socket = new Socket(adress, port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			out.writeUTF(Game.USERNAME);
			out.writeUTF(GameRegistry.VERSION);
			out.flush();
			this.start();
			Game.packetHandler = new PacketHandlerSP();
			System.out.println("Succsessfully connected to: " + adress + ":" + port + " as player " + username);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			lastError = e.toString();
			this.close();
		}
		return false;
	}

	double unprocessedSeconds = 0;
	long previousTime = System.nanoTime();
	double secondsPerTick = 3;
	
	public void run() 
	{		
		while(connected)
		{
			try {										
				long currentTime = System.nanoTime();
				long passedTime = currentTime - previousTime;
				previousTime = currentTime;
				unprocessedSeconds += passedTime / 1000000000.0;
				
				while(unprocessedSeconds > secondsPerTick)
				{
					Game.packetHandler.addPacketToSendingQueue(new Packet0StateUpdate(STATE));					
					unprocessedSeconds -= secondsPerTick;
				}
	
				if(socket.isConnected() && in.available() > 1) 
				{		    			    			
					int id = in.readByte();
					Packet packet = Packet.getPacket(id);
					packet.readData(in);
					
	    			Game.packetHandler.addPacketToQueue(packet);	
				}
				Thread.sleep(1);
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    	lastError = e.toString();
		    	this.close();
		    }
		}		
	}
	
	public void close()
	{
		try {
			System.out.println("Destroying Network Thread...");
			this.connected = false;
			
			while(this.isAlive())
			{
				Thread.sleep(100);
			}
			
			if(this.socket != null) 
			{
				if(this.socket.isConnected())
				{
					STATE = GameState.QUIT;
					Game.packetHandler.sendPacket(new Packet0StateUpdate(STATE));				
				}
				this.socket.close();
			}
			
			System.out.println("done!");
			
			if(IS_SINGLEPLAYER)
			{
				Server.server.inputHandler.handleCommand("stop", null, Server.server);
			}
			
			Game.playerUUID = null;
			Game.level = null;
			
			if(lastError.length() > 0)
			{
				Gui.setGui(new GuiError());
			}
			else Gui.setGui(new GuiMain());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
}
