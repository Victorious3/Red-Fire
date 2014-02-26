package vic.rpg.gui.controls;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * GSwitcher is a switcher that cycles through all the given modes on click.
 * @author Victorious3
 */
public class GSwitcher extends GControl 
{
	public String[] modes;
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
		DrawUtils.setGL(gl2);
		if(this.mouseDown)DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(158, 31, 74));
		else if(this.mouseHovered)DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(130, 91, 213));
		else DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(68, 21, 150));
		
		float thickness = 3;
		float oldThickness = DrawUtils.getLineWidth();
		DrawUtils.setLineWidth(thickness);		
		DrawUtils.drawRect(xCoord, yCoord, width, height, new Color(120, 31, 0));
		DrawUtils.setLineWidth(oldThickness);
		
		DrawUtils.setFont(RenderRegistry.RPGFont);
		
		TextRenderer tr = DrawUtils.getTextRenderer();		
		int sWidth = (int) tr.getBounds(currMode).getWidth();
		int sHeight = (int) (tr.getBounds(currMode).getHeight() / 2);
		
		DrawUtils.drawUnformattedString(xCoord + (width - sWidth)/2, yCoord + (height + sHeight)/2, currMode, Color.white);
		
		super.render(gl2, x, y);
	}

	@Override
	public void onClickReleased(int x, int y, int mouseButton) 
	{
		modePointer++;	
		if (modePointer >= maxMode) modePointer = 0;
		currMode = modes[modePointer];	
	}
}
