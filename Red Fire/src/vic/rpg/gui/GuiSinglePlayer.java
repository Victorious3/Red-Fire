package vic.rpg.gui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.client.net.NetHandler;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.client.render.DrawUtils.GradientAnimator;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GControl;
import vic.rpg.gui.controls.GList;
import vic.rpg.gui.controls.GList.IGList;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.server.GameState;
import vic.rpg.server.Server;
import vic.rpg.sound.SoundEngine;
import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import vic.rpg.world.World;

import com.jogamp.opengl.util.texture.Texture;

/**
 * GuiSinglePlayer is the gui where you can select a {@link Map} located in "%APPDATA%\.RedFire\saves\" for loading and starting an internal Server.
 * @author Victorious3
 */
public class GuiSinglePlayer extends Gui implements IGList, IGButton
{
	private Texture bgimage = TextureLoader.requestTexture(Utils.readImage("/vic/rpg/resources/connect_1.png"));
	private GList mapList;
	private ArrayList<File> list = new ArrayList<File>();
	
	private GradientAnimator fadeIn = DrawUtils.createGratientAnimator(1000, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0));
	
	public GuiSinglePlayer() 
	{
		super(true, true);
	}
	
	@Override
	public void render(GL2 gl2) 
	{		
		DrawUtils.setGL(gl2);
		DrawUtils.drawTexture(0, 0, bgimage);	
		super.render(gl2);
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		DrawUtils.drawString(Game.WIDTH / 2 - 135, 50, "Start a Game", Color.white);
		
		fadeIn.animate();
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, fadeIn.getColor());
	}
	
	@Override
	public void initGui() 
	{
		super.initGui();		
		File file = Utils.getOrCreateFile(Utils.getAppdata() + "/saves/");
		ArrayList<String> nameList = new ArrayList<String>();
		
		for(File f : file.listFiles())
		{
			String worldName = World.getWorldName(f);
			if(worldName != null)
			{
				list.add(f);
				nameList.add(worldName);
			}
		}
		
		
		mapList = new GList(150, 70, 500, 380, 30, nameList, this);
		controlsList.add(mapList);
		
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
	
	/**
	 * Starts a new internal {@link Server} with the given file as a -file parameter in -splayer mode.
	 * The method waits for the {@link Server} to finish its startup and connects to it after creating a new
	 * {@link NetHandler}. The {@link Gui#currentGui} is set to {@code null}.
	 * @param file
	 */
	private void loadGame(File file)
	{
		System.out.println("Starting Server...");
		Server.main(new String[]{"-splayer", "-file", file.getAbsolutePath()});
		Server.MAX_CONNECTIONS = 1;
		
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
		
		Game.netHandler = new NetHandler();
		Game.netHandler.IS_SINGLEPLAYER = true;
		
		System.out.println("Connecting to Server...");
		if(Game.netHandler.connect("localhost", 29598, Game.USERNAME))
		{
			Gui.setGui(null);
			SoundEngine.loadClip("/vic/rpg/resources/sounds/Dubstep - Nostalgia - The Other Side.wav", "Map.MainLoop");
			SoundEngine.stopAll();
			SoundEngine.playClip("Map.MainLoop", true);
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
		if(b2.name.equalsIgnoreCase("Load")) loadGame(list.get(mapList.selectedPos));
	}	
}
