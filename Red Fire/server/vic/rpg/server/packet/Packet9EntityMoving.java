package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.utils.Direction;

public class Packet9EntityMoving extends Packet 
{
	public int xCoord;
	public int yCoord;
	public Direction rotation;
	public boolean isWalking;
	public String uniqueUUID;
	public boolean isPlayer = false;
	public String playerName;
	
	public Packet9EntityMoving(EntityLiving entity) 
	{
		super(9);
		
		this.xCoord = entity.xCoord;
		this.yCoord = entity.yCoord;
		this.rotation = entity.rotation;
		this.isWalking = entity.isWalking();
		this.uniqueUUID = entity.UUID;
		
		if(entity instanceof EntityPlayer) 
		{
			isPlayer = true;
			playerName = ((EntityPlayer)entity).username;
		}
	}
	
	public Packet9EntityMoving()
	{
		super(9);
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			uniqueUUID = stream.readUTF();
			xCoord = stream.readInt();
			yCoord = stream.readInt();
			rotation = Direction.getDirection(stream.readInt());
			isWalking = stream.readBoolean();
			isPlayer = stream.readBoolean();
			if(isPlayer)
			{
				playerName = stream.readUTF();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeUTF(uniqueUUID);
			stream.writeInt(xCoord);
			stream.writeInt(yCoord);
			stream.writeInt(rotation.getID());
			stream.writeBoolean(isWalking);
			stream.writeBoolean(isPlayer);
			if(isPlayer)
			{
				stream.writeUTF(playerName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	
}
