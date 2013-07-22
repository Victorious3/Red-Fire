package vic.rpg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.gui.controls.GList;
import vic.rpg.gui.controls.GList.IGList;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.server.Server;
import vic.rpg.sound.SoundPlayer;
import vic.rpg.utils.Utils;

public class GuiSinglePlayer extends Gui implements IGList
{
	private Image bgimage = Utils.readImageFromJar("/vic/rpg/resources/connect_1.png");
	private GList levelList;
	
	public GuiSinglePlayer() 
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
		
		g2d.setColor(Color.white);
		g2d.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		g2d.drawString("Start a Game", Game.WIDTH / 2 - 135, 50);
	}
	
	@Override
	public void initGui() 
	{
		super.initGui();
		ArrayList<File> list = new ArrayList<File>();
		
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/saves/");
		
		for(File f : file.listFiles())
		{
			list.add(f);
		}
		
		levelList = new GList(150, 70, 500, 380, 30, list, this);
		controlsList.add(levelList);
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
	
	private void loadGame(File file)
	{
		Server.main(new String[]{"-splayer", "-file", file.getAbsolutePath()});
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

	@Override
	public void onElementDoubleClick(GList glist, Object element) 
	{
		this.loadGame((File) element);
	}	
}
