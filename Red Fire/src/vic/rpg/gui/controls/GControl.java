package vic.rpg.gui.controls;

import javax.media.opengl.GL2;

public class GControl {

	public int xCoord;
	public int yCoord;
	public int width;
	public int height;
	
	public boolean isVisible = true;
	public boolean mouseDown = false;
	public boolean mouseHovered = false;
	
	protected GControl(int xCoord, int yCoord, int width, int height)
	{
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.height = height;
		this.width = width;	
	}
	
	public void render(GL2 gl2, int x, int y){}
	
	public void postRender(GL2 gl2, int x, int y){}
	
	public void onClickStart(int x, int y, int mouseButton){}
	
	public void onClickReleased(int x, int y, int mouseButton){}
	
	public void onKeyTyped(char k, int keyCode){}

	public void onClickEnd(int x, int y) {}
	
	public void onDoubleClick(int x, int y, int mouseButton){}

	public void tick() {}

	public void onMouseWheelMoved(int amount) {}
}
