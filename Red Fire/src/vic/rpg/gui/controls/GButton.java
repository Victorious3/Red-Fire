package vic.rpg.gui.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import vic.rpg.registry.RenderRegistry;

public class GButton extends GControl {

	public IGButton handler;
	public String name;
	
	public GButton(int xCoord, int yCoord, int width, int height, IGButton handler, String name) 
	{
		super(xCoord, yCoord, width, height);
		this.handler = handler;
		this.name = name;
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) 
	{	
		if(this.mouseDown)g2d.setColor(new Color(158, 31, 74));
		else if(this.mouseHovered)g2d.setColor(new Color(130, 91, 213));
		else g2d.setColor(new Color(68, 21, 150));
		
		g2d.fillRect(xCoord, yCoord, width, height);
		
		g2d.setColor(new Color(120, 31, 0));		
		float thickness = 3;
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(thickness));		
		g2d.drawRect(xCoord, yCoord, width, height);
		g2d.setStroke(oldStroke);
		
		g2d.setColor(Color.white);
		g2d.setFont(RenderRegistry.RPGFont);
		
		FontMetrics fm = g2d.getFontMetrics();		
		int sWidth = fm.stringWidth(name);
		int sHeight = fm.getHeight() / 2;
		
		g2d.drawString(name, xCoord + (width - sWidth)/2, yCoord + (height + sHeight)/2);
	}

	@Override
	public void onClickStart(int x, int y) 
	{

	}

	@Override
	public void onClickReleased(int x, int y) 
	{
		if(this.mouseHovered) handler.onButtonPressed(name);
	}
	
	public static interface IGButton
	{
		public void onButtonPressed(String name);
	}
}
