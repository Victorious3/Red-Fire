package vic.rpg.client.net;

import java.awt.Point;
import java.io.DataInputStream;
import java.util.ArrayList;

import vic.rpg.Game;
import vic.rpg.client.render.Screen;
import vic.rpg.event.EventBus;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiIngame;
import vic.rpg.gui.GuiPlayer;
import vic.rpg.server.GameState;
import vic.rpg.server.packet.Packet;
import vic.rpg.server.packet.Packet0StateUpdate;
import vic.rpg.server.packet.Packet10TimePacket;
import vic.rpg.server.packet.Packet12Event;
import vic.rpg.server.packet.Packet1ConnectionRefused;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.server.packet.Packet6World;
import vic.rpg.server.packet.Packet7Entity;
import vic.rpg.server.packet.Packet9EntityMoving;
import vic.rpg.utils.Logger;
import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.living.EntityLiving;
import vic.rpg.world.entity.living.EntityPlayer;

/**
 * The PacketHandlerSP is processing all incoming {@link Packet Packets} from the Server and sends every {@link Packet} on the queue.
 * @author Victorious3
 */
public class PacketHandlerSP extends Thread
{	
	private ArrayList<Packet> packetQueue = new ArrayList<Packet>();
	private ArrayList<Packet> sendingQueue = new ArrayList<Packet>();
	
	/**
	 * PacketHadlerSP constructor. Sets the thread name to "Client PacketHandler" and starts it.
	 */
	public PacketHandlerSP()
	{
		this.setName("Client PacketHandler");
		this.start();
	}
	
	/**
	 * Adds a {@link Packet} to the {@link #packetQueue} for later processing by {@link #run()}
	 * @param p - Packet to process.
	 */
	public void addPacketToQueue(Packet p)
	{
		if(Game.netHandler.STATE == GameState.RUNNING) packetQueue.add(p);
	}
	
	/**
	 * Adds a {@link Packet} to the {@link #sendingQueue} for later sending by {@link #run()}
	 * @param p - Packet to send.
	 */
	public void addPacketToSendingQueue(Packet p)
	{
		sendingQueue.add(p);	
	}
	
	/**
	 * Handles all the incoming packets based on their id.
	 * @param p - Packet to process.
	 */
	public void handlePacket(Packet p)
	{
		if(p == null) return;
		if(p.id == 1)
		{
			Logger.log(((Packet1ConnectionRefused)p).message);
			Game.netHandler.lastError = ((Packet1ConnectionRefused)p).message;
			Game.netHandler.close();
		}
		else if(p.id == 6)
		{
			Game.map = new Map(); 
			Game.map.readFromNBT(((Packet6World)p).getData());
			Game.netHandler.STATE = GameState.RUNNING;
			sendPacket(new Packet0StateUpdate(Game.netHandler.STATE));
		}
		else if(p.id == 7)
		{
			Packet7Entity p7entity = (Packet7Entity) p;
			int mode = p7entity.mode;
			
			switch(mode)
			{
			case Packet7Entity.MODE_UPDATE:
				p7entity.update(Game.map);
				break;
			case Packet7Entity.MODE_CREATE:	
				Entity[] entities = p7entity.create();
				for (Entity e : entities)
				{
					synchronized(Game.map.entityMap)
					{
						if(Game.map != null) Game.map.addEntity(e);
						if(e instanceof EntityPlayer)
						{
							if(((EntityPlayer)e).username.equals(Game.USERNAME))
							{
								Game.playerUUID = e.UUID;
								Game.getPlayer().setRotation(Game.getPlayer().rotation);
								Point p1 = Utils.convCartToIso(new Point(-e.xCoord, -e.yCoord));
								p1.x += (Game.WIDTH - e.getWidth()) / 2;
								p1.y += (Game.HEIGHT - e.getHeight()) / 2;
								p1 = Utils.convIsoToCart(p1);
								Screen.xOffset = p1.x;
								Screen.yOffset = p1.y;
								if(Gui.currentGui instanceof GuiPlayer)
								{
									Gui.currentGui.initGui();
								}
							}
						}	
					}
				}
				break;
			case Packet7Entity.MODE_DELETE:
				p7entity.remove(Game.map);
			}
		}
		else if(p.id == 9)
		{
			if(Game.map != null && Game.getPlayer() != null)
			{
				EntityLiving e = (EntityLiving) Game.map.entityMap.get(((Packet9EntityMoving)p).UUID);
				if(e.UUID != Game.getPlayer().UUID)
				{
					e.xCoord = ((Packet9EntityMoving)p).xCoord;
					e.yCoord = ((Packet9EntityMoving)p).yCoord;
					e.setRotation(((Packet9EntityMoving)p).rotation);
					e.setWalking(((Packet9EntityMoving)p).isWalking);
				}
			}
		}
		else if(p.id == 10)
		{
			Game.map.time = ((Packet10TimePacket)p).time;
		}
		else if(p.id == 12)
		{
			EventBus.processEventPacket((Packet12Event)p);
		}
		else if(p.id == 20)
		{
			GuiIngame.gui.addChatMessage(((Packet20Chat)p).message, ((Packet20Chat)p).prefix + ((Packet20Chat)p).player + ((Packet20Chat)p).suffix);
		}
	}
	
	/**
	 * PacketHandlerSP thread. It picks one {@link Packet} from the {@link #packetQueue} and {@link #sendingQueue}, processes one and sends the other.
	 * If any of the queues is overflowing, the internal {@link Thread#sleep(long)} is skipped to allow fast processing.
	 */
	@Override
	public void run()
	{		
		while(Game.netHandler.connected)
		{
			try
			{
				if(packetQueue.size() > 0)
				{
					if(packetQueue.get(0) != null) 
					{
						handlePacket(packetQueue.get(0));
						packetQueue.remove(0);
					}
					else packetQueue.remove(0);
				}
				if(sendingQueue.size() > 0)
				{
					if(sendingQueue.get(0) != null) 
					{
						sendPacket(sendingQueue.get(0));
						sendingQueue.remove(0);
					}
					else sendingQueue.remove(0);
				}		
				if(sendingQueue.size() < 50 && packetQueue.size() < 50)
				{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Writes the contents of a {@link Packet} to the OutputStream by the use of {@link Packet#writeData(DataInputStream)}. If an exception occurs,
	 * the {@link NetHandler} is closed.
	 * @param packet - Packet to send
	 */
	public void sendPacket(Packet packet)
	{		
		try {		
			Game.netHandler.out.writeByte(packet.id);
			packet.writeData(Game.netHandler.out);
			Game.netHandler.out.flush();
		} catch (Exception e){
			e.printStackTrace();
			if(Game.netHandler.connected) 
			{
				Game.netHandler.lastError = "Error while sending a packet. This might be caused by the server terminating.";
				Game.netHandler.close();
			}
		}
	}
}
