package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.client.render.DrawUtils.LinearAnimator;
import vic.rpg.client.render.DrawUtils.RenderedText;
import vic.rpg.combat.Skill;
import vic.rpg.combat.SlotSkill;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GControl;
import vic.rpg.gui.controls.GIconButton;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.registry.WorldRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.server.packet.Packet20Chat;
import vic.rpg.utils.Utils;
import vic.rpg.world.entity.living.EntityLiving;

import com.jogamp.opengl.util.texture.Texture;

/**
 * GuiIngame is the main gui that's displayed when no other gui is active. It displays stuff like the current health of the player, the chat or the Quickbar with all the active {@link Skill Skills}.
 * @author Victorious3
 */
public class GuiIngame extends GuiContainer implements IGButton
{
	public GTextField chatField = new GTextField(10, Game.HEIGHT - 25, Game.WIDTH - 35, 20, 120, false);
	
	private boolean init = false;
	private ArrayList<Object[]> chatValues = new ArrayList<Object[]>();
	private CopyOnWriteArrayList<Object[]> chatBuffer = new CopyOnWriteArrayList<Object[]>();
	private String lastChat = "";
	private int scroll = 0;
	private final int MAX_CHATLINES = 10;
	private int lp = 0;
	
	private Texture tNormal = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/slotchange.png"));
	private Texture tHover = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/slotchange_hover.png"));
	private Texture tClick = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/slotchange_click.png"));
	
	private GIconButton buttonSlot2;
	private GIconButton buttonSlot3;
	private GIconButton buttonSlot4;
	private GIconButton buttonSlot5;
	private GIconButton buttonSlot6;
	private GIconButton buttonSlot7;
	private GIconButton buttonSlot8;
	private GIconButton buttonSlot9;
	
	private SlotSkill slotSkill0;
	private SlotSkill slotSkill1;
	private SlotSkill slotSkill2;
	private SlotSkill slotSkill3;
	private SlotSkill slotSkill4;
	private SlotSkill slotSkill5;
	private SlotSkill slotSkill6;
	private SlotSkill slotSkill7;
	private SlotSkill slotSkill8;
	private SlotSkill slotSkill9;
	
	//TODO Struggles with quick health changes.
	private LinearAnimator lpAnim = DrawUtils.createLinearAnimator(500, lp, lp);
	
	public static GuiIngame gui = new GuiIngame();
	public static EntityLiving focusedEntity = null;
	
	private GuiIngame() 
	{
		super(false);
		chatField.isVisible = false;
	}

	@Override
	public void onMouseClickStart(int x, int y, int mouseButton) 
	{
		super.onMouseClickStart(x, y, mouseButton);
		focusedEntity = null;
	}

	
	@Override
	public void initGui() 
	{
		scroll = 0;
		init = false;
	}

	@Override
	public void updateGui() 
	{
		if(Game.getPlayer() != null)
		{
			if(!init)
			{
				controlsList.clear();
				controlsList.add(chatField);
				setInventory(Game.getPlayer().getInventory());
				
				slotSkill0 = new SlotSkill(50, 505, 0, this);
				slotSkill1 = new SlotSkill(120, 505, 1, this);
				slotSkill2 = new SlotSkill(190, 505, 2, this);
				slotSkill3 = new SlotSkill(260, 505, 3, this);
				slotSkill4 = new SlotSkill(340, 505, 4, 13, this);
				slotSkill5 = new SlotSkill(410, 505, 5, 14, this);
				slotSkill6 = new SlotSkill(480, 505, 6, 15, this);
				slotSkill7 = new SlotSkill(550, 505, 7, 16, this);
				slotSkill8 = new SlotSkill(620, 505, 8, 17, this);
				slotSkill9 = new SlotSkill(690, 505, 9, 18, this);
				
				controlsList.add(slotSkill0);
				controlsList.add(slotSkill1);
				controlsList.add(slotSkill2);
				controlsList.add(slotSkill3);
				controlsList.add(slotSkill4);
				controlsList.add(slotSkill5);
				controlsList.add(slotSkill6);
				controlsList.add(slotSkill7);
				controlsList.add(slotSkill8);
				controlsList.add(slotSkill9);
				
				buttonSlot2 = new GIconButton(190, 495, 60, 10, "buttonSlot2", tNormal, tHover, tClick, this);
				buttonSlot3 = new GIconButton(260, 495, 60, 10, "buttonSlot3", tNormal, tHover, tClick, this);;
				buttonSlot4 = new GIconButton(340, 495, 60, 10, "buttonSlot4", tNormal, tHover, tClick, this);;
				buttonSlot5 = new GIconButton(410, 495, 60, 10, "buttonSlot5", tNormal, tHover, tClick, this);;
				buttonSlot6 = new GIconButton(480, 495, 60, 10, "buttonSlot6", tNormal, tHover, tClick, this);;
				buttonSlot7 = new GIconButton(550, 495, 60, 10, "buttonSlot7", tNormal, tHover, tClick, this);;
				buttonSlot8 = new GIconButton(620, 495, 60, 10, "buttonSlot8", tNormal, tHover, tClick, this);;
				buttonSlot9 = new GIconButton(690, 495, 60, 10, "buttonSlot9", tNormal, tHover, tClick, this);;
				
				controlsList.add(buttonSlot2);
				controlsList.add(buttonSlot3);
				controlsList.add(buttonSlot4);
				controlsList.add(buttonSlot5);
				controlsList.add(buttonSlot6);
				controlsList.add(buttonSlot7);
				controlsList.add(buttonSlot8);
				controlsList.add(buttonSlot9);
				
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
	public void onKeyTyped(char k, int keyCode) 
	{		
		super.onKeyTyped(k, keyCode);
		
		if(keyCode == KeyEvent.VK_ENTER)
		{
			scroll = 0;
			if(chatField.isVisible)
			{
				chatField.isVisible = false;
				if(chatField.plaintext.length() > 0) Game.packetHandler.addPacketToSendingQueue(new Packet20Chat(chatField.plaintext, Game.USERNAME));
				lastChat = chatField.plaintext;
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
		if(keyCode == KeyEvent.VK_UP && chatField.isVisible && lastChat.length() > 0)
		{
			chatField.setText(lastChat);
		}
	}
	
	private final int MAX_CHATSIZE = 400;
	
	@Override
	public void render(GL2 gl2) 
	{
		super.render(gl2);
		
		DrawUtils.setGL(gl2);
		
		if(chatBuffer.size() > 0)
		{
			for(Object[] o : chatBuffer)
			{
				addChatMessageFromBuffer((String)o[0], (String)o[1]);
			}
			chatBuffer.clear();
		}
		
		DrawUtils.setFont(new Font("Monospaced", 0, 20));
		DrawUtils.drawString(5, 20, (int)Game.game.GL_ANIMATOR.getLastFPS() + " FPS", Color.white);
		
		DrawUtils.setFont(new Font("Monospaced", Font.PLAIN, 14));
		
		int i2 = 0;
		int scroll = this.scroll;
		if(!chatField.isVisible) scroll = 0;
		
		DrawUtils.lockFont(true);
		for(int i = (chatValues.size() - MAX_CHATLINES + scroll) > 0 ? (chatValues.size() - MAX_CHATLINES + scroll) : 0; i < chatValues.size() + scroll; i++)
		{
			if(chatValues.get(i) != null)
			{		
				Object[] clog = chatValues.get(i);
				if(System.currentTimeMillis() < (long)clog[1] + 20000L || chatField.isVisible)
				{
					DrawUtils.fillRect(0, (int)(39 + i2 * DrawUtils.getFont().getSize()), MAX_CHATSIZE, 14, new Color(0, 0, 0, 120));
					DrawUtils.drawString(0, (int)(50 + i2 * DrawUtils.getFont().getSize()), (String)clog[0], Color.white);
					i2++;
				}
			}
		}
		DrawUtils.lockFont(false);
		
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
			DrawUtils.drawString(52, 483, "HP", Color.black);
		}
	}
	
	@Override
	public void onMouseWheelMoved(int amount) 
	{
		if(scroll + amount / 3 <= 0 && Math.abs(scroll + amount / 3) <= chatValues.size() - MAX_CHATLINES) scroll += amount / 3;
	}

	/**
	 * Adds a chat message to the buffer.
	 * @param s
	 * @param playername
	 */
	public void addChatMessage(String s, String playername)
	{		
		chatBuffer.add(new Object[]{s, playername});
	}
	
	private void addChatMessageFromBuffer(String s, String playername)
	{
		if(playername.equals("SERVER"))
		{
			s = "&p&1[&color=178,0,255#SERVER&1]: " + s;
		}
		else s = "&p&1[" + playername + "]: " + s;
		
		DrawUtils.setFont(new Font("Monospaced", Font.PLAIN, 14));
		RenderedText rs = DrawUtils.getFormattedStringMetrics(s, MAX_CHATSIZE);
		long time = System.currentTimeMillis();
		
		for(String s2 : rs.getLineSeperatedText())
		{
			if(chatValues.size() + 1 > 100) chatValues.remove(0);
			chatValues.add(new Object[]{s2, time});
		}
	}

	@Override
	public void onButtonPressed(GControl button) 
	{
		if(button == buttonSlot2) slotSkill2.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot3) slotSkill3.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot4) slotSkill4.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot5) slotSkill5.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot6) slotSkill6.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot7) slotSkill7.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot8) slotSkill8.setSkill(WorldRegistry.SKILL_HEAL.clone());
		else if(button == buttonSlot9) slotSkill9.setSkill(WorldRegistry.SKILL_HEAL.clone());
		
		gui.inventory.updateInventory();
	}
}
