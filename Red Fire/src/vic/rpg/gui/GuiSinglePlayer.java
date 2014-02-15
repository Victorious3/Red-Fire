package vic.rpg.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GControl;
import vic.rpg.gui.controls.GList;
import vic.rpg.gui.controls.GList.IGList;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureLoader;
import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.sound.SoundEngine;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class GuiSinglePlayer extends Gui implements IGList, IGButton
{
	private Texture bgimage = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/connect_1.png"));
	private GList levelList;
	private ArrayList<File> list = new ArrayList<File>();
	
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
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/saves/");
		ArrayList<String> nameList = new ArrayList<String>();
		
		for(File f : file.listFiles())
		{
			list.add(f);
			try {
				NBTInputStream in = new NBTInputStream(new FileInputStream(f));
				CompoundTag tag = (CompoundTag)in.readTag();
				nameList.add(tag.getString("name", "NO_NAME"));
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		levelList = new GList(150, 70, 500, 380, 30, nameList, this);
		controlsList.add(levelList);
		
		controlsList.add(new GButton(270, 500, 100, 30, this, "Cancel", "Cancel"));
		controlsList.add(new GButton(410, 500, 100, 30, this, "Load", "Load"));
	}
	
	@Override
	public void onKeyTyped(char k, int keyCode) 
	{
		super.onKeyTyped(k, keyCode);
		
		if(keyCode == KeyEvent.VK_ESCAPE)
		{
			Gui.setGui(new GuiMain());
		}
	}
	
	private void loadGame(File file)
	{
		System.out.println("Starting Server...");
		Server.main(new String[]{"-splayer", "-file", file.getAbsolutePath()});
		Server.MAX_CONNECTIONS = 1;
		Game.netHandler = new NetHandler();
		Game.netHandler.IS_SINGLEPLAYER = true;
		
		System.out.println("Waiting for Server...");
		int trys = 0;
		while(Server.STATE != GameState.RUNNING)
		{
			trys++;
			if(trys == 20)
			{
				System.out.println("Server timed out! Aborting!");
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Connecting to Server...");
		if(Game.netHandler.connect("localhost", 29598, Game.USERNAME))
		{
			Gui.setGui(null);
			SoundEngine.loadClip("/vic/rpg/resources/sounds/Dubstep - Nostalgia - The Other Side.wav", "Level.MainLoop");
			SoundEngine.stopAll();
			SoundEngine.playClip("Level.MainLoop", true);
		}
	}

	@Override
	public void onElementDoubleClick(GList glist, Object element, int pos) 
	{
		loadGame(list.get(pos));
	}

	@Override
	public void onButtonPressed(GControl button) 
	{
		GButton b2 = (GButton)button;
		if(b2.name.equalsIgnoreCase("Cancel")) Gui.setGui(new GuiMain());
		if(b2.name.equalsIgnoreCase("Load")) loadGame(list.get(levelList.selectedPos));
	}	
}
