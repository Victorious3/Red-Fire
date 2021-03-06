package vic.rpg.world.entity.living;

import vic.rpg.Init;
import vic.rpg.event.Event;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.entity.Entity;

/**
 * The EntityController is the where all the {@link Entity} manipulations are done that use {@link Event EntityEvents}.
 * @author Victorious3
 */
public class EntityController 
{
	@Init(side = Side.BOTH)
	public static void init()
	{
		Event.registerEntityEvent(new HealthChangeEvent());
	}
	
	public static class HealthChangeEvent extends Event
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
		ent.getEventBus().postEvent(new HealthChangeEvent((int)(ent.max_lp * health)));
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
		ent.getEventBus().postEvent(new HealthChangeEvent(ent.lp + (int)(ent.max_lp * health)));
	}
}
