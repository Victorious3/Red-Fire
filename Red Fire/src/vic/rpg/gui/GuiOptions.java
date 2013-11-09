package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.config.Options;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GSlider;
import vic.rpg.gui.controls.GSwitcher;
import vic.rpg.registry.RenderRegistry;

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
		gl2.setColor(new Color(80, 80, 80, 180));
		gl2.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		gl2.setColor(Color.WHITE);
		
		super.render(gl2);
		
		gl2.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		gl2.drawString("Options", Game.WIDTH / 2 - 90, Game.HEIGHT / 2 - 180);
		gl2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
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
		
		if(Options.ANTIALASING == RenderingHints.VALUE_ANTIALIAS_DEFAULT) ANTIALASING = 0;
		else if(Options.ANTIALASING == RenderingHints.VALUE_ANTIALIAS_ON) ANTIALASING = 1;
		else if(Options.ANTIALASING == RenderingHints.VALUE_ANTIALIAS_OFF) ANTIALASING = 2;
		
		if(Options.COLOR_RENDER == RenderingHints.VALUE_COLOR_RENDER_DEFAULT) COLOR_RENDER = 0;
		else if(Options.COLOR_RENDER == RenderingHints.VALUE_COLOR_RENDER_SPEED) COLOR_RENDER = 1;
		else if(Options.COLOR_RENDER == RenderingHints.VALUE_COLOR_RENDER_QUALITY) COLOR_RENDER = 2;
		
		if(Options.INTERPOLATION == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) INTERPOLATION = 0;
		else if(Options.INTERPOLATION == RenderingHints.VALUE_INTERPOLATION_BILINEAR) INTERPOLATION = 1;
		else if(Options.INTERPOLATION == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) INTERPOLATION = 2;
		
		int xOffset = (Game.WIDTH)/2;
		int yOffset = (Game.HEIGHT)/2 - 100;
		
		//FIXME Make Slider output the right values...
		slider1 = new GSlider(xOffset - 60, yOffset - 20, 150, 40, 1, 10, Options.RENDER_PASSES, GSlider.MODE_INT);
		
		switcherAA = new GSwitcher(xOffset - 60, yOffset + 30, 120, 30, new String[]{"default", "ON", "OFF"}, ANTIALASING);
		switcherCR = new GSwitcher(xOffset - 60, yOffset + 70, 120, 30, new String[]{"default", "speed", "quality"}, COLOR_RENDER);
		switcherIP = new GSwitcher(xOffset - 60, yOffset + 110, 120, 30, new String[]{"Next Neighbor", "bilinear", "bicubic"}, INTERPOLATION);
		
		this.controlsList.add(slider1);		
		this.controlsList.add(switcherAA);
		this.controlsList.add(switcherCR);
		this.controlsList.add(switcherIP);
		
		this.controlsList.add(new GButton(xOffset - 100, yOffset + 170, 100, 30, this, "Apply"));
		this.controlsList.add(new GButton(xOffset + 10, yOffset + 170, 100, 30, this, "Cancel"));
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
			Options.RENDER_PASSES = slider1.xScroll;
			
			switch(switcherAA.modePointer)
			{
			case 0: Options.ANTIALASING = RenderingHints.VALUE_ANTIALIAS_DEFAULT; break;
			case 1: Options.ANTIALASING = RenderingHints.VALUE_ANTIALIAS_ON; break;
			case 2: Options.ANTIALASING = RenderingHints.VALUE_ANTIALIAS_OFF; break;
			}
			
			switch(switcherCR.modePointer)
			{
			case 0: Options.COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_DEFAULT; break;
			case 1: Options.COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_SPEED; break;
			case 2: Options.COLOR_RENDER = RenderingHints.VALUE_COLOR_RENDER_QUALITY; break;
			}
			
			switch(switcherIP.modePointer)
			{
			case 0: Options.INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR; break;
			case 1: Options.INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BILINEAR; break;
			case 2: Options.INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC; break;
			}
			
			Options.safe();
			Gui.setGui(new GuiMain());
		}
	}
}
