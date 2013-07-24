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
		this.sprites = EntityPlayer.steps[this.rotation];
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
		if(isWalking() && Utils.getSide().equals(Utils.SIDE_CLIENT))
		{						
			tickCounter++;
			if(tickCounter == 8)
			{				
				step++;
				if(step == 3) step = 0;
				this.sprites = EntityPlayer.steps[step];
				this.sprite = sprites[this.rotation];
				setImage(sprite);				
				tickCounter = 0;	
			}
		}
	}

	@Override
	public String getName() 
	{
		return "The Chaser";
	}

	public void initRender() 
	{
		this.sprites = EntityPlayer.steps[0];
		this.sprite = sprites[0];

		setImage(sprite);
	}
}
