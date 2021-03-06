package vic.rpg.world.entity.living;

import vic.rpg.client.render.TextureFX;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

/**
 * A generic Non-Player-Character.
 * @author Victorious3
 */
public class EntityNPC extends EntityLiving 
{
	public int step = 0;
	
	public static TextureFX[] sprites = TextureFX.createTextureFXArray("/vic/rpg/resources/character/character.png", 70, 70, 8, 8, 0, 0, 10);	
	
	public EntityNPC() 
	{
		super(70, 70);
		this.speed = 2;
		
		if(Utils.getSide() == Side.CLIENT) this.initRender();
	}
	int tickCounter = 0;
	
	@Override
	public void tick() 
	{
		super.tick();
		
		//TODO Operations like that wont work anymore.
		/*if(!isWalking() && !walk && Utils.getSide() == Side.SERVER)
		{
			String[] players = new String[mapObj.onlinePlayersMap.values().size()];
			players = mapObj.onlinePlayersMap.values().toArray(players);
			if(players.length > 0)
			{
				Entity player = mapObj.entityMap.get(players[0]);
				walkTo(player.xCoord, player.yCoord, Double.MAX_VALUE);
			}
		}*/
	}

	@Override
	public String getName() 
	{
		return "The Chaser";
	}

	/**
	 * Initialize the current sprite and the rotation.
	 */
	public void initRender() 
	{
		this.rotatedSprites = sprites;
		this.setRotation(Direction.NORTH);
	}
}
