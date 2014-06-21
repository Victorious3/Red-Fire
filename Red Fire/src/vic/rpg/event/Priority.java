package vic.rpg.event;

import java.util.Comparator;

/**
 * A Priority is used to sort the {@link EventListener EntityEventListeners} to ceck
 * which one should be notified of a new {@link Event} first.
 * @author Victorious3
 */
public class Priority implements Comparable<Priority>
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
	
	public static Comparator<EventListener> entityEventListenerComperator = new Comparator<EventListener>()
	{
		@Override
		public int compare(EventListener o1, EventListener o2) 
		{
			return o1.getPriority().compareTo(o2.getPriority());
		}
	};
}