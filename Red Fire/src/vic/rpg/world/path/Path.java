package vic.rpg.world.path;

import java.util.ArrayList;

import vic.rpg.world.tile.Tile;

/**
 * A Path is a set of {@link Node Nodes} representing the shortest Path from the begin {@link Node} to the end {@link Node}.
 * @author Victorious3
 *
 */
public class Path
{
	protected ArrayList<Node> path = new ArrayList<Node>();
	private int pointer = 1;
	
	public Path(NodeMap nodeMap, Node begin, Node end, double maxCost)
	{
		this.nodeMap = nodeMap;
		this.begin = begin;
		this.end = end;
		this.maxCost = maxCost;
	}
	
	private NodeMap nodeMap;
	private Node begin;
	private Node end;
	private double maxCost;
	
	/**
	 * Indicates if this Path was computed already.
	 */
	public boolean isReady = false;
	public boolean isPossible = true;
	
	/**
	 * Returns the next {@link Node} from the Path.
	 * @return
	 */
	public Node next()
	{		
		if(pointer > path.size()) return null;
		Node n = path.get(getLenght() - pointer);
		pointer += 1;
		
		return n;
	}
	
	/**
	 * Checks if the Path has a {@link Node} left to walk onto.
	 * @return
	 */
	public boolean hasNext()
	{
		return pointer + 1 <= getLenght();
	}
	
	/**
	 * Resets the pointer of this Path to 1.
	 */
	public void revert()
	{
		pointer = 1;
	}
	
	/**
	 * Returns the length of this Path.
	 * @return
	 */
	public int getLenght()
	{
		return path.size();
	}
	
	/**
	 * A* Algorithm.
	 * @return
	 */
	public boolean compute()
	{
		NodeMap map = nodeMap.clone();
		
		if(isNodeBlocked(end, map) || isNodeBlocked(begin, map)) return false;
		
		ArrayList<Node> openList = new ArrayList<Node>();
		ArrayList<Node> closedList = new ArrayList<Node>();
		openList.add(begin);
		
		double cost = 0;
		Node parent = begin;		
		boolean reachedEnd = false;
		
		while(cost < maxCost && openList.size() > 0)
		{
			if(parent.equals(end))
			{
				reachedEnd = true;
				break;
			}
			else
			{
				openList.remove(parent);
				closedList.add(parent);
				
				for(Node n : getNeighbors(parent, map))
				{
					if(openList.contains(n))
					{
						double g = calculateG(parent, n, map); 
						if(n.g > g)
						{
							n.parent = parent;
							n.g = g;
						}
					}
					else if(!closedList.contains(n))
					{
						n.g = calculateG(parent, n, map);
						n.parent = parent;
						openList.add(n);
					}
				}
			}
			
			double f = Double.MAX_VALUE;
			
			for(Node n : openList)
			{
				double f2 = calculateF(begin, n.parent, n, map);
				if(f2 < f)
				{
					parent = n;
					f = f2;
				}
			}
			
			cost = f;
		}
		
		if(reachedEnd)
		{
			Node n = parent;
			
			while(!n.equals(begin))
			{
				path.add(n);
				n = n.parent;
			}
			return true;
		}
		return false;
	}
	
	private static double calculateG(Node parent, Node node, NodeMap map)
	{
		double f = 0;	
		f += Math.abs(parent.x - node.x) + Math.abs(parent.y - node.y);
		f += parent.g;
		f += map.map.getTileAt(node.x, node.y, 0).getMovementCost(node.x, node.y, 0, map.map);		
		return f;
	}
	
	private static double calculateH(Node begin, Node node, NodeMap map)
	{
		double f = 0;
		f += Math.sqrt(Math.pow((begin.x - node.x), 2) + Math.pow((begin.y - node.y), 2));		
		return f;
	}
		
	private static double calculateF(Node begin, Node parent, Node node, NodeMap map)
	{
		double f = 0;
		
		f += calculateG(parent, node, map);
		f += calculateH(begin, node, map);
		
		return f;
	}
	
	private static ArrayList<Node> getNeighbors(Node n, NodeMap map)
	{
		ArrayList<Node> neighbors = new ArrayList<Node>();
		
		if(n.x + 1 < map.width)
		{
			if(!isNodeBlocked(map.nodes[n.x + 1][n.y], map)) neighbors.add(map.nodes[n.x + 1][n.y]);
			if(n.y + 1 < map.height)
			{
				if(!isNodeBlocked(map.nodes[n.x + 1][n.y + 1], map)) neighbors.add(map.nodes[n.x + 1][n.y + 1]);
			}
		}
		if(n.x - 1 >= 0)
		{
			if(!isNodeBlocked(map.nodes[n.x - 1][n.y], map)) neighbors.add(map.nodes[n.x - 1][n.y]);
			if(n.y - 1 >= 0)
			{
				if(!isNodeBlocked(map.nodes[n.x - 1][n.y - 1], map)) neighbors.add(map.nodes[n.x - 1][n.y - 1]);
			}
		}
		if(n.y + 1 < map.height)
		{
			if(!isNodeBlocked(map.nodes[n.x][n.y + 1], map)) neighbors.add(map.nodes[n.x][n.y + 1]);
			if(n.x - 1 >= 0)
			{
				if(!isNodeBlocked(map.nodes[n.x - 1][n.y + 1], map)) neighbors.add(map.nodes[n.x - 1][n.y + 1]);
			}
		}
		if(n.y - 1 >= 0)
		{
			if(!isNodeBlocked(map.nodes[n.x][n.y - 1], map)) neighbors.add(map.nodes[n.x][n.y - 1]);
			if(n.x + 1 < map.width)
			{
				if(!isNodeBlocked(map.nodes[n.x + 1][n.y - 1], map)) neighbors.add(map.nodes[n.x + 1][n.y - 1]);
			}
		}		
		return neighbors;
	}
	
	/**
	 * Checks weather a node can be walked onto.
	 * @param n
	 * @param map
	 * @return
	 */
	public static boolean isNodeBlocked(Node n, NodeMap map)
	{
		if(n.isBlocked) return true;
		if(!isWalkingPermitted(n.x, n.y, map)) return true;
		return false;
	}
	
	private static boolean isWalkingPermitted(int x, int y, NodeMap map)
	{
		for(Tile t : map.map.getTilesAt(x, y))
		{
			if(t == null) continue;
			if(!t.isWalkingPermitted(x, y, 0, map.map)) return false;
		}
		return true;
	}
}
