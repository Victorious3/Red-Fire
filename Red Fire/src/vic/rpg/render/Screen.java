package vic.rpg.render;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import vic.rpg.Game;
import vic.rpg.gui.Gui;
import vic.rpg.level.Entity;
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
		}
	}
	
	int tickCounter = 0;
	public void tick()
	{
		if(Game.thePlayer != null && !Game.thePlayer.isWalkingBlocked)
		{			
			Game.thePlayer.setWalking(false);
		
			if(!Gui.currentGui.pauseGame)
			{
				if(GameRegistry.key.APressed) 
				{				
					Game.thePlayer.xCoord -= 2;
					if(!Game.thePlayer.collides(Game.level))
					{						
						Game.thePlayer.setWalking(true);
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
						Game.thePlayer.setWalking(true);
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
						Game.thePlayer.setWalking(true);
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
						Game.thePlayer.setWalking(true);
						Screen.xOffset -= 2;
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.thePlayer));
					}
					else Game.thePlayer.xCoord -= 2; 
				}
				
				if(GameRegistry.key.APressed) 
				{
					Game.thePlayer.setRotation(1);
				}
				else if(GameRegistry.key.WPressed) 
				{
					Game.thePlayer.setRotation(3);
				}
				else if(GameRegistry.key.SPressed) 
				{
					Game.thePlayer.setRotation(0);
				}
				else if(GameRegistry.key.DPressed) 
				{
					Game.thePlayer.setRotation(2);
				}
			}
		}
	}
	
	public void postRender(Graphics2D g2d)
	{
		Composite c = g2d.getComposite();
		if(Game.level != null)
		{					
			BufferedImage bLight = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d2 = (Graphics2D) bLight.getGraphics();
			g2d2.setColor(Color.darkGray);
			g2d2.fillRect(0, 0, bLight.getWidth(), bLight.getHeight());
			
			for(Entity e : Game.level.entities.values())
			{
				for(LightSource s : e.lightSources)
				{
					Point p = e.getLightPosition(s);
					if(p.x + s.width >= -xOffset && p.x <= -xOffset + Game.WIDTH && p.y + s.width >= -yOffset && p.y <= -yOffset + Game.HEIGHT)
					{	
						g2d2.drawImage(s.getImage(), p.x + xOffset - s.width / 2, p.y + yOffset - s.width / 2, null);				
					}
				}
			}
//			g2d.setComposite(BlendComposite.Multiply);
//			g2d.drawImage(bLight, null, 0, 0);
		}
		
		g2d.setComposite(c);
		if(Gui.currentGui != null)Gui.currentGui.render(g2d);    
	}
}
