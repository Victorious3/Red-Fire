package vic.rpg.gui.controls;

import java.awt.Color;

import javax.media.opengl.GL2;

import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.DrawUtils.GradientAnimator;

public class GButton extends GControl {

	public IGButton handler;
	public String name;
	
	public GButton(int xCoord, int yCoord, int width, int height, IGButton handler, String name) 
	{
		super(xCoord, yCoord, width, height);
		this.handler = handler;
		this.name = name;
	}

	private GradientAnimator gAnimIN = DrawUtils.createGratientAnimator(200, new Color(68, 21, 150), new Color(130, 91, 213));
	private GradientAnimator gAnimOUT = DrawUtils.createGratientAnimator(200, new Color(130, 91, 213), new Color(68, 21, 150)).forward();

	@Override
	public void render(GL2 gl2, int x, int y) 
	{	
		DrawUtils.setGL(gl2);
		
		if(this.mouseDown)DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(158, 31, 74));
		else if(this.mouseHovered)
		{
			gAnimOUT.reset();		
			gAnimIN.animate();
			DrawUtils.fillRect(xCoord, yCoord, width, height, gAnimIN.getColor());
		}
		else
		{
			gAnimIN.reset();
			gAnimOUT.animate();
			DrawUtils.fillRect(xCoord, yCoord, width, height, gAnimOUT.getColor());
		}
		
		float thickness = 3;
		float oldThickness = DrawUtils.getLineWidth();
		
		DrawUtils.setLineWidth(thickness);
		DrawUtils.drawRect(xCoord, yCoord, width, height, new Color(120, 31, 0));
		DrawUtils.setLineWidth(oldThickness);
		
		DrawUtils.setFont(RenderRegistry.RPGFont);		
		int sWidth = (int) DrawUtils.getTextRenderer().getBounds(name).getWidth();
		int sHeight = (int) DrawUtils.getTextRenderer().getBounds(name).getHeight() / 2;
		DrawUtils.drawString(xCoord + (width - sWidth)/2, yCoord + (height + sHeight)/2, name, Color.white);
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
