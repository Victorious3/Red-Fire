package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import vic.rpg.utils.Direction;
import vic.rpg.world.entity.living.EntityLiving;

public class Packet9EntityMoving extends Packet 
{
	public int xCoord;
	public int yCoord;
	public Direction rotation;
	public boolean isWalking;
	public String UUID;
	
	public Packet9EntityMoving(EntityLiving entity) 
	{
		super(9);
		
		this.xCoord = entity.xCoord;
		this.yCoord = entity.yCoord;
		this.rotation = entity.rotation;
		this.isWalking = entity.isWalking();
		this.UUID = entity.UUID;
	}
	
	public Packet9EntityMoving()
	{
		super(9);
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			UUID = stream.readUTF();
			xCoord = stream.readInt();
			yCoord = stream.readInt();
			rotation = Direction.getDirection(stream.readInt());
			isWalking = stream.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeUTF(UUID);
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(rotation.getID());
			stream.writeBoolean(isWalking);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	
}
