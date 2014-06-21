package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.config.Options;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GControl;
import vic.rpg.gui.controls.GSwitcher;
import vic.rpg.registry.LanguageRegistry;
import vic.rpg.registry.RenderRegistry;

/**
 * GuiOptions is the gui that lets the player change all the {@link Options}.
 * @author Victorious3
 */
public class GuiOptions extends Gui implements GButton.IGButton 
{	
	private GSwitcher switcherLighting;
	private GSwitcher switcherLanguage;
	
	public GuiOptions() 
	{
		super(true, false);
	}
	
	@Override
	public void render(GL2 gl2) 
	{
		DrawUtils.setGL(gl2);
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, new Color(80, 80, 80, 180));
		
		super.render(gl2);
		
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		DrawUtils.drawUnformattedString(Game.WIDTH / 2 - 90, Game.HEIGHT / 2 - 180, "Options", Color.white);
		DrawUtils.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		DrawUtils.drawUnformattedString(Game.WIDTH / 2 - 90, Game.HEIGHT / 2 - 85, "Enable experimental lighting:", Color.white);
		DrawUtils.drawUnformattedString(Game.WIDTH / 2 - 90, Game.HEIGHT / 2 - 15, "Language:", Color.white);
	}

	@Override
	public void onKeyTyped(char k, int keyCode) 
	{
		if(keyCode == KeyEvent.VK_ESCAPE)
		{
			Gui.setGui(new GuiMain());
		}
	}

	@Override
	public void initGui() 
	{
		int xOffset = (Game.WIDTH)/2;
		int yOffset = (Game.HEIGHT)/2 - 100;
		
		switcherLighting = new GSwitcher(xOffset - 60, yOffset + 30, 120, 30, new String[]{"ON", "OFF"}, Options.LIGHTING ? 0 : 1);
		switcherLanguage = new GSwitcher(xOffset - 60, yOffset + 100, 120, 30, LanguageRegistry.getLanguages().toArray(new String[LanguageRegistry.getLanguages().size()]), LanguageRegistry.getLanguages().indexOf(Options.LANGUAGE));

		this.controlsList.add(switcherLighting);
		this.controlsList.add(switcherLanguage);
		
		this.controlsList.add(new GButton(xOffset - 100, yOffset + 200, 100, 30, this, "Apply", "Apply"));
		this.controlsList.add(new GButton(xOffset + 10, yOffset + 200, 100, 30, this, "Cancel", "Cancel"));
	}

	@Override
	public void onButtonPressed(GControl button)
	{
		GButton b2 = (GButton)button;
		if(b2.name.equalsIgnoreCase("Cancel"))
		{
			Gui.setGui(new GuiMain());
		}
		else if(b2.name.equalsIgnoreCase("Apply"))
		{
			Options.LIGHTING = switcherLighting.modePointer == 0 ? true : false;
			Options.LANGUAGE = switcherLanguage.modes[switcherLanguage.modePointer];
			
			Options.safe();
			Gui.setGui(new GuiMain());
		}
	}
}
