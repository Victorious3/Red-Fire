package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet0Update extends Packet 
{
	public int data = 0;
	
	public Packet0Update() 
	{
		super(0);
	}
	
	public Packet0Update(int data) 
	{
		super(0);
		this.data = data;
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			data = stream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeInt(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
