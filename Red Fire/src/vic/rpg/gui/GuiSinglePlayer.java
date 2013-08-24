package vic.rpg.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.gui.controls.GList;
import vic.rpg.gui.controls.GList.IGList;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureLoader;
import vic.rpg.server.Server;
import vic.rpg.sound.SoundPlayer;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class GuiSinglePlayer extends Gui implements IGList
{
	private Texture bgimage = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/connect_1.png"));
	private GList levelList;
	
	public GuiSinglePlayer() 
	{
		super(true, true);
	}
	
	@Override
	public void render(GL2 gl2) 
	{		
		DrawUtils.setGL(gl2);
		DrawUtils.drawTexture(0, 0, bgimage);	
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, new Color(80, 80, 80, 180));
		super.render(gl2);
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		DrawUtils.drawString(Game.WIDTH / 2 - 135, 50, "Start a Game", Color.white);
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
