package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.config.Options;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GSlider;
import vic.rpg.gui.controls.GSwitcher;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;

public class GuiOptions extends Gui implements GButton.IGButton 
{
	private GSlider slider1;
	
	private GSwitcher switcherAA;
	private GSwitcher switcherCR;
	private GSwitcher switcherIP;
	
	public GuiOptions() 
	{
		super(true, true);
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
	}

	@Override
	public void keyTyped(char k, int keyCode) 
	{
		if(keyCode == KeyEvent.VK_ESCAPE)
		{
			Gui.setGui(new GuiMain());
		}
	}

	@Override
	public void initGui() 
	{
		int ANTIALASING = 0;
		int COLOR_RENDER = 0;
		int INTERPOLATION = 0;
		
		int xOffset = (Game.WIDTH)/2;
		int yOffset = (Game.HEIGHT)/2 - 100;
		
		switcherAA = new GSwitcher(xOffset - 60, yOffset + 30, 120, 30, new String[]{"default", "ON", "OFF"}, ANTIALASING);
		switcherCR = new GSwitcher(xOffset - 60, yOffset + 70, 120, 30, new String[]{"default", "speed", "quality"}, COLOR_RENDER);
		switcherIP = new GSwitcher(xOffset - 60, yOffset + 110, 120, 30, new String[]{"Next Neighbor", "bilinear", "bicubic"}, INTERPOLATION);
		
		this.controlsList.add(switcherAA);
		this.controlsList.add(switcherCR);
		this.controlsList.add(switcherIP);
		
		this.controlsList.add(new GButton(xOffset - 100, yOffset + 170, 100, 30, this, "Apply", "Apply"));
		this.controlsList.add(new GButton(xOffset + 10, yOffset + 170, 100, 30, this, "Cancel", "Cancel"));
	}

	@Override
	public void onButtonPressed(String name)
	{
		if(name.equalsIgnoreCase("Cancel"))
		{
			Gui.setGui(new GuiMain());
		}
		else if(name.equalsIgnoreCase("Apply"))
		{
			//TODO Adjust the options
			Options.safe();
			Gui.setGui(new GuiMain());
		}
	}
}
