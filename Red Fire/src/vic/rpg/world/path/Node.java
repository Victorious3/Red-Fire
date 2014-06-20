package vic.rpg.world.path;

import java.awt.Point;

import vic.rpg.world.Map;
import vic.rpg.world.entity.living.EntityLiving;
import vic.rpg.world.tile.Tile;

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
	 * Creates a new Node from a {@link Point}. (Cartesian)
	 * @param p
	 * @return
	 */
	public static Node fromPoint(Point p)
	{
		Node n = new Node();
		n.x = (int) ((float)p.x / (float)(Map.CELL_SIZE / 2));
		n.y = (int) ((float)p.y / (float)(Map.CELL_SIZE / 2));
		return n;
	}
	
	/**
	 * Returns the coordinates of this node inside a {@link Point}. (Cartesian)
	 * @return
	 */
	public Point toPoint()
	{
		int x2 = x * (Map.CELL_SIZE / 2);
		int y2 = y * (Map.CELL_SIZE / 2);
		
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
