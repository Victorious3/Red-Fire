package vic.rpg.server.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import vic.rpg.level.entity.Entity;
import vic.rpg.level.entity.EntityEvent;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.server.GameState;
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
		this.setName("Server PacketHandler for player " + con.username);
	}

	public void addPacketToQueue(Packet p)
	{
		packetQueue.add(p);
	}
	
	public void addPacketToSendingQueue(Packet p)
	{
		if(con.STATE == GameState.RUNNING) sendingQueue.add(p);
	}
	
	public int getQueueLenght()
	{
		return packetQueue.size();
	}
	
	public int getSendingQueueLenght()
	{
		return sendingQueue.size();
	}
	
	public void handlePacket(Packet p)
	{			
		try {
			if(p.id == 0)
			{
				con.STATE = ((Packet0StateUpdate)p).data;
				if(con.STATE == GameState.RUNNING) addPacketToSendingQueue(p);
				else if(con.STATE == GameState.QUIT) Server.server.delConnection(con, "Disconnect quitting");
			}
			else if(p.id == 8)
			{
				p.id = 7;
				EntityPlayer player = (EntityPlayer)(((Packet8PlayerUpdate)p).entities[0]);
				ServerLoop.level.entityMap.put(ServerLoop.level.onlinePlayersMap.get(con.username), player);
				Server.server.broadcast(p);
			}
			else if(p.id == 9)
			{
				Packet9EntityMoving p9entitymoving = (Packet9EntityMoving) p;
				
				Entity e = ServerLoop.level.entityMap.get(p9entitymoving.UUID);
				
				e.xCoord = p9entitymoving.xCoord;
				e.yCoord = p9entitymoving.yCoord;
				
				ServerLoop.level.entityMap.put(e.UUID, e);
				
				Server.server.broadcast(p);
			}
			else if(p.id == 20)
			{
				String message = ((Packet20Chat)p).message;
				if(message.startsWith("/"))
				{
					String[] args = message.split(" ");
					String command = args[0];
					command = command.replace("/", "");
					LinkedList<String> args2 = new LinkedList<String>(Arrays.asList(args));
					args2.remove(0);
					Server.server.inputHandler.handleCommand(command, args2, con);
				}
				else
				{
					System.out.println("[" + con.username + "]: " + message);
					Server.server.broadcast(p);
				}
			}
			else if(p.id == 11)
			{
				Packet11EntityInteraction packet = (Packet11EntityInteraction) p;
				Entity entity = ServerLoop.level.entityMap.get(packet.UUID);
				EntityPlayer player = ServerLoop.level.getPlayer(con.username);
				if(packet.mode == Packet11EntityInteraction.MODE_ONCLICK)
				{
					entity.onMouseClicked(packet.data[0], packet.data[1], player, packet.data[2]);
				}
			}
			else if(p.id == 12)
			{
				EntityEvent eev = ((Packet12Event)p).eev;
				ServerLoop.level.entityMap.get(((Packet12Event)p).UUID).onEventReceived(eev);	
			}
			else if(p.id == 13)
			{
				//TODO This allows cheaters to modify their Inventory in every way they like. They could even add more size to it... Think of some verifying algorithm.
				Packet13InventoryUpdate packet = (Packet13InventoryUpdate) p;
				packet.inventory.parentEntity = ServerLoop.level.getPlayer(con.username);
				packet.inventory.parentEntity.addEventListener(packet.inventory);
				packet.inventory.parentEntity.inventory = packet.inventory;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				Server.server.delConnection(con, e.getMessage());
			}
		}
	}
	
	@Override
	public void run() 
	{
		while(con.connected)
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
		}
	}
}
