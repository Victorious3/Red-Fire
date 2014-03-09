package vic.rpg.level.entity;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.jnbt.CompoundTag;

import vic.rpg.Game;
import vic.rpg.level.Editable;
import vic.rpg.level.INBTReadWrite;
import vic.rpg.level.Level;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.level.entity.living.EntityPlayer;
import vic.rpg.level.tiles.Tile;
import vic.rpg.listener.EntityEventListener;
import vic.rpg.render.Drawable;
import vic.rpg.render.LightSource;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet11EntityInteraction;
import vic.rpg.server.packet.Packet12Event;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

/**
 * An Entity is any object in a level that is not a {@link Tile}. Mostly used for moving objects like {@link EntityPlayer}.
 * @author Victorious3
 */
public abstract class Entity extends Drawable implements Cloneable, INBTReadWrite, EntityEventListener
{
	@Editable public int xCoord = 0;
	@Editable public int yCoord = 0;
	
	/**
	 * The UUID is an {@link UUID} that allows for unique indexing of the Entities.
	 */
	@Editable public String UUID;
	public int id = 0;
	
	public ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
	public Level levelObj;
	public ArrayList<EntityEventListener> entityListeners = new ArrayList<EntityEventListener>(Arrays.asList(new EntityEventListener[]{this}));
	
	/**
	 * Used to deploy a new {@link EntityEvent} unique to this Entity.
	 * Returns if the {@link EntityEvent} was cancelled by one Listener.
	 * @param eev
	 */
	public void postEvent(EntityEvent eev)
	{
		for(EntityEventListener el : entityListeners)
		{
			eev = el.onEventPosted(eev);
			if(eev.isCancelled()) return;
		}
		
		if(Utils.getSide() == eev.side || eev.side == Side.BOTH)
		{
			for(EntityEventListener el : entityListeners) el.onEventPosted(eev);
			for(EntityEventListener el : entityListeners) el.onEventReceived(eev);
		}
		if(Utils.getSide() != eev.side || eev.side == Side.OTHER_SIDE || eev.side == Side.BOTH)
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
	
	/**
	 * Cycles through all {@link EntityEventListener} bound to this Entity and calls {@link EntityLiving#onEventReceived(EntityEvent)}.
	 * Returns if the {@link EntityEvent} was cancelled by one Listener.
	 * @param eev
	 */
	public void processEvent(EntityEvent eev)
	{
		for(EntityEventListener el : entityListeners) 
		{
			eev = el.onEventReceived(eev);
			if(eev.isCancelled()) return;
		}
	}
	
	/**
	 * Adds an {@link EntityEventListener} to let him receive {@link EntityEvent EntityEvents}.
	 * @param eel
	 */
	public void addEventListener(EntityEventListener eel)
	{
		entityListeners.add(eel);
		Collections.sort(entityListeners, Priority.entityEventListenerComperator);
	}
	
	/**
	 * Removes an {@link EntityEventListener}.
	 * @param eel
	 */
	public void removeEventListener(EntityEventListener eel)
	{
		entityListeners.remove(eel);
		Collections.sort(entityListeners, Priority.entityEventListenerComperator);
	}
	
	@Override
	public Entity clone()
	{
		return (Entity) super.clone();
	}

	protected Entity(int width, int height) 
	{
		super(width, height);
	}
	
	/**
	 * Returns the position of a {@link LightSource} for rendering.
	 * @param l
	 * @return
	 */
	public Point getLightPosition(LightSource l)
	{
		return new Point(this.xCoord, this.yCoord);
	}

	/**
	 * Called every 0.2 seconds.
	 */
	public void tick() {}
	
	@Override public void readFromNBT(CompoundTag tag, Object... args) {}
	
	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		return tag;
	}
	
	/**
	 * Gets the collision box of an Entity represented by an {@link Area}.
	 * @param area
	 * @return
	 */
	public Area getCollisionBoxes(Area area)
	{
		return area;	
	}
	
	/**
	 * Called when the user mouse hovered over this Entity. (Isometric coordinates)
	 * @param x
	 * @param y
	 * @param entity
	 */
	public void onMouseHovered(int x, int y, EntityPlayer entity){}
	
	/**
	 * Called when the user clicked on this Entity. (Isometric coordinates)
	 * @param x
	 * @param y
	 * @param entity
	 * @param mouseEvent
	 */
	public void onMouseClicked(int x, int y, EntityPlayer entity, int mouseEvent)
	{
		if(Utils.getSide() == Side.CLIENT)
		{
			Game.packetHandler.addPacketToSendingQueue(new Packet11EntityInteraction(this, Packet11EntityInteraction.MODE_ONCLICK, x, y, mouseEvent));
		}
	}

	public void onKeyPressed(KeyEvent key){}
	
	/**
	 * Returns the position of this Entity in tiled space.
	 * @return Point
	 */
	public Point getTiledPosition()
	{
		int x = (int)((float)(this.xCoord) / (float)Level.CELL_SIZE * 2);
		int y = (int)((float)(this.yCoord) / (float)Level.CELL_SIZE * 2);
		
		return new Point(x, y);
	}
	
	/**
	 * Returns the render offset to center the Entity at its bottom point in
	 * Isometric Coordinates
	 * @return
	 */
	public Dimension getRenderOffset()
	{
		return new Dimension(getWidth() / 2, getHeight());
	}
	
	/**
	 * Calculates if this Entity collides with any other Entities or impassable Tiles.
	 * @param level
	 * @return
	 */
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
		
		Point p = getTiledPosition();
		if(p.x < 0 || p.y < 0 || p.x >= level.width || p.y >= level.height) return true;
		
		Tile[] tiles = level.getTilesAt(p.x, p.y);		
		for(Tile t : tiles)
		{
			if(t == null) continue;
			if(!t.isWalkingPermitted()) return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the displayed name of this Entity.
	 * @return
	 */
	public String getName()
	{
		return "Entity";
	}

	@Override
	public EntityEvent onEventReceived(EntityEvent e) 
	{
		return e;
	}

	@Override
	public EntityEvent onEventPosted(EntityEvent e) 
	{
		return e;
	}

	@Override
	public Priority getPriority() 
	{
		return Priority.PRIORITY_LEAST;
	}
}
