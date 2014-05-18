package vic.rpg.server.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

import vic.rpg.world.entity.living.Inventory;

public class Packet13InventoryUpdate extends Packet 
{
	public Inventory inventory;
	
	public Packet13InventoryUpdate() 
	{
		super(13);
	}
	
	public Packet13InventoryUpdate(Inventory inventory) 
	{
		super(13);
		this.inventory = inventory;
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			byte[] b = new byte[stream.available()];
			stream.readFully(b);
			
			NBTInputStream nbtStream = new NBTInputStream(new ByteArrayInputStream(b));
			CompoundTag tag = (CompoundTag)nbtStream.readTag();
			Inventory inventory = new Inventory(null);
			inventory.readFromNBT(tag);
			this.inventory = inventory;
			
			nbtStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			NBTOutputStream nbtStream = new NBTOutputStream(bOut);		
			nbtStream.writeTag(inventory.writeToNBT(new CompoundTag("inventory", new HashMap<String, Tag>())));
			nbtStream.close();
			
			stream.write(bOut.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
