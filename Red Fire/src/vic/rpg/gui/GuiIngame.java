package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.render.DrawUtils;
import vic.rpg.server.packet.Packet20Chat;

public class GuiIngame extends Gui 
{
	public GTextField chatField = new GTextField(10, Game.HEIGHT - 60, Game.WIDTH - 35, 20, 120, false);
	
	private ArrayList<String> chatValues = new ArrayList<String>();
	private int MAX_CHATLINES = 10;
	
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
				Game.thePlayer.setWalking(false);
			}
		}
	}
	
	@Override
	public void render(GL2 gl2) 
	{
		super.render(gl2);
		DrawUtils.setGL(gl2);
		DrawUtils.setFont(new Font("Veranda", 0, 20));
		DrawUtils.drawString(5, 20, (int)Game.game.GL_ANIMATOR.getLastFPS() + " FPS", Color.white);
		
		DrawUtils.setFont(new Font("Lucida Console", Font.PLAIN, 14));
		
		int stringHeight = 0;
		for(int i = chatValues.size() - 1; i >= 0; i--)
		{
			String s = chatValues.get(i) != null ? chatValues.get(i): " ";
			DrawUtils.drawString(0, (int)(50 + stringHeight * DrawUtils.getTextRenderer().getBounds("X").getHeight()), s, Color.white);
			stringHeight += 1;
		}
		
		if(focusedEntity != null)
		{
			DrawUtils.drawTexture(200, 0, focusedEntity.getShortcutImage());
			DrawUtils.drawString(200, 10, focusedEntity.getName(), Color.white);
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
