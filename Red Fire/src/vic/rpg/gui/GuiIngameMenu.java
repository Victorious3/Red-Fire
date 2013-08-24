package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.controls.GButton;
import vic.rpg.registry.GameRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;

public class GuiIngameMenu extends Gui implements GButton.IGButton {

	private Texture logo = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/Red Fire.png"));
	private int xOffset;
	private int yOffset;
	
	public GuiIngameMenu() 
	{
		super(false);
	}

	@Override
	public void render(GL2 gl2) 
	{		
		DrawUtils.setGL(gl2);
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, new Color(80, 80, 80, 180));
		
		DrawUtils.drawTexture(xOffset, yOffset, logo);
		
		float oldStroke = DrawUtils.getLineWidth();
		DrawUtils.setLineWidth(3F);
		DrawUtils.drawRect(xOffset, yOffset, 544, 268, new Color(120, 31, 0));		
		DrawUtils.setLineWidth(oldStroke);
		
		DrawUtils.setFont(new Font("Veranda", 0, 20));
		DrawUtils.drawString(5, 20, "Red Fire V." + GameRegistry.VERSION, Color.white);
		
		super.render(gl2);
	}

	@Override
	public void initGui() 
	{
		int width = 120;
		
		xOffset = (Game.WIDTH - 544)/2;
		yOffset = (Game.HEIGHT - 268)/2 - 40;
		
		this.controlsList.add(new GButton(16 + xOffset, yOffset + 15, width, 30, this, "Options"));
		this.controlsList.add(new GButton(16 + xOffset + 1*(width + 10), yOffset + 15, width, 30, this, "Save"));
		this.controlsList.add(new GButton(16 + xOffset + 2*(width + 10), yOffset + 15, width, 30, this, "Load"));
		this.controlsList.add(new GButton(16 + xOffset + 3*(width + 10), yOffset + 15, width, 30, this, "Quit to Title"));
		this.controlsList.add(new GButton(16 + xOffset, yOffset + 222, width, 30, this, "Continue"));

		super.initGui();
	}
	
	public void button()
	{
		System.out.println("Button pressed!");
	}

	@Override
	public void onButtonPressed(String name) 
	{
		if(name.equalsIgnoreCase("Options"))
		{
			Gui.setGui(new GuiOptions(this));
		}
		if(name.equalsIgnoreCase("Quit to Title"))
		{
			Game.netHandler.close();		
			Gui.setGui(new GuiMain());
		}
		if(name.equalsIgnoreCase("Continue"))
		{
			Gui.setGui(null);
		}
	}

}
