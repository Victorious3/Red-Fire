package vic.rpg.server.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.EntityEvent;

public class Packet12Event extends Packet
{
	public EntityEvent eev;
	public String UUID;
	
	public Packet12Event()
	{
		super(12);
	}
	
	public Packet12Event(EntityEvent eev, Entity entity)
	{
		super(12);
		this.eev = eev;
		this.UUID = entity.UUID;
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			int eventID = stream.readInt();
			UUID = stream.readUTF();
			int objectLength = stream.readInt();
			
			eev = EntityEvent.getEntityEvent(eventID).clone();
			byte[] b = new byte[stream.available()];
			stream.readFully(b);
			
			NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(b));		
			for(int i = 0; i < objectLength; i++)
			{
				Tag t = nbtStream.readTag();
				Object data = t.getValue();
				if(data instanceof Short) data = (Short)data == 1;
				eev.data.put(t.getName(), data);			
			}
			nbtStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeInt(eev.id);
			stream.writeUTF(UUID);
			stream.writeInt(eev.data.size());
			
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			NBTOutputStream nbtStream = new NBTOutputStream(bOut);
			
			for(String s : eev.data.keySet())
			{
				Object o = eev.data.get(s);
				if(o instanceof Boolean) nbtStream.writeTag(new ShortTag(s, ((Boolean)o).booleanValue() ? (short)1 : (short)0));
				else if(o instanceof Integer) nbtStream.writeTag(new IntTag(s, ((Integer)o).intValue()));
				else if(o instanceof Byte) nbtStream.writeTag(new ByteTag(s, ((Byte)o).byteValue()));
				else if(o instanceof Double) nbtStream.writeTag(new DoubleTag(s, ((Double)o).doubleValue()));
				else if(o instanceof Float) nbtStream.writeTag(new FloatTag(s, ((Float)o).floatValue()));
				else if(o instanceof Long) nbtStream.writeTag(new LongTag(s, ((Long)o).longValue()));
				else if(o instanceof String) nbtStream.writeTag(new StringTag(s, (String)o));
				else if(o instanceof Integer[]) nbtStream.writeTag(new IntArrayTag(s, ArrayUtils.toPrimitive((Integer[])o)));
				else if(o instanceof Byte[]) nbtStream.writeTag(new ByteArrayTag(s, ArrayUtils.toPrimitive((Byte[])o)));
				else throw new IllegalArgumentException("Object of type " + o.getClass().getSimpleName() + " has an illegal type!");
			}
			
			nbtStream.close();
			stream.write(bOut.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
