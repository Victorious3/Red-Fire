package vic.rpg.gui.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class GSlider extends GControl {

	public float minS;
	public float maxS;
	
	public float returnValue;
	public int mode;
	
	public float xScroll = 0.0F;
	private boolean isScrolling = false;
	
	public static final int MODE_INT = 0;
	public static final int MODE_FLOAT = 1;
	public static final int MODE_PERCENT = 2;
	
	public GSlider(int xCoord, int yCoord, int width, int height, float minS, float maxS, float xScroll, int mode) 
	{
		super(xCoord, yCoord, width, height);
		
		this.minS = minS;
		this.maxS = maxS;
		this.mode = mode;
		
		this.xScroll = xScroll; 
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) 
	{
		g2d.setColor(new Color(97, 74, 119, 190));
		g2d.fillRect(xCoord, yCoord, width, height);
		
		if(this.mouseDown)g2d.setColor(new Color(158, 31, 74));
		else if(this.mouseHovered)g2d.setColor(new Color(130, 91, 213));
		else g2d.setColor(new Color(68, 21, 150));
		g2d.fillRect(xCoord + (int)((float)(width - 17) * this.xScroll), yCoord, 16, height);
		
		g2d.setColor(new Color(120, 31, 0));		
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(3.0F));		
		g2d.drawRect(xCoord, yCoord, width, height);
		g2d.setStroke(oldStroke);
		
		String s = null;
		
		switch(mode)
		{
		case GSlider.MODE_INT : s = String.valueOf((int)returnValue); break; 
		case GSlider.MODE_FLOAT : s = String.valueOf(Math.round(returnValue * 100) / 100); break;
		case GSlider.MODE_PERCENT : s = String.valueOf((int)returnValue) + "%"; break;  
		}
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Veranda", 2, 18));
		
		FontMetrics fm = g2d.getFontMetrics();		
		int sWidth = fm.stringWidth(String.valueOf((int)returnValue));
		int sHeight = fm.getHeight() / 2;		
		g2d.drawString(s, xCoord + (width - sWidth)/2, yCoord + (height + sHeight)/2);

		if(isScrolling)
		{
			xScroll = (float)(x - xCoord) / (float)(width);
			
			if(xScroll < 0.0F) xScroll = 0.0F;
			if(xScroll > 1.0F) xScroll = 1.0F;
		}
				
		returnValue = returnValue(minS, maxS, xScroll);
	}

	@Override
	public void onClickStart(int x, int y) 
	{
		this.isScrolling = true;
	}

	@Override
	public void onClickReleased(int x, int y) 
	{
		this.isScrolling = false;
	}
	
	public static float returnValue(float minS, float maxS, float xScroll)
	{
		return (maxS - xScroll)* xScroll + minS;
	}
	
}
