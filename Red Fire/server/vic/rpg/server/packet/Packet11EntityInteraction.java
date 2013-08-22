package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import vic.rpg.level.Entity;

public class Packet11EntityInteraction extends Packet 
{
	public static int MODE_ONCLICK = 1;
	
	public Integer[] data;
	public String UUID;
	public int mode;
	
	public Packet11EntityInteraction(Entity ent, int mode, Integer... data) 
	{
		super(11);
		this.data = data;
		this.UUID = ent.UUID;
		this.mode = mode;
	}
	
	public Packet11EntityInteraction()
	{
		super(11);
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			UUID = stream.readUTF();
			mode = stream.readInt();	
			ArrayList<Integer> list = new ArrayList<Integer>();
			while(stream.available() > 0)
			{
				list.add(stream.readInt());
			}
			data = new Integer[list.size()];
			data = list.toArray(data);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeUTF(UUID);
			stream.writeInt(mode);		
			for(int i : data)
			{
				stream.writeInt(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
