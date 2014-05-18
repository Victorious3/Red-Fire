package vic.rpg.listener;

import java.util.Comparator;

import vic.rpg.world.entity.Entity;
import vic.rpg.world.entity.EntityEvent;

/**
 * The EntityEventListener has to be added to an {@link Entity} with {@link Entity#addEventListener(EntityEventListener)} to receive
 * {@link EntityEvent EntityEvents} dedicated to this Entity.
 * @author Victorious3
 *
 */
public interface EntityEventListener
{
	/**
	 * Called when an {@link EntityEvent} was received on the referenced {@link Entity}.
	 * The {@link EntityEvent} can be modified or cancelled.
	 * @param e
	 * @return EntityEvent
	 */
	public EntityEvent onEventReceived(EntityEvent e);
	
	/**
	 * Called when an {@link EntityEvent} was posted on the referenced {@link Entity}.
	 * The {@link EntityEvent} can be modified or cancelled.
	 * @param e
	 * @return EntityEvent
	 */
	public EntityEvent onEventPosted(EntityEvent e);
	
	/**
	 * Returns the Priority of this {@link EntityEventListener}.
	 * @return Priority
	 */
	public Priority getPriority();
	
	/**
	 * A Priority is used to sort the {@link EntityEventListener EntityEventListeners} to ceck
	 * which one should be notified of a new {@link EntityEvent} first.
	 * @author Victorious3
	 */
	public static class Priority implements Comparable<Priority>
	{
		private final int p;
		
		public static Priority PRIORITY_HIGHEST = new Priority(4);
		public static Priority PRIORITY_HIGH = new Priority(3);
		public static Priority PRIORITY_MEDIUM = new Priority(2);
		public static Priority PRIORITY_LOW = new Priority(1);
		public static Priority PRIORITY_LEAST = new Priority(0);
		
		private Priority(int p)
		{
			this.p = p;
		}	
		
		@Override
		public int compareTo(Priority arg0) 
		{
			if(arg0.p > p) return 1;
			if(arg0.p < p) return -1;
			if(arg0.p == p) return 0;
			return 0;
		}
		
		public static Comparator<EntityEventListener> entityEventListenerComperator = new Comparator<EntityEventListener>()
		{
			@Override
			public int compare(EntityEventListener o1, EntityEventListener o2) 
			{
				return o1.getPriority().compareTo(o2.getPriority());
			}
		};
	}
}
