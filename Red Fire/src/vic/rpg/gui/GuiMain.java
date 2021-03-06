package vic.rpg.gui;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.PostInit;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.client.render.TextureFX;
import vic.rpg.client.render.DrawUtils.GradientAnimator;
import vic.rpg.client.render.DrawUtils.LinearAnimator;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GControl;
import vic.rpg.registry.LanguageRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.sound.SoundEngine;
import vic.rpg.utils.Utils.Side;

/**
 * GuiMain is the main menu that's displayed when the game has started. All actions start from here.
 * @author Victorious3
 *
 */
public class GuiMain extends Gui implements IGButton 
{
	public static TextureFX bgimage = new TextureFX("/vic/rpg/resources/fire.gif", 40);	
	
	public GuiMain() 
	{
		super(true, true);
	}

	private GradientAnimator fadeIn = DrawUtils.createGratientAnimator(1000, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0));
	private static LinearAnimator fadeRight = DrawUtils.createLinearAnimator(500, -500, 0);
	
	private static LinearAnimator fadeRightButton1 = DrawUtils.createLinearAnimator(500, -500, 0);
	private static LinearAnimator fadeRightButton2 = DrawUtils.createLinearAnimator(600, -600, 0);
	private static LinearAnimator fadeRightButton3 = DrawUtils.createLinearAnimator(700, -700, 0);
	private static LinearAnimator fadeRightButton4 = DrawUtils.createLinearAnimator(800, -800, 0);
	private static LinearAnimator fadeRightButton5 = DrawUtils.createLinearAnimator(900, -900, 0);
	
	boolean animationFinished = false;
	
	@PostInit(side = Side.CLIENT)
	public static void init()
	{
		LanguageRegistry.addTranslation(LanguageRegistry.en_GB, "guimain.singleplayer", "Singleplayer");
		LanguageRegistry.addTranslation(LanguageRegistry.de_DE, "guimain.singleplayer", "Einzelspieler");
		LanguageRegistry.addTranslation(LanguageRegistry.fr_FR, "guimain.singleplayer", "Jeu Solo");

		LanguageRegistry.addTranslation(LanguageRegistry.en_GB, "guimain.multiplayer", "Multiplayer");
		LanguageRegistry.addTranslation(LanguageRegistry.de_DE, "guimain.multiplayer", "Mehrspieler");
		LanguageRegistry.addTranslation(LanguageRegistry.fr_FR, "guimain.multiplayer", "Multijoueur");
		
		LanguageRegistry.addTranslation(LanguageRegistry.en_GB, "guimain.options", "Options");
		LanguageRegistry.addTranslation(LanguageRegistry.de_DE, "guimain.options", "Einstellungen");
		LanguageRegistry.addTranslation(LanguageRegistry.fr_FR, "guimain.options", "Reglages");
		
		LanguageRegistry.addTranslation(LanguageRegistry.en_GB, "guimain.singleplayer", "Singleplayer");
		LanguageRegistry.addTranslation(LanguageRegistry.de_DE, "guimain.singleplayer", "Einzelspieler");
		LanguageRegistry.addTranslation(LanguageRegistry.fr_FR, "guimain.singleplayer", "Jeu Solo");
		
		LanguageRegistry.addTranslation(LanguageRegistry.en_GB, "guimain.credits", "Credits");
		LanguageRegistry.addTranslation(LanguageRegistry.de_DE, "guimain.credits", "Mitwirkende");
		LanguageRegistry.addTranslation(LanguageRegistry.fr_FR, "guimain.credits", "Contributors");
		
		LanguageRegistry.addTranslation(LanguageRegistry.en_GB, "guimain.quit", "Quit");
		LanguageRegistry.addTranslation(LanguageRegistry.de_DE, "guimain.quit", "Verlassen");
		LanguageRegistry.addTranslation(LanguageRegistry.fr_FR, "guimain.quit", "Quitter");
	}
	
	@Override
	public void render(GL2 gl2) 
	{	
		DrawUtils.setGL(gl2);
		bgimage.draw(gl2, 0, 0);
		super.render(gl2);	
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(80.0F));
		fadeRight.animate();
		DrawUtils.drawString(Game.WIDTH / 2 - 130 + (int)fadeRight.getValue(), Game.HEIGHT / 2 - 100, "Red Fire", Color.white);
		
		fadeIn.animate();
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, fadeIn.getColor());
		
		if(!animationFinished)
		{
			DrawUtils.animate(fadeRightButton1, fadeRightButton2, fadeRightButton3, fadeRightButton4, fadeRightButton5);
			
			controlsList.get(0).xCoord = Game.WIDTH / 2 - 120 + (int)fadeRightButton1.getValue();
			controlsList.get(1).xCoord = Game.WIDTH / 2 - 120 + (int)fadeRightButton2.getValue();
			controlsList.get(2).xCoord = Game.WIDTH / 2 - 120 + (int)fadeRightButton3.getValue();
			controlsList.get(3).xCoord = Game.WIDTH / 2 - 120 + (int)fadeRightButton4.getValue();
			controlsList.get(4).xCoord = Game.WIDTH / 2 - 120 + (int)fadeRightButton5.getValue();
			
			if(fadeRightButton5.getValue() == 0) 
			{
				animationFinished = true;
				controlsList.get(0).lock(false);
				controlsList.get(1).lock(false);
				controlsList.get(2).lock(false);
				controlsList.get(3).lock(false);
				controlsList.get(4).lock(false);
			}
		}
	}

	@Override
	public void initGui() 
	{
		SoundEngine.loadClip("/vic/rpg/resources/sounds/Horst Theme.wav", "GuiMain.bg");
		SoundEngine.stopAll("GuiMain.bg");
		SoundEngine.playClip("GuiMain.bg", true);
		
		super.initGui();
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 - 40, 240, 40, this, LanguageRegistry.getTranslation("guimain.singleplayer"), "Singleplayer").lock(true));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 10, 240, 40, this, LanguageRegistry.getTranslation("guimain.multiplayer"), "Multiplayer").lock(true));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 60, 240, 40, this, LanguageRegistry.getTranslation("guimain.options"), "Options").lock(true));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 110, 240, 40, this, LanguageRegistry.getTranslation("guimain.credits"), "Credits").lock(true));
		controlsList.add(new GButton(Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 160, 240, 40, this, LanguageRegistry.getTranslation("guimain.quit"), "Quit").lock(true));
	}

	@Override
	public void onButtonPressed(GControl button) 
	{
		GButton b2 = (GButton)button;
		if(b2.name.equalsIgnoreCase("Singleplayer"))
		{
			Gui.setGui(new GuiSinglePlayer());
		}
		else if(b2.name.equalsIgnoreCase("Multiplayer"))
		{
			Gui.setGui(new GuiConnect());
		}
		else if(b2.name.equalsIgnoreCase("Options"))
		{
			Gui.setGui(new GuiOptions());
		}
		else if(b2.name.equalsIgnoreCase("Credits"))
		{
			
		}
		else if(b2.name.equalsIgnoreCase("Quit"))
		{
			Game.game.stopGame();			
		}
	}
}
