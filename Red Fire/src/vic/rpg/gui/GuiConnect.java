package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GTextField;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.sound.SoundPlayer;
import vic.rpg.utils.Utils;

public class GuiConnect extends Gui implements IGButton
{
	private Image bgimage = Utils.readImageFromJar("/vic/rpg/resources/connect_1.png");
	
	private GTextField server = new GTextField(Game.WIDTH / 2 - 75, Game.HEIGHT / 2 - 20, 150, 20, 15, true).setText("localhost");
	private GTextField username = new GTextField(Game.WIDTH / 2 - 75, Game.HEIGHT / 2 + 10, 150, 20, 15, true).setText(Game.USERNAME);
	
	private String errorMessage = "";
	
	public GuiConnect() 
	{
		super(true, true);
	}

	@Override
	public void render(Graphics2D g2d) 
	{		
		g2d.drawImage(bgimage, 0, 0, null);
		
		g2d.setColor(new Color(80, 80, 80, 180));
		g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		super.render(g2d);
		
		g2d.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		g2d.drawString("Connect to a Server", Game.WIDTH / 2 - 200, Game.HEIGHT / 2 - 100);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		g2d.drawString("Sponsored Servers: bobcraft.de", Game.WIDTH / 2 - 200, Game.HEIGHT / 2 - 80);
		g2d.drawString("Server Adress:", Game.WIDTH / 2 - 180, Game.HEIGHT / 2 - 5);
		g2d.drawString("Username:", Game.WIDTH / 2 - 156, Game.HEIGHT / 2 + 25);
		
		if(errorMessage.length() > 0)
		{
			g2d.setColor(Color.black);
			g2d.fillRect(Game.WIDTH / 2 - 205, Game.HEIGHT / 2 + 110, g2d.getFontMetrics().stringWidth("Connection failed! Reason: " + errorMessage) + 10, 30);
			g2d.setColor(Color.red);
			g2d.drawString("Connection failed! Reason: " + errorMessage, Game.WIDTH / 2 - 200, Game.HEIGHT / 2 + 130);
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
				SoundPlayer.playSoundLoop("/vic/rpg/resources/sounds/Dubstep - Nostalgia - The Other Side.wav");
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
