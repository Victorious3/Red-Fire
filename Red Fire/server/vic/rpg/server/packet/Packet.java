package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;

public abstract class Packet implements Cloneable {
	
	public int id = 0;
	
	protected Packet(int id)	
	{
		this.id = id;
	}
	
	public abstract void readData(DataInputStream stream);
	
	@Override
	public Packet clone() 
	{
		try {
			return (Packet) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract void writeData(DataOutputStream stream);
	
	private static HashMap<Integer, Packet> packetRegistry = new HashMap<Integer, Packet>(); 

	static
	{
		packetRegistry.put(0, new Packet0StateUpdate());
		packetRegistry.put(1, new Packet1ConnectionRefused());
		packetRegistry.put(20, new Packet20Chat());
		packetRegistry.put(6, new Packet6World());
		packetRegistry.put(7, new Packet7Entity());
		packetRegistry.put(8, new Packet8PlayerUpdate());
		packetRegistry.put(9, new Packet9EntityMoving());
		packetRegistry.put(10, new Packet10TimePacket());
		packetRegistry.put(11, new Packet11EntityInteraction());
	}
	
	public static Packet getPacket(int id)
	{
		return packetRegistry.get(id).clone();
	}
	
	public void handlePacket(int side){}
}
