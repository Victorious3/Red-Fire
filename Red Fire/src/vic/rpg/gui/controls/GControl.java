package vic.rpg.gui.controls;

import javax.media.opengl.GL2;

import vic.rpg.editor.listener.Mouse;
import vic.rpg.gui.Gui;

/**
 * GControl offers a framework for all {@link Gui} elements like buttons, text boxes and more. It can't work on its own.
 * @author Victorious3
 */
public abstract class GControl {

	public int xCoord;
	public int yCoord;
	public int width;
	public int height;
	
	public boolean isVisible = true;
	public boolean isLocked = false;
	public boolean mouseDown = false;
	public boolean mouseHovered = false;
	
	protected GControl(int xCoord, int yCoord, int width, int height)
	{
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.height = height;
		this.width = width;	
	}
	
	/**
	 * Prevents this GControl from receiving any Events from the parent {@link Gui}. Used for Animation.
	 * @return this
	 */
	public GControl lock(boolean locked)
	{
		this.isLocked = locked;
		return this;
	}
	
	/**
	 * Renders the GControl. Parameters are the mouse coordinates.
	 * @param gl2
	 * @param x
	 * @param y
	 */
	public void render(GL2 gl2, int x, int y){}
	
	/**
	 * Renders the GControl, called after {@link #render(GL2, int, int)}. Parameters are the mouse coordinates.
	 * @param gl2
	 * @param x
	 * @param y
	 */
	public void postRender(GL2 gl2, int x, int y){}
	
	/**
	 * Called when any {@link Mouse} button is pressed down within the bounds of this GControl.
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onClickStart(int x, int y, int mouseButton){}
	
	/**
	 * Called when any {@link Mouse} button is getting released.
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onClickReleased(int x, int y, int mouseButton){}
	
	/**
	 * Called when a Key is typed.
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onKeyTyped(char k, int keyCode){}

	/**
	 * Called when any {@link Mouse} button is pressed down and the mouse coordinates are outside of the bounds. 
	 * @param x
	 * @param y
	 */
	public void onClickEnd(int x, int y) {}
	
	/**
	 * Called when any double click happens.
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onDoubleClick(int x, int y, int mouseButton){}

	/**
	 * This gets updated every 0.2 seconds by the currently active {@link Gui}. Used for time consuming calculations to not slow down {@link #render(GL2, int, int)}
	 */
	public void tick() {}

	/**
	 * Called when the {@link Mouse} wheel is moved.
	 * @param amount
	 */
	public void onMouseWheelMoved(int amount) {}
}
