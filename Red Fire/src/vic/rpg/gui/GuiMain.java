package vic.rpg.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.editor.Editor;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.Gif;
import vic.rpg.server.Server;
import vic.rpg.sound.SoundPlayer;

public class GuiMain extends Gui implements IGButton 
{
	public static Gif bgimage = new Gif("/vic/rpg/resources/redfire.gif", 5);
	
	public GuiMain() 
	{
		super(true, true);
	}

	@Override
	public void render(Graphics2D g2d) 
	{
		g2d.drawImage(bgimage.getImage(), 0, 0, null);
		g2d.setColor(new Color(80, 80, 80, 180));
		g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		super.render(g2d);
		
		g2d.setFont(RenderRegistry.RPGFont.deriveFont(80.0F));
		g2d.drawString("Red Fire", Game.WIDTH / 2 - 130, Game.HEIGHT / 2 - 100);
	}

	@Override
	public void initGui() 
	{
		SoundPlayer.playSoundLoop("/vic/rpg/resources/sounds/fire.wav");
		
		super.initGui();
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 - 40, 240, 40, this, "Singleplayer"));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 10, 240, 40, this, "Multiplayer"));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 60, 240, 40, this, "Options"));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 110, 240, 40, this, "Editor"));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 160, 240, 40, this, "Quit"));
	}

	@Override
	public void onButtonPressed(String name) 
	{
		if(name.equalsIgnoreCase("Singleplayer"))
		{
			Server.main(new String[]{"-splayer"});
			Server.MAX_CONNECTIONS = 1;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Game.netHandler = new NetHandler();
			Game.netHandler.IS_SINGLEPLAYER = true;
			
			if(Game.netHandler.connect("localhost", 29598, Game.USERNAME))
			{
				Gui.setGui(null);
				
				SoundPlayer.playSoundLoop("/vic/rpg/resources/sounds/Dubstep - Nostalgia - The Other Side.wav");
			}
		}
		else if(name.equalsIgnoreCase("Multiplayer"))
		{
			Gui.setGui(new GuiConnect());
		}
		else if(name.equalsIgnoreCase("Options"))
		{
			Gui.setGui(new GuiOptions(this));
		}
		else if(name.equalsIgnoreCase("Editor"))
		{
			Editor.main(new String[]{"internal"});
		}
		else if(name.equalsIgnoreCase("Quit"))
		{
			Game.game.stopGame();			
		}
	}
}
