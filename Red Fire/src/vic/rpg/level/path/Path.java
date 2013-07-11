package vic.rpg.level.path;

import java.util.ArrayList;

import vic.rpg.registry.LevelRegistry;

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
	
	public boolean isReady = false;
	public boolean isPossible = true;
	
	public Node next()
	{		
		if(pointer > path.size()) return null;
		Node n = path.get(getLenght() - pointer);
		pointer += 1;
		
		return n;
	}
	
	public boolean hasNext()
	{
		return pointer + 1 <= getLenght();
	}
	
	public void revert()
	{
		pointer = 1;
	}
	
	public int getLenght()
	{
		return path.size();
	}
	
	public boolean compute()
	{
		NodeMap map = nodeMap.clone();
		
		if(end.isBlocked || begin.isBlocked) return false;
		
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
		f += LevelRegistry.tileRegistry.get(map.level.worldobjects[node.x][node.y][0]).getMovementCost();		
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
	
	private static boolean isNodeBlocked(Node n, NodeMap map)
	{
		if(n.isBlocked) return true;
		if(!LevelRegistry.tileRegistry.get(map.level.worldobjects[n.x][n.y][0]).isWalkingPermitted()) return true;
		return false;
	}
}
