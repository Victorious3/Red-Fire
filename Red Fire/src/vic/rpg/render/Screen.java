package vic.rpg.render;

import java.awt.Color;
import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.config.Options;
import vic.rpg.gui.Gui;
import vic.rpg.level.Entity;
import vic.rpg.level.Tile;
import vic.rpg.registry.GameRegistry;
import vic.rpg.server.packet.Packet9EntityMoving;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;

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
			Game.level.render(gl2);			
		}
	}
	
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
				
				if(GameRegistry.key.APressed && GameRegistry.key.WPressed) 
				{
					Game.getPlayer().setRotation(Direction.NORTH);
				}
				else if(GameRegistry.key.WPressed && GameRegistry.key.DPressed) 
				{
					Game.getPlayer().setRotation(Direction.EAST);
				}
				else if(GameRegistry.key.DPressed && GameRegistry.key.SPressed) 
				{
					Game.getPlayer().setRotation(Direction.SOUTH);
				}
				else if(GameRegistry.key.SPressed && GameRegistry.key.APressed) 
				{
					Game.getPlayer().setRotation(Direction.WEST);
				}
				else if(GameRegistry.key.APressed) 
				{
					Game.getPlayer().setRotation(Direction.NORTH_WEST);
				}
				else if(GameRegistry.key.WPressed) 
				{
					Game.getPlayer().setRotation(Direction.NORTH_EAST);
				}
				else if(GameRegistry.key.SPressed) 
				{
					Game.getPlayer().setRotation(Direction.SOUTH_WEST);
				}
				else if(GameRegistry.key.DPressed) 
				{
					Game.getPlayer().setRotation(Direction.SOUTH_EAST);
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
	
	private Color getAmbientLight()
	{
		if(Game.level.isAmbientLighting)
		{			
			float time = Game.level.time;
			if(time > 5000) time = 10000 - time;
			return new Color((int)((255F / 5000F) * time), (int)((255F / 5000F) * time), (int)((255F / 5000F) * time));
		}
		else return Color.white;
	}
	
	private int textureID = 0;
	private int frameBufferID = 0;
	
	public void init(GL2 gl2)
	{
		int[] params1 = new int[1];
		gl2.glGenTextures(1, params1, 0);
		textureID = params1[0];
		gl2.glBindTexture(GL.GL_TEXTURE_2D, textureID);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl2.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, Game.RES_WIDTH, Game.RES_HEIGHT, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, null);
		
		int[] params2 = new int[1];
		gl2.glGenFramebuffers(1, params2, 0);
		frameBufferID = params2[0];
		gl2.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, frameBufferID);
		gl2.glFramebufferTexture2D(GL2.GL_DRAW_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, textureID, 0);
		
		gl2.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, 0);
	}

	public void postRender(GL2 gl2)
	{
		if(Game.level != null && Options.LIGHTING)
		{
			DrawUtils.setGL(gl2);
			
			gl2.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, frameBufferID);
			gl2.glFramebufferTexture2D(GL2.GL_DRAW_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, textureID, 0);
			
			DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, getAmbientLight()); 
			
			gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_DST_ALPHA);	
									
			for(Entity e : Game.level.entityMap.values())
			{
				for(LightSource s : e.lightSources)
				{
					Point p1 = e.getLightPosition(s);
					Point p2 = Utils.convCartToIso(new Point(p1.x + xOffset, p1.y + yOffset));
					s.draw(gl2, p2.x, Game.HEIGHT - p2.y);				
				}
			}
			
			for(int x = 0; x < Game.level.width; x++)
			{
				for(int y = 0; y < Game.level.height; y++)
				{				
					for(int i = 0; i < Game.level.getLayerAmount(); i++)
					{
						Integer data = Game.level.getTileDataAt(x, y, i);
						Tile tile = Game.level.getTileAt(x, y, i);
						if(tile != null)
						{
							if(tile.emitsLight(x, y, data))
							{
								LightSource ls = tile.getLightSource(x, y, data);
								Point p1 = tile.getLightPosition(x, y, data);
								Point p2 = Utils.convCartToIso(new Point(p1.x + xOffset, p1.y + yOffset));
								ls.draw(gl2, p2.x, Game.HEIGHT - p2.y);
							}
						}
					}
				}		
			}
			
			gl2.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
			gl2.glBlendFunc(GL2.GL_DST_COLOR, GL2.GL_ONE_MINUS_SRC_ALPHA);
			
			gl2.glEnable(GL2.GL_TEXTURE_2D);
			gl2.glPushMatrix();
			gl2.glColor3f(1.0F, 1.0F, 1.0F);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
			gl2.glBegin(GL2.GL_QUADS);
			gl2.glNormal3i(0, 0, 1);
	        gl2.glTexCoord2i(0, 0);
	        gl2.glVertex2i(0, 0);
	        gl2.glTexCoord2i(1, 0);
	        gl2.glVertex2i(0 + Game.WIDTH, 0);
	        gl2.glTexCoord2i(1, 1);
	        gl2.glVertex2i(Game.WIDTH, Game.HEIGHT);
	        gl2.glTexCoord2i(0, 1);
	        gl2.glVertex2i(0, Game.HEIGHT);
	        gl2.glEnd();
			gl2.glPopMatrix();
			gl2.glDisable(GL2.GL_TEXTURE_2D);
			
			gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);		
		}
    	if(Gui.currentGui != null) Gui.currentGui.render(gl2);
	}
}
