package vic.rpg.level.entity.living;

import vic.rpg.render.TextureFX;
import vic.rpg.server.ServerLoop;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class EntityNPC extends EntityLiving 
{
	public int step = 0;
	
	public static TextureFX[] sprites = TextureFX.createTextureFXArray("/vic/rpg/resources/character/character.png", 70, 70, 8, 8, 0, 0, 10);	
	
	public EntityNPC() 
	{
		super(70, 70);
		this.zLevel = -1;
		this.speed = 2;
		
		if(Utils.getSide() == Side.CLIENT) this.initRender();
	}
	int tickCounter = 0;
		
	@Override
	public void tick() 
	{
		super.tick();
		
		if(!isWalking() && !walk && Utils.getSide() == Side.SERVER)
		{
			EntityPlayer[] player = new EntityPlayer[ServerLoop.level.onlinePlayersMap.values().size()];
			player = ServerLoop.level.onlinePlayersMap.values().toArray(player);		
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
		this.rotatedSprites = sprites;
		this.setRotation(Direction.NORTH);
	}
}
