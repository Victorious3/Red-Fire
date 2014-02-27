package vic.rpg.level.path;

import java.awt.Point;

import vic.rpg.level.Level;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.tiles.Tile;

/**
 * A node is basically a {@link Tile} coordinate. It contains information about weather an {@link EntityLiving}
 * can walk onto it.
 * @author Victorious3
 *
 */
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
	
	/**
	 * Creates a new Node from a {@link Point}.
	 * @param p
	 * @return
	 */
	public static Node fromPoint(Point p)
	{
		Node n = new Node();
		n.x = (int) ((float)p.x / (float)Level.CELL_SIZE);
		n.y = (int) ((float)p.y / (float)Level.CELL_SIZE);
		return n;
	}
	
	/**
	 * Returns the coordinates of this node inside a {@link Point}.
	 * @return
	 */
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
