package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import vic.rpg.Game;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.server.packet.Packet20Chat;

public class GuiIngame extends Gui 
{
	public GTextField chatField = new GTextField(10, Game.HEIGHT - 60, Game.WIDTH - 35, 20, 120, false);
	
	public ArrayList<String> chatValues = new ArrayList<String>();
	public int MAX_CHATLINES = 10;
	
	public static GuiIngame gui = new GuiIngame();
	public static EntityLiving focusedEntity = null;
	
	public GuiIngame() 
	{
		super(false);
		controlsList.add(chatField);
		chatField.isVisible = false;
	}

	@Override
	public void onMouseClickStart(int x, int y, int mouseButton) 
	{
		super.onMouseClickStart(x, y, mouseButton);
		focusedEntity = null;
	}

	@Override
	public void keyTyped(char k, int keyCode) 
	{		
		super.keyTyped(k, keyCode);
		
		if(keyCode == KeyEvent.VK_ENTER)
		{
			if(chatField.isVisible)
			{
				chatField.isVisible = false;
				if(chatField.plaintext.length() > 0)Game.packetHandler.addPacketToSendingQueue(new Packet20Chat(chatField.plaintext, Game.USERNAME));
				Game.thePlayer.isWalkingBlocked = false;
				chatField.clear();
			}
			else
			{
				chatField.isVisible = true;
				Game.thePlayer.isWalkingBlocked = true;
				Game.thePlayer.isWalking = false;
			}
		}
	}
	
	@Override
	public void render(Graphics2D g2d) 
	{
		super.render(g2d);
		g2d.setFont(new Font("Veranda", 0, 20));
		g2d.setColor(Color.white);
		g2d.drawString(Game.fps + " FPS", 5, 20);
		
		g2d.setFont(new Font("Lucida Console", Font.PLAIN, 14));
		
		int stringHeight = 0;
		for(int i = chatValues.size() - 1; i >= 0; i--)
		{
			String s = chatValues.get(i) != null ? chatValues.get(i): " ";
			g2d.drawString(s, 0, 50 + stringHeight * g2d.getFontMetrics().getHeight());
			stringHeight += 1;
		}
		
		if(focusedEntity != null)
		{
			g2d.drawImage(focusedEntity.getShortcutImage(), 200, 0, null);
			g2d.drawString(focusedEntity.getName(), 200, 10);
		}
		
	}
	
	public void addChatMessage(String s, String playername)
	{		
		if(playername.equals("server"))
		{
			s = "{SERVER}: " + s;
		}
		else s = "[" + playername + "]: " + s;
		
		if(chatValues.size() + 1 > MAX_CHATLINES) chatValues.remove(0);
		chatValues.add(s);
	}
}
