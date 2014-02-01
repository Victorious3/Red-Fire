package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GIconButton;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.item.Slot;
import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.DrawUtils.LinearAnimator;
import vic.rpg.render.TextureLoader;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class GuiIngame extends GuiContainer implements IGButton
{
	public GTextField chatField = new GTextField(10, Game.HEIGHT - 25, Game.WIDTH - 35, 20, 120, false);
	
	private boolean init = false;
	private ArrayList<Object[]> chatValues = new ArrayList<Object[]>();
	private final int MAX_CHATLINES = 10;
	private int lp = 0;
	
	private Texture tNormal = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/slotchange.png"));
	private Texture tHover = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/slotchange_hover.png"));
	private Texture tClick = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/slotchange_click.png"));
	
	private GIconButton buttonSlot3;
	private GIconButton buttonSlot4;
	private GIconButton buttonSlot5;
	private GIconButton buttonSlot6;
	private GIconButton buttonSlot7;
	private GIconButton buttonSlot8;
	private GIconButton buttonSlot9;
	private GIconButton buttonSlot0;
	
	//TODO Struggles with quick health changes.
	private LinearAnimator lpAnim = DrawUtils.createLinearAnimator(500, lp, lp);
	
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
				
				controlsList.add(new Slot(50, 505, 9, this, 2, 2, true));
				controlsList.add(new Slot(120, 505, 10, this, 2, 2, true));
				controlsList.add(new Slot(190, 505, 11, this, 2, 2, true));
				controlsList.add(new Slot(260, 505, 12, this, 2, 2, true));
				controlsList.add(new Slot(340, 505, 13, this, 2, 2, true));
				controlsList.add(new Slot(410, 505, 14, this, 2, 2, true));
				controlsList.add(new Slot(480, 505, 15, this, 2, 2, true));
				controlsList.add(new Slot(550, 505, 16, this, 2, 2, true));
				controlsList.add(new Slot(620, 505, 17, this, 2, 2, true));
				controlsList.add(new Slot(690, 505, 18, this, 2, 2, true));
				
				buttonSlot3 = new GIconButton(190, 495, 60, 10, "buttonSlot3", tNormal, tHover, tClick, this);
				buttonSlot4 = new GIconButton(260, 495, 60, 10, "buttonSlot4", tNormal, tHover, tClick, this);;
				buttonSlot5 = new GIconButton(340, 495, 60, 10, "buttonSlot5", tNormal, tHover, tClick, this);;
				buttonSlot6 = new GIconButton(410, 495, 60, 10, "buttonSlot6", tNormal, tHover, tClick, this);;
				buttonSlot7 = new GIconButton(480, 495, 60, 10, "buttonSlot7", tNormal, tHover, tClick, this);;
				buttonSlot8 = new GIconButton(550, 495, 60, 10, "buttonSlot8", tNormal, tHover, tClick, this);;
				buttonSlot9 = new GIconButton(620, 495, 60, 10, "buttonSlot9", tNormal, tHover, tClick, this);;
				buttonSlot0 = new GIconButton(690, 495, 60, 10, "buttonSlot0", tNormal, tHover, tClick, this);;
				
				controlsList.add(buttonSlot3);
				controlsList.add(buttonSlot4);
				controlsList.add(buttonSlot5);
				controlsList.add(buttonSlot6);
				controlsList.add(buttonSlot7);
				controlsList.add(buttonSlot8);
				controlsList.add(buttonSlot9);
				controlsList.add(buttonSlot0);
				
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
		
		DrawUtils.setFont(new Font("Lucida Console", Font.PLAIN, 14));
		
		int i2 = 0;
		for(int i = 0; i < chatValues.size(); i++)
		{
			if(chatValues.get(i) != null)
			{		
				Object[] clog = chatValues.get(i);
				if(System.currentTimeMillis() < (long)clog[1] + 20000L || chatField.isVisible)
				{
					DrawUtils.fillRect(0, (int)(39 + i2 * DrawUtils.getFont().getSize()), MAX_CHATSIZE, DrawUtils.getFont().getSize(), new Color(0, 0, 0, 120));
					DrawUtils.drawString(0, (int)(50 + i2 * DrawUtils.getFont().getSize()), (String)clog[0], Color.white);
					i2++;
				}
			}
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
		if(playername.equals("SERVER"))
		{
			s = "[&color=178,0,255#SERVER&1]: " + s;
		}
		else s = "[" + playername + "]: " + s;
		
		if(chatValues.size() + 1 > MAX_CHATLINES) chatValues.remove(0);
		chatValues.add(new Object[]{s, System.currentTimeMillis()});
	}

	@Override
	public void onButtonPressed(String name) {
		// TODO Auto-generated method stub
		
	}
}
