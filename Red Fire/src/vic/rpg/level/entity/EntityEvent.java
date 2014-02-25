package vic.rpg.level.entity;

import java.util.HashMap;

import vic.rpg.utils.Utils.Side;

public class EntityEvent implements Cloneable 
{
	private static HashMap<Integer, EntityEvent> eventRegistry = new HashMap<Integer, EntityEvent>();
	
	public HashMap<String, Object> data = new HashMap<String, Object>();
	public Side side;
	public int id;
	
	public EntityEvent(Side s, int id)
	{
		this.side = s;
		this.id = id;
	}
	
	public Object getData(String objName)
	{
		return data.get(objName);		
	}
	
	public void putData(String objName, Object obj)
	{
		data.put(objName, obj);	
	}
	
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

	public static EntityEvent getEntityEvent(int id)
	{
		return eventRegistry.get(id);
	}
}
