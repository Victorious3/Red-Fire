package vic.rpg.gui.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Stroke;

import javax.media.opengl.GL2;

import vic.rpg.registry.RenderRegistry;

public class GSwitcher extends GControl 
{
	private String[] modes;
	private int maxMode;
	
	public String currMode;
	public int modePointer;
	
	public GSwitcher(int xCoord, int yCoord, int width, int height, String[] modes, int modePointer) 
	{
		super(xCoord, yCoord, width, height);

		this.modes = modes;
		this.modePointer = modePointer;
		this.currMode = modes[modePointer];
		this.maxMode = modes.length;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{
		if(this.mouseDown)gl2.setColor(new Color(158, 31, 74));
		else if(this.mouseHovered)gl2.setColor(new Color(130, 91, 213));
		else gl2.setColor(new Color(68, 21, 150));
		
		gl2.fillRect(xCoord, yCoord, width, height);
		
		gl2.setColor(new Color(120, 31, 0));		
		float thickness = 3;
		Stroke oldStroke = gl2.getStroke();
		gl2.setStroke(new BasicStroke(thickness));		
		gl2.drawRect(xCoord, yCoord, width, height);
		gl2.setStroke(oldStroke);
		
		gl2.setColor(Color.white);
		gl2.setFont(RenderRegistry.RPGFont);
		
		FontMetrics fm = gl2.getFontMetrics();		
		int sWidth = fm.stringWidth(currMode);
		int sHeight = fm.getHeight() / 2;
		
		gl2.drawString(currMode, xCoord + (width - sWidth)/2, yCoord + (height + sHeight)/2);
		
		super.render(gl2, x, y);
	}

	@Override
	public void onClickStart(int x, int y) 
	{
		super.onClickStart(x, y);
	}

	@Override
	public void onClickReleased(int x, int y) 
	{	
		modePointer++;	
		if (modePointer >= maxMode) modePointer = 0;
		currMode = modes[modePointer];
	}
}
