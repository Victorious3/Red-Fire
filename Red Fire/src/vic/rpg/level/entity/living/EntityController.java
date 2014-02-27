package vic.rpg.level.entity.living;

import vic.rpg.Init;
import vic.rpg.level.entity.Entity;
import vic.rpg.level.entity.EntityEvent;
import vic.rpg.utils.Utils.Side;

/**
 * The EntityController is the where all the {@link Entity} manipulations are done that use {@link EntityEvent EntityEvents}.
 * @author Victorious3
 */
public class EntityController 
{
	@Init(side = Side.BOTH)
	public static void init()
	{
		EntityEvent.registerEntityEvent(new HealthChangeEvent());
	}
	
	public static class HealthChangeEvent extends EntityEvent
	{
		public HealthChangeEvent()
		{
			super(Side.BOTH, 0);
		}
		
		public HealthChangeEvent(int health) 
		{
			super(Side.BOTH, 0);
			this.putData("health", health);
		} 
	};
	
	/**
	 * Sets the health of an EntityLiving and updates it via the event bus. The value is a
	 * {@code float} from 0 to 1. It sets the current health to the percentage of the maximum
	 * health.
	 * @param ent
	 * @param health
	 */
	public static void setHealth(EntityLiving ent, float health)
	{
		ent.postEvent(new HealthChangeEvent((int)(ent.max_lp * health)));
	}
	
	/**
	 * Changes the health of an EntityLiving and updates it via the event bus. The value is a
	 * {@code float} from 0 to 1. It adds the percentage value of the maximal health to the current
	 * health.
	 * @param ent
	 * @param health
	 */
	public static void changeHealth(EntityLiving ent, float health)
	{
		ent.postEvent(new HealthChangeEvent(ent.lp + (int)(ent.max_lp * health)));
	}
}
