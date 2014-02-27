package vic.rpg.level.path;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.Level;
import vic.rpg.level.entity.Entity;
import vic.rpg.utils.Utils;

/**
 * A NodeMap is a grid of {@link Node Nodes}.
 * @author Victorious3
 */
public class NodeMap implements Cloneable 
{
	public int width, height;
	public Node[][] nodes;	
	public Level level;
	
	public NodeMap(Level level)
	{
		recreate(level);
	}
	
	/**
	 * Recreates this NodeMap with a given {@link Level}. It checks weather a {@link Node} is
	 * blocked because the collision box of an {@link Entity} intersects with it.
	 * @param level
	 */
	public void recreate(Level level)
	{	
		this.width = level.width;
		this.height = level.height;
		
		Node[][] nodes = new Node[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				nodes[i][j] = new Node(i, j);
			}
		}
		
		int size = Level.CELL_SIZE / 2;
		
		for(Entity e : level.entityMap.values())
		{
			Area a = e.getCollisionBoxes(new Area());
			Rectangle r = a.getBounds();
			
			for(int i = r.x; i <= r.x + r.width; i += size / 2)
			{				
				for(int j = r.y; j <= r.y + r.height; j += size / 2)
				{
					a = e.getCollisionBoxes(new Area());
					Area a2 = new Area(new Polygon(new int[]{i - Level.CELL_SIZE / 2, i, i + Level.CELL_SIZE / 2, i, i + Level.CELL_SIZE / 2}, new int[]{j + Level.CELL_SIZE / 4, j, j + Level.CELL_SIZE / 4, j + Level.CELL_SIZE / 2, j + Level.CELL_SIZE / 4}, 4));
					a.intersect(a2);
					if(!a.isEmpty())
					{						
						Point p2 = Utils.convIsoToCart(new Point(i, j));
						int x = p2.x / size;
						int y = p2.y / size;
						
						if(x > 0 && x < width && y > 0 && y < height)
						{
							Node n = nodes[x][y];
							n.isBlocked = true;
							nodes[x][y] = n;
						}
					}
				}
			}
		}
		
		this.nodes = nodes;
		this.level = level;
	}

	@Override
	protected NodeMap clone()
	{
		try {
			return (NodeMap) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
