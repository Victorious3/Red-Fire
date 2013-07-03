package vic.rpg.level.path;

import java.awt.Rectangle;
import java.awt.geom.Area;

import vic.rpg.level.Entity;
import vic.rpg.level.Level;
import vic.rpg.utils.Utils;

public class ObstacleMap 
{
	public int width, height;
	public Node[][] obstacles;	
	
	public ObstacleMap(Level level)
	{
		recreate(level);
	}
	
	public void recreate(Level level)
	{	
		this.width = level.width;
		this.height = level.height;
		
		Node[][] obstacles = new Node[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				obstacles[i][j] = new Node(i, j);
			}
		}
		
		int size = Level.CELL_SIZE;
		
		for(Entity e : level.entities.values())
		{
			Area a = e.getCollisionBoxes(new Area());
			Rectangle r = a.getBounds();

			for(int i = Utils.rnd(r.x, size); i < r.x + r.width; i += size)
			{				
				for(int j = Utils.rnd(r.y, size); j < r.y + r.height; j += size)
				{
					if(a.intersects(new Rectangle(i, j, size, size)))
					{
						int x = i / size;
						int y = j / size;
						
						if(x > 0 && x < width && y > 0 && y < height)
						{
							Node n = obstacles[x][y];
							n.isBlocked = true;
							obstacles[x][y] = n;
						}
					}
				}
			}
		}
		
		this.obstacles = obstacles;
	}
}
