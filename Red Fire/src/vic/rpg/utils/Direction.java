package vic.rpg.utils;

import java.util.HashMap;

/**
 * Contains every Direction on a compass.
 * @author Victorious3
 */
public enum Direction 
{
	NORTH(0), NORTH_EAST(1), EAST(2), SOUTH_EAST(3), SOUTH(4), SOUTH_WEST(5), WEST(6), NORTH_WEST(7), CENTER(8);
	
	private final int id;
	
	private Direction(int id)
	{
		this.id = id;
	}
	
	private static HashMap<Integer, Direction> allValues = new HashMap<Integer, Direction>();
	
	static 
	{
		for(Direction d : values())
		{
			allValues.put(d.id, d);
		}
	}
	
	public int getID()
	{
		return id;
	}
	
	public Direction rot90Clockwise(Direction d)
	{
		if(d == CENTER) return d;
		if(d == WEST)
		{
			return NORTH;
		}
		if(d == NORTH_WEST)
		{
			return NORTH_EAST;
		}
		else return getDirection(d.getID() + 1);
	}
		
	public static Direction getDirection(int id)
	{
		return allValues.get(id);
	}
	
	public static int getAmount()
	{
		return allValues.size();
	}
}
