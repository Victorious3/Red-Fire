package vic.rpg.level;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jnbt.CompoundTag;

import vic.rpg.Game;
import vic.rpg.level.entity.EntityEvent;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.listener.EntityEventListener;
import vic.rpg.render.Drawable;
import vic.rpg.render.LightSource;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet11EntityInteraction;
import vic.rpg.server.packet.Packet12Event;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

public class Entity extends Drawable implements Cloneable, INBTReadWrite, EntityEventListener
{
	@Editable public int xCoord = 0;
	@Editable public int yCoord = 0;
	
	@Editable public String UUID;
	public int id = 0;
	
	public ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
	public Level levelObj;
	public ArrayList<EntityEventListener> entityListeners = new ArrayList<EntityEventListener>(Arrays.asList(new EntityEventListener[]{this}));
	
	public void postEvent(EntityEvent eev)
	{
		if(Utils.getSide() == eev.side || eev.side == Side.BOTH)
		{
			for(EntityEventListener el : entityListeners) el.onEventPosted(eev);
			for(EntityEventListener el : entityListeners) el.onEventReceived(eev);
		}
		if(Utils.getSide() != eev.side || eev.side == Side.BOTH)
		{
			if(Utils.getSide() == Side.CLIENT)
			{
				Game.packetHandler.addPacketToSendingQueue(new Packet12Event(eev, this));
			}
			if(Utils.getSide() == Side.SERVER)
			{
				Server.server.broadcast(new Packet12Event(eev, this));
			}
		}	
	}
	
	public void processEvent(EntityEvent eev)
	{
		for(EntityEventListener el : entityListeners) el.onEventReceived(eev);
	}
	
	public void addEventListener(EntityEventListener eel)
	{
		entityListeners.add(eel);
		Collections.sort(entityListeners, Priority.entityEventListenerComperator);
	}
	
	@Override
	public Entity clone()
	{
		return (Entity) super.clone();
	}

	public Entity(int width, int height) 
	{
		super(width, height);
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
	}

	public void tick() {}
	
	@Override public void readFromNBT(CompoundTag tag, Object... args) {}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		return tag;
	}
	
	public Area getCollisionBoxes(Area area)
	{
		return area;	
	}
	
	public void onMouseHovered(int x, int y, EntityPlayer entity){}
	
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent)
	{
		if(Utils.getSide() == Side.CLIENT)
		{
			Game.packetHandler.addPacketToSendingQueue(new Packet11EntityInteraction(this, Packet11EntityInteraction.MODE_ONCLICK, x, y, mouseEvent));
		}
	}

	public void onKeyPressed(KeyEvent key){}
	
	public boolean collides(Level level)
	{
		for(Entity ent : level.entityMap.values())
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
		
		//TODO Not quite precise, this is using a single point rather than using an area.
		Point p = Utils.convCartToIso(new Point(this.xCoord, this.yCoord));
		p.x += this.getWidth() / 2;
		p.y += this.getHeight();
		Point p2 = Utils.convIsoToCart(p);
		int x = (int)((float)(p2.x) / (float)Level.CELL_SIZE * 2);
		int y = (int)((float)(p2.y) / (float)Level.CELL_SIZE * 2);
		
		if(x < 0 || y < 0 || x >= level.width || y >= level.height) return true;
		
		Tile[] tiles = level.getTilesAt(x, y);		
		for(Tile t : tiles)
		{
			if(t == null) continue;
			if(!t.isWalkingPermitted()) return true;
		}
		
		return false;
	}
	
	public String getName()
	{
		return "Entity";
	}

	@Override
	public void onEventReceived(EntityEvent e) {}

	@Override
	public void onEventPosted(EntityEvent e) {}

	@Override
	public Priority getPriority() 
	{
		return Priority.PRIORITY_LEAST;
	}
}
