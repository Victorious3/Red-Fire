package vic.rpg.listener;

import java.util.Comparator;

import vic.rpg.level.entity.EntityEvent;

public interface EntityEventListener
{
	public void onEventReceived(EntityEvent e);
	
	public void onEventPosted(EntityEvent e);
	
	public Priority getPriority();
	
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
