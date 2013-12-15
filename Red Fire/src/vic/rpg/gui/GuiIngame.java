package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.item.Slot;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.DrawUtils.LinearAnimator;
import vic.rpg.server.packet.Packet20Chat;

public class GuiIngame extends GuiContainer 
{
	public GTextField chatField = new GTextField(10, Game.HEIGHT - 25, Game.WIDTH - 35, 20, 120, false);
	
	private boolean init = false;
	private ArrayList<String> chatValues = new ArrayList<String>();
	private final int MAX_CHATLINES = 10;
	private int lp;
	private LinearAnimator lpAnim;
	
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
	public void updateGui() 
	{
		if(Game.getPlayer() != null)
		{
			if(!init)
			{
				setInventory(Game.getPlayer().getInventory());
				
				controlsList.add(new Slot(50, 500, 9, this, 2, 2, true));
				controlsList.add(new Slot(120, 500, 10, this, 2, 2, true));
				controlsList.add(new Slot(190, 500, 11, this, 2, 2, true));
				controlsList.add(new Slot(260, 500, 12, this, 2, 2, true));
				controlsList.add(new Slot(340, 500, 13, this, 2, 2, true));
				controlsList.add(new Slot(410, 500, 14, this, 2, 2, true));
				controlsList.add(new Slot(480, 500, 15, this, 2, 2, true));
				controlsList.add(new Slot(550, 500, 16, this, 2, 2, true));
				controlsList.add(new Slot(620, 500, 17, this, 2, 2, true));
				controlsList.add(new Slot(690, 500, 18, this, 2, 2, true));
				
				lp = Game.getPlayer().lp;
				lpAnim = DrawUtils.createLinearAnimator(500, lp, lp);
				
				init = true;
			}
			if(lp != Game.getPlayer().lp)
			{
				lpAnim = DrawUtils.createLinearAnimator(500, lp, Game.getPlayer().lp);
				lp = Game.getPlayer().lp;
			}
		}
		super.updateGui();
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
				Game.getPlayer().isWalkingBlocked = false;
				chatField.clear();
			}
			else
			{
				chatField.isVisible = true;
				Game.getPlayer().isWalkingBlocked = true;
				Game.getPlayer().setWalking(false);
			}
		}
	}
	
	private final int MAX_CHATSIZE = 400;
	
	@Override
	public void render(GL2 gl2) 
	{
		super.render(gl2);
		
		DrawUtils.setGL(gl2);
		DrawUtils.setFont(new Font("Veranda", 0, 20));
		DrawUtils.drawString(5, 20, (int)Game.game.GL_ANIMATOR.getLastFPS() + " FPS", Color.white);
		
		DrawUtils.fillRect(0, 50 - 14, MAX_CHATSIZE, 12 * MAX_CHATLINES, new Color(0, 0, 0, 120));
		
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
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(18F));
		
		if(Game.getPlayer() != null)
		{
			lpAnim.animate();
			DrawUtils.fillRect(50, 470, (int)(200 * ((float)lpAnim.getValue() / (float)Game.getPlayer().max_lp)), 15, Color.green);
			DrawUtils.drawRect(50, 470, 200, 15, Color.black);
			DrawUtils.drawString(52, 483, "LP", Color.black);
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
