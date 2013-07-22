package vic.rpg.level;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Comparator;

import org.jnbt.CompoundTag;

import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.render.LightSource;
import vic.rpg.render.Render;
import vic.rpg.utils.Utils;

public class Entity extends Render implements Cloneable
{
	@Editable public int xCoord = 0;
	@Editable public int yCoord = 0;
	
	public String UUID;
	public int id = 0;
	@Editable public int zLevel = 0;
	
	public ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
	public Level levelObj;
	
	@Override
	public Entity clone()
	{
		return (Entity) super.clone();
	}

	public Entity(int width, int height) 
	{
		super(width, height);
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.initRender();
	}
	
	public Point getLightPosition(LightSource l)
	{
		return new Point(this.xCoord, this.yCoord);
	}
	
	public Entity(int width, int height, int xCoord, int yCoord) 
	{
		super(width, height);
		
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		if(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.initRender();
	}

	public void initRender() {}
	
	public void tick() {}
	
	public void readFromNBT(CompoundTag tag) {}
	
	public CompoundTag writeToNBT(CompoundTag tag) 
	{
		return tag;
	}
	
	public Area getCollisionBoxes(Area area)
	{
		return area;	
	}
	
	public void onMouseHovered(int x, int y, EntityPlayer entity){}
	
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent){}

	public void onKeyPressed(KeyEvent key){}
	
	public boolean collides(Level level)
	{
		for(Entity ent : level.entities.values())
		{
			if(ent == this) continue;
			
			boolean collides = false;
			
			Area a1 = ent.getCollisionBoxes(new Area());
			Area a2 = this.getCollisionBoxes(new Area());
			
			a1.intersect(a2);
			
			if(!a1.isEmpty())
			{
				collides = true;
			}
			
			if(ent instanceof EntityStatic && collides)
			{
				if(((EntityStatic)ent).canEntityWalkOnto(this))
				{
					((EntityStatic)ent).onEntityWalkingOnto(xCoord, yCoord, this);
					return false;
				}
			}
			
			if(collides) return true;
		}
		return false;
	}
	
	public static class EntityComperator implements Comparator<Entity>
	{
		@Override
		public int compare(Entity arg0, Entity arg1) 
		{
			if(arg0.zLevel > arg1.zLevel) return  1;
			if(arg0.zLevel < arg1.zLevel) return -1;
			
			return 0;
		}		
	}
	
	public String getName()
	{
		return "Entity";
	}
}
