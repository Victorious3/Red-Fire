package vic.rpg.gui;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.editor.Editor;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureFX;
import vic.rpg.sound.SoundPlayer;


public class GuiMain extends Gui implements IGButton 
{
	public static TextureFX bgimage = new TextureFX("/vic/rpg/resources/fire.gif", 4);	
	public GuiMain() 
	{
		super(true, true);
	}

	@Override
	public void render(GL2 gl2) 
	{
		DrawUtils.setGL(gl2);	
		bgimage.draw(gl2, 0, 0);
		super.render(gl2);	
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(80.0F));
		DrawUtils.drawString(Game.WIDTH / 2 - 130, Game.HEIGHT / 2 - 100, "Red Fire", Color.white);
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
			Gui.setGui(new GuiSinglePlayer());
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
