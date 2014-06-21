package vic.rpg.event;

import vic.rpg.world.entity.Entity;

/**
 * The EntityEventListener has to be added to an {@link Entity} with {@link Entity#addEventListener(EventListener)} to receive
 * {@link Event EntityEvents} dedicated to this Entity.
 * @author Victorious3
 *
 */
public interface EventListener
{
	/**
	 * Called when an {@link Event} was received on the referenced {@link Entity}.
	 * The {@link Event} can be modified or cancelled.
	 * @param e
	 * @return EntityEvent
	 */
	public Event onEventReceived(Event e);
	
	/**
	 * Called when an {@link Event} was posted on the referenced {@link Entity}.
	 * The {@link Event} can be modified or cancelled.
	 * @param e
	 * @return EntityEvent
	 */
	public Event onEventPosted(Event e);
	
	/**
	 * Returns the Priority of this {@link EventListener}.
	 * @return Priority
	 */
	public Priority getPriority();
}
