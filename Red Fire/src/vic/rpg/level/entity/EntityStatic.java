package vic.rpg.level.entity;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * An EntityStatic is a none movable Entity.
 * @author Victorious3
 *
 */
public abstract class EntityStatic extends Entity
{
	protected EntityStatic(int width, int height) 
	{
		super(width, height);		
	}
	
	public void onEntityWalkingOnto(int x, int y, Entity entity){}
	
	public boolean canEntityWalkOnto(Entity entity)
	{
		return false;
	}
	
	public boolean isEntityInRange(Entity e, int radius)
	{
		Area circle = new Area(new Ellipse2D.Float(xCoord - radius, yCoord - radius, radius * 2, radius * 2));
		Area eArea = e.getCollisionBoxes(new Area());
		
		circle.intersect(eArea);
		if(circle.isEmpty()) return false;
		return true;
	}
}
