package vic.rpg.server.packet;

import java.io.IOException;
import java.util.ArrayList;

import vic.rpg.level.Entity;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.server.Server;
import vic.rpg.server.ServerLoop;
import vic.rpg.server.io.Connection;

public class PacketHandlerMP extends Thread
{
	private ArrayList<Packet> packetQueue = new ArrayList<Packet>();
	private ArrayList<Packet> sendingQueue = new ArrayList<Packet>();
	private Connection con;
	
	public PacketHandlerMP(Connection con)
	{
		this.con = con;
		this.setName("PacketHandler for player " + con.player);
	}

	public void addPacketToQueue(Packet p)
	{
		packetQueue.add(p);
	}
	public void addPacketToSendingQueue(Packet p)
	{
		sendingQueue.add(p);
	}
	
	public void handlePacket(Packet p)
	{			
		if(p.id == 0)
		{
			if(((Packet0Update)p).data == -1) addPacketToSendingQueue(p);
			else if(((Packet0Update)p).data == 0) Server.server.delConnection(con, new RuntimeException("Disconnect quitting"));
		}
		else if(p.id == 8)
		{
			p.id = 7;
			Server.server.broadcast(p);
		}
		else if(p.id == 9)
		{
			Packet9EntityMoving p9entitymoving = (Packet9EntityMoving) p;
			
			Entity e;
			if(p9entitymoving.isPlayer) e = ServerLoop.level.playerList.get(p9entitymoving.playerName);
			else e = ServerLoop.level.entities.get(p9entitymoving.uniqueUUID);
			
			e.xCoord = p9entitymoving.xCoord;
			e.yCoord = p9entitymoving.yCoord;
			
			if(p9entitymoving.isPlayer) ServerLoop.level.playerList.put(p9entitymoving.playerName, (EntityPlayer)e);
			else  ServerLoop.level.entities.put(e.uniqueUUID, e);
			
			Server.server.broadcast(p);
		}
		else if(p.id == 20)
		{
			System.out.println("[" + con.player + "]: " + ((Packet20Chat)p).message);
			Server.server.broadcast(p);
		}
	}
	
	private void sendPacket(Packet packet)
	{
		if(con != null)
		{
			try {
				con.out.writeByte(packet.id);
				packet.writeData(con.out);
				con.out.flush();
			} catch (IOException e) {
				Server.server.delConnection(con, e);
			}
		}
	}
	
	@Override
	public void run() 
	{
		while(con.connected)
		{
			if(packetQueue.size() != 0)
			{
				if(packetQueue.get(0) == null) continue;
				handlePacket(packetQueue.get(0));
				packetQueue.remove(0);
			}
			if(sendingQueue.size() != 0)
			{
				if(sendingQueue.get(0) == null) continue;
				sendPacket(sendingQueue.get(0));
				sendingQueue.remove(0);
			}
			
			if(sendingQueue.size() < 50 && packetQueue.size() < 50)
			{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
