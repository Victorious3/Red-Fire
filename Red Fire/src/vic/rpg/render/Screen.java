package vic.rpg.render;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.Gui;
import vic.rpg.registry.GameRegistry;
import vic.rpg.server.packet.Packet9EntityMoving;

public class Screen extends Drawable 
{	
	public static int xOffset = 0;
	public static int yOffset = 0;
	
	public Screen(int width, int height)
	{
		super(width, height);
	}

	@Override
	public void render(GL2 gl2) 
	{
		if(Game.level != null)
		{			
			resetTexture();		
			Game.level.render(gl2);			
		}
	}
	
	int tickCounter = 0;
	public void tick()
	{
		if(Game.getPlayer() != null && !Game.getPlayer().isWalkingBlocked)
		{						
			if(!Gui.currentGui.pauseGame)
			{
				if(GameRegistry.key.APressed) 
				{				
					Game.getPlayer().xCoord -= 2;
					if(!Game.getPlayer().collides(Game.level))
					{						
						Game.getPlayer().setWalking(true);
						Screen.xOffset += 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.getPlayer()));
					}
					else Game.getPlayer().xCoord += 2; 
				}
				if(GameRegistry.key.WPressed) 
				{			
					Game.getPlayer().yCoord -= 2;
					if(!Game.getPlayer().collides(Game.level))
					{
						Game.getPlayer().setWalking(true);
						Screen.yOffset += 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.getPlayer()));
					}
					else Game.getPlayer().yCoord += 2; 
				}
				if(GameRegistry.key.SPressed) 
				{				
					Game.getPlayer().yCoord += 2;
					if(!Game.getPlayer().collides(Game.level))
					{
						Game.getPlayer().setWalking(true);
						Screen.yOffset -= 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.getPlayer()));
					}
					else Game.getPlayer().yCoord -= 2; 
				}
				if(GameRegistry.key.DPressed) 
				{					
					Game.getPlayer().xCoord += 2;
					if(!Game.getPlayer().collides(Game.level))
					{				
						Game.getPlayer().setWalking(true);
						Screen.xOffset -= 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.getPlayer()));
					}
					else Game.getPlayer().xCoord -= 2; 
				}
				
				if(GameRegistry.key.APressed) 
				{
					Game.getPlayer().setRotation(1);
				}
				else if(GameRegistry.key.WPressed) 
				{
					Game.getPlayer().setRotation(3);
				}
				else if(GameRegistry.key.SPressed) 
				{
					Game.getPlayer().setRotation(0);
				}
				else if(GameRegistry.key.DPressed) 
				{
					Game.getPlayer().setRotation(2);
				}
				else
				{
					if(Game.getPlayer().isWalking())
					{
						Game.getPlayer().setWalking(false);
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.getPlayer()));
					}
				}
			}
		}
	}

	public void postRender(GL2 gl2)
	{
		/*if(Game.level != null)
		{						
			for(Entity e : Game.level.entityMap.values())
			{
				for(LightSource s : e.lightSources)
				{
					Point p = e.getLightPosition(s);
					if(p.x + s.width >= -xOffset && p.x <= -xOffset + Game.WIDTH && p.y + s.width >= -yOffset && p.y <= -yOffset + Game.HEIGHT)
					{	
						s.draw(gl2, p.x + xOffset - s.width / 2, p.y + yOffset - s.width / 2);				
					}
				}
			}
		}*/
		if(Gui.currentGui != null) Gui.currentGui.render(gl2);    
	}
}
