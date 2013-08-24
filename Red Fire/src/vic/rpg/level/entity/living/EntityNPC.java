package vic.rpg.level.entity.living;

import vic.rpg.server.ServerLoop;
import vic.rpg.utils.Utils;

public class EntityNPC extends EntityLiving 
{
	public int step = 0;
	
	public EntityNPC() 
	{
		super(33, 32);
		this.zLevel = -1;
		this.speed = 2;
		
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.initRender();
	}
	int tickCounter = 0;
		
	@Override
	public void tick() 
	{
		super.tick();
		
		if(!isWalking() && !walk && Utils.getSide().equals(Utils.SIDE_SERVER))
		{
			EntityPlayer[] player = new EntityPlayer[ServerLoop.level.onlinePlayersList.values().size()];
			player = ServerLoop.level.onlinePlayersList.values().toArray(player);		
			if(player.length > 0) walkTo(player[0].xCoord - 70, player[0].yCoord, Double.MAX_VALUE);
		}
	}

	@Override
	public String getName() 
	{
		return "The Chaser";
	}

	public void initRender() 
	{
		this.rotatedSprites = EntityPlayer.sprites.clone();
		this.setRotation(0);
	}
}
