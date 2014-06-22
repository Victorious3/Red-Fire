package vic.rpg.server.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vic.rpg.event.EventBus;
import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.server.ServerLoop;
import vic.rpg.server.io.Connection;
import vic.rpg.world.Map;
import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.living.EntityPlayer;

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
				EntityPlayer player = (EntityPlayer)(((Packet8PlayerUpdate)p).create()[0]);
				ServerLoop.world.getMap(player.dimension).entityMap.put(ServerLoop.world.getUUID(con.username), player);
				Server.server.broadcastLocally(player.dimension, p);
			}
			else if(p.id == 9)
			{
				Packet9EntityMoving p9entitymoving = (Packet9EntityMoving) p;
				
				EntityPlayer e = ServerLoop.world.getPlayer(con.username);
				
				e.xCoord = p9entitymoving.xCoord;
				e.yCoord = p9entitymoving.yCoord;
				
				Server.server.broadcastLocally(e.dimension, p);
			}
			else if(p.id == 20)
			{
				String message = ((Packet20Chat)p).message;
				if(message.startsWith("/"))
				{
					List<String> list = new ArrayList<String>();
					Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);
					
					while(m.find())
					{
						list.add(m.group(1).replace("\"",""));
					}
					
					String command = list.remove(0);
					command = command.replace("/", "");
					
					Server.server.inputHandler.handleCommand(command, list, con);
				}
				else
				{
					((Packet20Chat)p).prefix = con.prefix;
					((Packet20Chat)p).suffix = con.suffix;
					System.out.println("[" + con.username + "]: " + message);
					Server.server.broadcast(p);
				}
			}
			else if(p.id == 11)
			{
				Packet11EntityInteraction packet = (Packet11EntityInteraction) p;
				Map map = ServerLoop.world.getMap(ServerLoop.world.getDimension(con.username));
				Entity entity = map.entityMap.get(packet.UUID);
				EntityPlayer player = ServerLoop.world.getPlayer(con.username);
				if(packet.mode == Packet11EntityInteraction.MODE_ONCLICK)
				{
					entity.onMouseClicked(packet.data[0], packet.data[1], player, packet.data[2]);
				}
			}
			else if(p.id == 12)
			{
				EventBus.processEventPacket((Packet12Event)p);
			}
			else if(p.id == 13)
			{
				//TODO This allows cheaters to modify their Inventory in every way they like. They could even add more size to it... Think of some verifying algorithm.
				Packet13InventoryUpdate packet = (Packet13InventoryUpdate) p;
				packet.update(ServerLoop.world.getPlayer(con.username).inventory);
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
