package vic.rpg.server.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import vic.rpg.registry.WorldRegistry;
import vic.rpg.world.entity.Entity;

public class Packet7Entity extends Packet 
{
	/**
	 * Updates the entities by calling {@link Entity#readFromNBT(CompoundTag, Object...)}.
	 */
	public static final int MODE_UPDATE = 0;
	/**
	 * Deletes the entities.
	 */
	public static final int MODE_DELETE = 1;
	/**
	 * Creates completely new entities.
	 */
	public static final int MODE_CREATE = 2;
	
	public int mode;
	private Entity[] entities;
	private byte[] data;
	
	protected Packet7Entity(Entity[] entities, int mode, int i)
	{
		super(i);
		
		this.entities = entities;
		this.mode = mode;
	}
	
	/**
	 * @param entities
	 * @param mode: {@link #MODE_CREATE}, {@link #MODE_UPDATE}, {@link #MODE_DELETE}
	 */
	public Packet7Entity(Entity[] entities, int mode) 
	{		
		this(entities, mode, 7);
	}
	
	public Packet7Entity()
	{
		super(7);
	}
	
	public Packet7Entity(int id)
	{
		super(id);
	}
	
	public Packet7Entity(Entity entity, int mode) 
	{
		this(new Entity[]{entity}, mode);
	}
	
	public void update(vic.rpg.world.Map map)
	{
		try {
			NBTInputStream in = new NBTInputStream(new DataInputStream(new ByteArrayInputStream(data)));
			CompoundTag tag = (CompoundTag)in.readTag();
			in.close();
			
			Map<String, Tag> map2 = tag.getValue();

			for(Tag t : map2.values())
			{
				String UUID = ((CompoundTag)t).getString("uuid", null);
				map.getEntity(UUID).readFromNBT(tag, (Object[])null);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Entity[] create()
	{
		try {
			NBTInputStream in = new NBTInputStream(new DataInputStream(new ByteArrayInputStream(data)));
			CompoundTag tag = (CompoundTag)in.readTag();
			in.close();
			
			Map<String, Tag> map = tag.getValue();
			ArrayList<Entity> list = new ArrayList<Entity>();	
			
			for(Tag t : map.values())
			{
				Entity e = WorldRegistry.readEntityFromNBT((CompoundTag) t);
				list.add(e);
			}
			
			Entity[] entities = new Entity[list.size()];
			entities = list.toArray(entities);
			
			return entities;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return entities;
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			mode = stream.readInt();	
			data = new byte[stream.available()];
			stream.readFully(data);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeInt(mode);

			Map<String, Tag> map = new HashMap<String, Tag>();
			for(Entity e : entities)
			{
				map.put("entity", WorldRegistry.writeEntityToNBT(e));
			}
			
			CompoundTag tag = new CompoundTag("entities", map);			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			NBTOutputStream out;
			try {
				out = new NBTOutputStream(baos);
				out.writeTag(tag);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}				
			stream.write(baos.toByteArray());
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
