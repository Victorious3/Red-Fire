package vic.rpg.event;

public interface IEventReceiver extends EventListener
{
	public EventBus getEventBus();
	
	public String getUniqueIdentifier();
	
	public int getDimension();
}
