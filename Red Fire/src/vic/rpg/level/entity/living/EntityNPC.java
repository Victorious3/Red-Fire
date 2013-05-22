package vic.rpg.level.entity.living;

import vic.rpg.render.ImageBuffer;
import vic.rpg.server.ServerLoop;
import vic.rpg.utils.Utils;

public class EntityNPC extends EntityLiving 
{
	public EntityNPC() 
	{
		super(33, 32);
	}

	@Override
	public void tick() 
	{
		super.tick();
		
		if(!isWalking && Utils.getSide().equals(Utils.SIDE_SERVER))
		{
//			Random rand = new Random();
//			walkTo(xCoord + rand.nextInt(201) - 100, yCoord + rand.nextInt(201) - 100);
			
			EntityPlayer[] player = new EntityPlayer[ServerLoop.level.playerList.values().size()];
			player = ServerLoop.level.playerList.values().toArray(player);
//			
			if(player.length > 0) walkTo(player[0].xCoord - 35, player[0].yCoord);
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
		g2d.drawImage(ImageBuffer.getAnimatedImageData("/vic/rpg/resources/character/player_main_1.gif")[0], 0, 0, null);
	}
}
