package vic.rpg.server.packet;

import vic.rpg.level.entity.Entity;
import vic.rpg.level.entity.living.EntityPlayer;

public class Packet8PlayerUpdate extends Packet7Entity
{
	public Packet8PlayerUpdate(EntityPlayer player, int mode) 
	{
		super(new Entity[]{player}, mode, 8);
	}
	
	public Packet8PlayerUpdate()
	{
		super(8);
	}
}
