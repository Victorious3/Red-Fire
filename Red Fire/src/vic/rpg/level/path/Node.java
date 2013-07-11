package vic.rpg.level.path;

import java.awt.Point;

import vic.rpg.level.Level;

public class Node implements Cloneable 
{
	public int x,y;
	public boolean isBlocked = false;
	
	public double g;
	public double h;
	public double f;
	
	public Node parent;
	
	private Node(){}
	
	public Node(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public static Node fromPoint(Point p)
	{
		Node n = new Node();
		n.x = (int) ((float)p.x / (float)Level.CELL_SIZE);
		n.y = (int) ((float)p.y / (float)Level.CELL_SIZE);
		return n;
	}
	
	public Point toPoint()
	{
		int x2 = x * Level.CELL_SIZE;
		int y2 = y * Level.CELL_SIZE;
		
		return new Point(x2, y2);
	}

	@Override
	public boolean equals(Object arg0) 
	{
		if(arg0 instanceof Node)
		{
			Node n = (Node) arg0;
			if(n.x == x && n.y == y) return true;
		}
		return false;
	}
	
	@Override
	public Node clone()
	{
		try {
			return (Node) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
