package vic.rpg.level.entity.living;

import vic.rpg.level.entity.EntityEvent;
import vic.rpg.utils.Utils.Side;

public class EntityController 
{
	static
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
	
	public static void setHealth(EntityLiving ent, float health)
	{
		ent.postEvent(new HealthChangeEvent((int)(ent.max_lp * health)));
	}
	
	public static void changeHealth(EntityLiving ent, float health)
	{
		ent.postEvent(new HealthChangeEvent(ent.lp + (int)(ent.max_lp * health)));
	}
}
