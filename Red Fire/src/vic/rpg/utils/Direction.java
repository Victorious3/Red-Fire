package vic.rpg.utils;

import java.util.HashMap;

public enum Direction 
{
	NORTH(0), EAST(1), SOUTH(2), WEST(3), NORTH_EAST(4), NORTH_WEST(5), SOUTH_EAST(6), SOUTH_WEST(7);
	
	private final int id;
	
	private Direction(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	
	private static HashMap<Integer, Direction> allValues = new HashMap<Integer, Direction>();
	
	public static Direction getDirection(int id)
	{
		return allValues.get(id);
	}
}
