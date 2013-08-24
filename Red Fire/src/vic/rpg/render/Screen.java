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
		if(Game.thePlayer != null && !Game.thePlayer.isWalkingBlocked)
		{						
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
				else
				{
					if(Game.thePlayer.isWalking())
					{
						Game.thePlayer.setWalking(false);
						Game.packetHandler.addPacketToSendingQueue(new Packet9EntityMoving(Game.thePlayer));
					}
				}
			}
		}
	}

	public void postRender(GL2 gl2)
	{
		/*Composite c = g2d.getComposite();
		g2d.setComposite(BlendComposite.Multiply);
		
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
			g2d.drawImage(bLight, null, 0, 0);
		}
		g2d.setComposite(c);*/
		if(Gui.currentGui != null) Gui.currentGui.render(gl2);    
	}
}
