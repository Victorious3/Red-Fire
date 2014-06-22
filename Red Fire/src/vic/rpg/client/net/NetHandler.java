package vic.rpg.client.net;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import vic.rpg.Game;
import vic.rpg.event.EventBus;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiError;
import vic.rpg.gui.GuiMain;
import vic.rpg.registry.GameRegistry;
import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.server.command.CommandStop;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet0StateUpdate;

/**
 * NetHandler does handle all Client <-> Server communication for the Client.
 * @author Victorious3
 */
public class NetHandler extends Thread 
{
	public Socket socket;
	public DataInputStream in;
	public DataOutputStream out;
	public boolean connected;
	public boolean IS_SINGLEPLAYER = false;
	public String lastError = "";
	
	/**
	 * Indicates the current {@link GameState} of the Client.
	 */
	public int STATE = GameState.RUNNING; 
	
	/**
	 * NetHandler constructor. Sets the thread name to "Network Thread".
	 */
	public NetHandler()
	{
		this.setName("Network Thread");
	}
	
	/**
	 * Tries to connect to a given Server. On success it returns {@code true}.
	 * The latest error is stored in {@link #lastError}.
	 * @param adress
	 * @param port
	 * @param username
	 * @return Boolean
	 */
	public boolean connect(String adress, int port, String username)
	{		
		Game.USERNAME = username;
		
		try {								
			EventBus.clearClient();
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
	
	/**
	 * Network Thread. It listens on the Client socket and creates a new {@link Packet} by the use of {@link Packet#readData(DataInputStream)} for processing
	 * in {@link PacketHandlerSP} if a server sent a new {@link Packet}. It also sends an {@link Packet0StateUpdate update Packet}
	 * every 10 seconds to inform the Server of the fact that the Client is still up and running.
	 * <br><br>
	 * The latest error is stored in {@link #lastError}.
	 */
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
	
	/**
	 * Used to stop the Network Thread and close the active Socket on disconnect. 
	 * Also stops the server by sending the {@link CommandStop stop command} if the Game is running on SMP.
	 * <br>
	 * If an error had occurred previously, {@link GuiError} is displayed.
	 */
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
			Game.map = null;
			
			if(lastError.length() > 0)
			{
				Gui.setGui(new GuiError());
			}
			else Gui.setGui(new GuiMain());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reference to {@link InputStream#available()}.
	 */
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
