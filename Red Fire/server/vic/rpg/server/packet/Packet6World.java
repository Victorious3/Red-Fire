package vic.rpg.server.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

import vic.rpg.level.Level;

public class Packet6World extends Packet
{
	Level level;
	byte[] data;
	
	public Packet6World(Level level)
	{
		super(6);
		this.level = level;
	}
	
	public Packet6World()
	{
		super(6);
	}
	
	public CompoundTag getData()
	{		
		NBTInputStream in;

		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			in = new NBTInputStream(new DataInputStream(stream));
			CompoundTag tag = (CompoundTag)in.readTag();
			in.close();
			stream.close();
			return tag;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void readData(DataInputStream stream) 
	{	
		try {
			data = new byte[stream.available()];
			stream.readFully(data);
	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	
		NBTOutputStream out;
		try {
			out = new NBTOutputStream(new DataOutputStream(baos));
			out.writeTag(level.writeToNBT(null, true));		
			baos.flush();
			baos.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			stream.write(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

