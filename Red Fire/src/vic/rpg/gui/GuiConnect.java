package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureLoader;
import vic.rpg.sound.SoundEngine;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class GuiConnect extends Gui implements IGButton
{
	private Texture bgimage = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/connect_1.png"));
	
	private GTextField server = new GTextField(Game.WIDTH / 2 - 75, Game.HEIGHT / 2 - 20, 150, 20, 15, true).setText("localhost");
	private GTextField username = new GTextField(Game.WIDTH / 2 - 75, Game.HEIGHT / 2 + 10, 150, 20, 15, true).setText(Game.USERNAME);
	
	private String errorMessage = "";
	
	public GuiConnect() 
	{
		super(true, true);
	}

	@Override
	public void render(GL2 gl2) 
	{		
		DrawUtils.setGL(gl2);
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, new Color(80, 80, 80, 180));
		DrawUtils.drawTexture(0, 0, bgimage);
		super.render(gl2);
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		DrawUtils.drawString(Game.WIDTH / 2 - 200, Game.HEIGHT / 2 - 100, "Connect to a Server", Color.white);
		DrawUtils.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		DrawUtils.drawString(Game.WIDTH / 2 - 200, Game.HEIGHT / 2 - 80, "Sponsored Servers: bobcraft.de", Color.white);
		DrawUtils.drawString(Game.WIDTH / 2 - 180, Game.HEIGHT / 2 - 5, "Server Adress:", Color.white);
		DrawUtils.drawString(Game.WIDTH / 2 - 156, Game.HEIGHT / 2 + 25, "Username:", Color.white);
		
		if(errorMessage.length() > 0)
		{
			DrawUtils.fillRect(Game.WIDTH / 2 - 205, Game.HEIGHT / 2 + 110, (int) (DrawUtils.getTextRenderer().getBounds("Connection failed! Reason: " + errorMessage).getWidth() + 10), 30, Color.black);
			DrawUtils.drawString(Game.WIDTH / 2 - 200, Game.HEIGHT / 2 + 130, "Connection failed! Reason: " + errorMessage, Color.red);
		}
	}

	@Override
	public void initGui() 
	{
		super.initGui();
		controlsList.add(server);
		controlsList.add(username);
		controlsList.add(new GButton(Game.WIDTH / 2 - 125, Game.HEIGHT / 2 + 60, 120, 30, this, "Connect"));
		controlsList.add(new GButton(Game.WIDTH / 2 + 5, Game.HEIGHT / 2 + 60, 120, 30, this, "Cancel"));
	}

	@Override
	public void onButtonPressed(String name) 
	{
		if(name.equalsIgnoreCase("Cancel"))
		{
			Gui.setGui(new GuiMain());
		}
		else if(name.equalsIgnoreCase("Connect"))
		{
			String adress;
			int port = 29598;
			String username = "player";
			
			if(this.username.plaintext.length() > 0) username = this.username.plaintext;
			if(server.plaintext.contains(":"))
			{
				adress = server.plaintext.split(":")[0];
				port = Integer.valueOf(server.plaintext.split(":")[1]);
			}
			else adress = server.plaintext;
			
			Game.netHandler = new NetHandler();
			
			if(Game.netHandler.connect(adress, port, username))
			{
				Gui.setGui(null);
				SoundEngine.loadClip("/vic/rpg/resources/sounds/Dubstep - Nostalgia - The Other Side.wav", "Level.MainLoop");
				SoundEngine.stopAll();
				SoundEngine.playClip("Level.MainLoop", true);
			}
			else
			{
				errorMessage = Game.netHandler.lastError;
			}
		}		
	}

	@Override
	public void keyTyped(char k, int keyCode) 
	{
		super.keyTyped(k, keyCode);
		
		if(keyCode == KeyEvent.VK_ESCAPE)
		{
			Gui.setGui(new GuiMain());
		}
	}
}
