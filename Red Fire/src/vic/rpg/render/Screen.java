package vic.rpg.render;

import java.awt.Graphics2D;

import vic.rpg.Game;
import vic.rpg.gui.Gui;
import vic.rpg.registry.GameRegistry;
import vic.rpg.server.packet.Packet9EntityMoving;

public class Screen extends Render 
{	
	public static int xOffset = 0;
	public static int yOffset = 0;
	
	public Screen(int width, int height)
	{
		super(width, height);
	}

	@Override
	public void render(Graphics2D g2d) 
	{
		if(Game.level != null)
		{			
			resetImage();
			Game.level.render(g2d);
			draw(Game.level, xOffset, yOffset);
		}
	}
	
	int tickCounter = 0;
	public void tick()
	{
		if(Game.thePlayer != null && !Game.thePlayer.isWalkingBlocked)
		{			
			Game.thePlayer.isWalking = false;
		
			if(!Gui.currentGui.pauseGame)
			{
				if(GameRegistry.key.APressed) 
				{				
					Game.thePlayer.xCoord -= 2;
					if(!Game.thePlayer.collides(Game.level))
					{
						Game.thePlayer.setRotation(1);
						Game.thePlayer.isWalking = true;
						Screen.xOffset += 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.thePlayer));
					}
					else Game.thePlayer.xCoord += 2; 
				}
				if(GameRegistry.key.WPressed) 
				{			
					Game.thePlayer.yCoord -= 2;
					if(!Game.thePlayer.collides(Game.level))
					{
						Game.thePlayer.setRotation(3);
						Game.thePlayer.isWalking = true;
						Screen.yOffset += 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.thePlayer));
					}
					else Game.thePlayer.yCoord += 2; 
				}
				if(GameRegistry.key.SPressed) 
				{				
					Game.thePlayer.yCoord += 2;
					if(!Game.thePlayer.collides(Game.level))
					{
						Game.thePlayer.setRotation(0);
						Game.thePlayer.isWalking = true;
						Screen.yOffset -= 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.thePlayer));
					}
					else Game.thePlayer.yCoord -= 2; 
				}
				if(GameRegistry.key.DPressed) 
				{					
					Game.thePlayer.xCoord += 2;
					if(!Game.thePlayer.collides(Game.level))
					{
						Game.thePlayer.setRotation(2);
						Game.thePlayer.isWalking = true;
						Screen.xOffset -= 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.thePlayer));
					}
					else Game.thePlayer.xCoord -= 2; 
				}
			}
		}
	}
	
	public void postRender(Graphics2D g2d)
	{
        if(Game.level != null) Game.level.drawLightMap(g2d);
        
		if(Gui.currentGui != null)Gui.currentGui.render(g2d);    
	}
}
