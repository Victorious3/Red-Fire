package vic.rpg.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;

import vic.rpg.Game;
import vic.rpg.gui.controls.GButton;
import vic.rpg.registry.GameRegistry;
import vic.rpg.utils.Utils;

public class GuiIngameMenu extends Gui implements GButton.IGButton {

	private Image logo;
	private int xOffset;
	private int yOffset;
	
	public GuiIngameMenu() 
	{
		super(false);
	}

	@Override
	public void render(Graphics2D g2d) 
	{		
		g2d.setColor(new Color(80, 80, 80, 180));
		g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		
		g2d.drawImage(logo, xOffset, yOffset, null);
		
		g2d.setColor(new Color(120, 31, 0));

		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(3.0F));
		g2d.drawRect(xOffset, yOffset, 544, 268);		
		g2d.setStroke(oldStroke);
		
		g2d.setFont(new Font("Veranda", 0, 20));
		g2d.setColor(Color.white);
		g2d.drawString("Red Fire V." + GameRegistry.VERSION, 5, 20);
		
		super.render(g2d);
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

		logo = Utils.readImageFromJar("/vic/rpg/resources/Red Fire.png");	
		
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
