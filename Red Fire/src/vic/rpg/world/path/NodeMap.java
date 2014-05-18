package vic.rpg.world.path;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.entity.Entity;

/**
 * A NodeMap is a grid of {@link Node Nodes}.
 * @author Victorious3
 */
public class NodeMap implements Cloneable 
{
	public int width, height;
	public Node[][] nodes;	
	public Map map;
	
	public NodeMap(Map map)
	{
		recreate(map);
	}
	
	/**
	 * Recreates this NodeMap with a given {@link Map}. It checks weather a {@link Node} is
	 * blocked because the collision box of an {@link Entity} intersects with it.
	 * @param map
	 */
	public void recreate(Map map)
	{	
		this.width = map.width;
		this.height = map.height;
		
		Node[][] nodes = new Node[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				nodes[i][j] = new Node(i, j);
			}
		}
		
		int size = Map.CELL_SIZE / 2;
		
		for(Entity e : map.entityMap.values())
		{
			Area a = e.getCollisionBoxes(new Area());
			Rectangle r = a.getBounds();
			
			for(int i = r.x; i <= r.x + r.width; i += size / 2)
			{				
				for(int j = r.y; j <= r.y + r.height; j += size / 2)
				{
					a = e.getCollisionBoxes(new Area());
					Area a2 = new Area(new Polygon(new int[]{i - Map.CELL_SIZE / 2, i, i + Map.CELL_SIZE / 2, i, i + Map.CELL_SIZE / 2}, new int[]{j + Map.CELL_SIZE / 4, j, j + Map.CELL_SIZE / 4, j + Map.CELL_SIZE / 2, j + Map.CELL_SIZE / 4}, 4));
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
		this.map = map;
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
