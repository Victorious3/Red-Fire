package vic.rpg.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import vic.rpg.Game;
import vic.rpg.server.Server;
import vic.rpg.server.packet.Packet12Event;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;
import vic.rpg.world.entity.living.EntityLiving;

//TODO Test if this causes memory leaks.
public class EventBus 
{
	private static HashMap<String, EventBus> eventBusMapClient = new HashMap<String, EventBus>();
	private static HashMap<String, EventBus> eventBusMapServer = new HashMap<String, EventBus>();
	
	private ArrayList<EventListener> entityListeners = new ArrayList<EventListener>();
	private final IEventReceiver eventReceiver;
	
	public EventBus(IEventReceiver eventReceiver)
	{
		this.eventReceiver = eventReceiver;
		addEventListener(eventReceiver);
		addEventBus(this);
	}
	
	/**
	 * Used to deploy a new {@link Event} unique to the {@link IEventReceiver}.
	 * Returns if the {@link Event} was cancelled by one Listener.
	 * @param event
	 */
	public void postEvent(Event event)
	{
		for(EventListener el : entityListeners)
		{
			event = el.onEventPosted(event);
			if(event.isCancelled()) return;
		}
		
		if(Utils.getSide() == event.side || event.side == Side.BOTH)
		{
			for(EventListener el : entityListeners) el.onEventPosted(event);
			for(EventListener el : entityListeners) el.onEventReceived(event);
		}
		if(Utils.getSide() != event.side || event.side == Side.OTHER_SIDE || event.side == Side.BOTH)
		{
			if(Utils.getSide() == Side.CLIENT)
			{
				Game.packetHandler.addPacketToSendingQueue(new Packet12Event(event, eventReceiver));
			}
			if(Utils.getSide() == Side.SERVER)
			{
				Server.server.broadcastLocally(eventReceiver.getDimension(), new Packet12Event(event, eventReceiver));
			}
		}	
	}
	
	/**
	 * Cycles through all {@link EventListener} bound to this EventBus and calls {@link EntityLiving#onEventReceived(Event)}.
	 * Returns if the {@link Event} was cancelled by one Listener.
	 * @param eev
	 */
	public void processEvent(Event eev)
	{
		for(EventListener el : entityListeners) 
		{
			eev = el.onEventReceived(eev);
			if(eev.isCancelled()) return;
		}
	}
	
	/**
	 * Adds an {@link EventListener} to let him receive {@link Event Events}.
	 * @param eel
	 */
	public void addEventListener(EventListener eel)
	{
		entityListeners.add(eel);
		Collections.sort(entityListeners, Priority.entityEventListenerComperator);
	}
	
	/**
	 * Removes an {@link EventListener}.
	 * @param eel
	 */
	public void removeEventListener(EventListener eel)
	{
		entityListeners.remove(eel);
		Collections.sort(entityListeners, Priority.entityEventListenerComperator);
	}
	
	public static void clearServer()
	{
		eventBusMapServer.clear();
	}
	
	public static void clearClient()
	{
		eventBusMapClient.clear();
	}
	
	public static void processEventPacket(Packet12Event eventPacket)
	{
		if(Utils.getSide() == Side.SERVER) eventBusMapServer.get(eventPacket.UUID).processEvent(eventPacket.eev);
		else eventBusMapClient.get(eventPacket.UUID).processEvent(eventPacket.eev);
	}
	
	public static void removeEventBus(String uuid)
	{
		if(Utils.getSide() == Side.SERVER) eventBusMapServer.remove(uuid);
		else eventBusMapClient.remove(uuid);
	}
	
	public static void addEventBus(EventBus bus)
	{
		if(Utils.getSide() == Side.SERVER) eventBusMapServer.put(bus.eventReceiver.getDimension() + "_" + bus.eventReceiver.getUniqueIdentifier(), bus);
		else eventBusMapClient.put(bus.eventReceiver.getDimension() + "_" + bus.eventReceiver.getUniqueIdentifier(), bus);
	}
}
