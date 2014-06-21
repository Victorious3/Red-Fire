package vic.rpg.event;

import java.util.HashMap;

import vic.rpg.utils.Utils.Side;
import vic.rpg.world.entity.Entity;

/**
 * An Event can be triggered on the Server or Client side and is automatically redirected to the right
 * counterpart of this Entity on the other side. Use {@link EventListener EntityEventListeners} and add them to any EventBus
 * by calling {@link Entity#addEventListener(EventListener)} to make them receive Events for the specified {@link Side}.
 * @author Victorious3
 */
public class Event implements Cloneable 
{
	private static HashMap<Integer, Event> eventRegistry = new HashMap<Integer, Event>();
	
	public HashMap<String, Object> data = new HashMap<String, Object>();
	public Side side;
	public int id;
	private boolean isCancelled = false;
	
	/**
	 * Creates an Event with a given id that can be received on the given {@link Side}. All {@link Side Sides} are supported.
	 * <b>Remember to add the created Event to {@link #registerEntityEvent(Event)}!</b>
	 * @param s
	 * @param id
	 */
	public Event(Side s, int id)
	{
		this.side = s;
		this.id = id;
	}
	
	/**
	 * Returns weather this Event was cancelled by an {@link EventListener}.
	 * @return
	 */
	public boolean isCancelled()
	{
		return isCancelled;
	}
	
	/**
	 * Cancel this Event to stop it from getting passed to the other {@link EventListener EventListeners}.
	 */
	public void cancel()
	{
		isCancelled = true;
	}
	
	/**
	 * Gets a given Object from the data map.
	 * @param objName
	 * @return
	 */
	public Object getData(String objName)
	{
		return data.get(objName);		
	}
	
	/**
	 * Put a given Object to the data map.
	 * <b>Only primitive types + String are supported!</b>
	 * @param objName
	 * @param obj
	 */
	public void putData(String objName, Object obj)
	{
		data.put(objName, obj);	
	}
	
	/**
	 * Register a new Event.
	 * @param e
	 */
	public static void registerEntityEvent(Event e)
	{
		if(eventRegistry.containsKey(e.id)) throw new IllegalArgumentException("There's already an Event registered with the id " + e.id + "!");
		eventRegistry.put(e.id, e);
	}
	
	@Override
	public Event clone()
	{
		try {
			return (Event) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the EntityEvent bound to the given id.
	 * @param id
	 * @return
	 */
	public static Event getEntityEvent(int id)
	{
		return eventRegistry.get(id);
	}
}
