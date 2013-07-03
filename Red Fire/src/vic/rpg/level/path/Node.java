package vic.rpg.level.path;

import java.awt.Point;

import vic.rpg.level.Level;

public class Node 
{
	public int x,y;
	public boolean isBlocked = false;
	
	public Node(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point toPoint()
	{
		int x2 = x * Level.CELL_SIZE;
		int y2 = y * Level.CELL_SIZE;
		
		return new Point(x2, y2);
	}	
}
