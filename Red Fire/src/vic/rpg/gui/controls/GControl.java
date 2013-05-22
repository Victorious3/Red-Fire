package vic.rpg.gui.controls;

import java.awt.Graphics2D;

public class GControl {

	public int xCoord;
	public int yCoord;
	public int width;
	public int height;
	
	public boolean isVisible = true;
	public boolean mouseDown = false;
	public boolean mouseHovered = false;
	
	public GControl(int xCoord, int yCoord, int width, int height)
	{
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.height = height;
		this.width = width;	
	}
	
	public void render(Graphics2D g2d, int x, int y){}
	
	public void onClickStart(int x, int y){}
	
	public void onClickReleased(int x, int y){}
	
	public void onKeyPressed(char k, int keyCode){}

	public void onClickEnd(int x, int y) {}

	public void tick() {}
}
