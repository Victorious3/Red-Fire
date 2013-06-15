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
	}
	int tickCounter = 0;
		
	@Override
	public void tick() 
	{
		super.tick();
		
		if(!isWalking() && Utils.getSide().equals(Utils.SIDE_SERVER))
		{
//			Random rand = new Random();
//			walkTo(xCoord + rand.nextInt(201) - 100, yCoord + rand.nextInt(201) - 100);
			
			EntityPlayer[] player = new EntityPlayer[ServerLoop.level.playerList.values().size()];
			player = ServerLoop.level.playerList.values().toArray(player);
//			
			if(player.length > 0) walkTo(player[0].xCoord - 70, player[0].yCoord);
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

	@Override
	public void initRender() 
	{
		this.sprites = EntityPlayer.steps[0];
		this.sprite = sprites[0];

		setImage(sprite);
	}
}
