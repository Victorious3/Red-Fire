package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet10TimePacket extends Packet 
{	
	public int time;
	
	public Packet10TimePacket(int time) 
	{
		super(10);		
		this.time = time;
	}
	
	public Packet10TimePacket() 
	{
		super(10);
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			time = stream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeInt(time);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
