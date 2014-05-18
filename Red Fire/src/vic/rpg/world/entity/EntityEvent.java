package vic.rpg.world.entity;

import java.util.HashMap;

import vic.rpg.listener.EntityEventListener;
import vic.rpg.utils.Utils.Side;

/**
 * An EntityEvent is an Event that can be triggered on the Server or Client side and is automatically redirected to the right
 * counterpart of this Entity on the other side. Use {@link EntityEventListener EntityEventListeners} and add them to any Entity
 * by calling {@link Entity#addEventListener(EntityEventListener)} to make them receive EntityEvents for the specified {@link Side}.
 * @author Victorious3
 */
public class EntityEvent implements Cloneable 
{
	private static HashMap<Integer, EntityEvent> eventRegistry = new HashMap<Integer, EntityEvent>();
	
	public HashMap<String, Object> data = new HashMap<String, Object>();
	public Side side;
	public int id;
	private boolean isCancelled = false;
	
	/**
	 * Creates an EntityEvent with a given id that can be received on the given {@link Side}. All {@link Side Sides} are supported.
	 * <b>Remember to add the created Event to {@link #registerEntityEvent(EntityEvent)}!</b>
	 * @param s
	 * @param id
	 */
	public EntityEvent(Side s, int id)
	{
		this.side = s;
		this.id = id;
	}
	
	/**
	 * Returns weather this EntityEvent was cancelled by an {@link EntityEventListener}.
	 * @return
	 */
	public boolean isCancelled()
	{
		return isCancelled;
	}
	
	/**
	 * Cancel this EntityEvent to stop it from getting passed to the other {@link EntityEventListener EntityEventListeners}.
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
	 * Register a new EntityEvent.
	 * @param e
	 */
	public static void registerEntityEvent(EntityEvent e)
	{
		if(eventRegistry.containsKey(e.id)) throw new IllegalArgumentException("There's already an EntityEvent registered with the id " + e.id + "!");
		eventRegistry.put(e.id, e);
	}
	
	@Override
	public EntityEvent clone()
	{
		try {
			return (EntityEvent) super.clone();
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
	public static EntityEvent getEntityEvent(int id)
	{
		return eventRegistry.get(id);
	}
}
