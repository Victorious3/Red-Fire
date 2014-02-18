package vic.rpg.client.packet;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import vic.rpg.Game;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiIngame;
import vic.rpg.gui.GuiPlayer;
import vic.rpg.level.Level;
import vic.rpg.level.entity.Entity;
import vic.rpg.level.entity.EntityEvent;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.render.Screen;
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
import vic.rpg.utils.Utils;

public class PacketHandlerSP extends Thread
{	
	private ArrayList<Packet> packetQueue = new ArrayList<Packet>();
	private ArrayList<Packet> sendingQueue = new ArrayList<Packet>();
	
	public PacketHandlerSP()
	{
		this.setName("Client PacketHandler");
		this.start();
	}
	
	public void addPacketToQueue(Packet p)
	{
		if(Game.netHandler.STATE == GameState.RUNNING) packetQueue.add(p);
	}
	
	public void addPacketToSendingQueue(Packet p)
	{
		sendingQueue.add(p);	
	}
	
	public void handlePacket(Packet p)
	{
		if(p == null) return;
		if(p.id == 1)
		{
			System.out.println(((Packet1ConnectionRefused)p).message);
			Game.netHandler.lastError = ((Packet1ConnectionRefused)p).message;
			Game.netHandler.close();
		}
		else if(p.id == 6)
		{
			Game.level = new Level(); 
			Game.level.readFromNBT(((Packet6World)p).getData());
			Game.netHandler.STATE = GameState.RUNNING;
			sendPacket(new Packet0StateUpdate(Game.netHandler.STATE));
		}
		else if(p.id == 7)
		{
			Packet7Entity p7entity = (Packet7Entity) p;
			
			Entity[] entities = p7entity.entities;
			int mode = p7entity.mode;
			
			switch(mode)
			{
			case Packet7Entity.MODE_CREATE:
			case Packet7Entity.MODE_UPDATE:
				for (Entity e : entities)
				{
					if(Game.level != null) Game.level.entityMap.put(e.UUID, e);	
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
				break;
			case Packet7Entity.MODE_DELETE:
				
				Map<String, Entity> map = Collections.synchronizedMap(Game.level.entityMap);
				
				synchronized(map)
				{
					for (Entity e : entities)
					{
						map.remove(e.UUID);
					}
				}			
			}
		}
		else if(p.id == 9)
		{
			if(Game.level != null && Game.getPlayer() != null)
			{
				EntityLiving e = (EntityLiving) Game.level.entityMap.get(((Packet9EntityMoving)p).UUID);
				
				if(e.UUID != Game.getPlayer().UUID)
				{
					e.xCoord = ((Packet9EntityMoving)p).xCoord;
					e.yCoord = ((Packet9EntityMoving)p).yCoord;
					e.setRotation(((Packet9EntityMoving)p).rotation);
					e.setWalking(((Packet9EntityMoving)p).isWalking);
					Game.level.entityMap.put(e.UUID, e);
				}
			}
		}
		else if(p.id == 10)
		{
			Game.level.time = ((Packet10TimePacket)p).time;
		}
		else if(p.id == 12)
		{
			EntityEvent eev = ((Packet12Event)p).eev;
			Game.level.entityMap.get(((Packet12Event)p).UUID).processEvent(eev);		
		}
		else if(p.id == 20)
		{
			GuiIngame.gui.addChatMessage(((Packet20Chat)p).message, ((Packet20Chat)p).player);
		}
	}
	
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
