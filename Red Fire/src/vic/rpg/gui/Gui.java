package vic.rpg.gui;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.gui.controls.GControl;
import vic.rpg.registry.GameRegistry;

/**
 * Gui is the basic gui framework used.
 * @author Victorious3
 *
 */
public class Gui
{
	/**
	 * The currently active Gui. It is the one receiving all input events.
	 */
	public static Gui currentGui = null;
	
	/**
	 * Sets the currently active Gui. It also reverts the cursor to {@link Cursor#getDefaultCursor()} and calls {@link #initGui()} for the new Gui.
	 * If a {@code null} is given, the currently active Gui is set to {@link GuiIngame#gui}.
	 * @see #currentGui
	 * @param gui
	 */
	public static void setGui(Gui gui)
	{
		currentGui = gui;
		if(gui == null)
		{
			currentGui = GuiIngame.gui;
		}
		Game.frame.setCursor(Cursor.getDefaultCursor());
		currentGui.initGui();
	}
	
	/**
	 * All {@link GControl GControls} active.
	 */
	protected List<GControl> controlsList = Collections.synchronizedList(new ArrayList<GControl>()); 
	
	public Gui(boolean pauseGame, boolean overridesEsc)
	{
		this.pauseGame = pauseGame;
		this.overridesEsc = overridesEsc; 
	}
	
	public Gui(boolean pauseGame)
	{
		this.pauseGame = pauseGame;
	}
	
	public boolean pauseGame = false;
	public boolean overridesEsc = false;
	
	/**
	 * The main render loop. It also controls rendering of the {@link GControl GControls}.
	 * @see #controlsList
	 * @param gl2
	 */
	public void render(GL2 gl2)
	{
		synchronized(controlsList)
		{
			Iterator<GControl> i = controlsList.iterator();
			
			while(i.hasNext())
			{
				GControl gc = i.next();
				if(gc.isVisible) gc.render(gl2, GameRegistry.mouse.xCoord, GameRegistry.mouse.yCoord);
			}
		}
	}
	
	/**
	 * The second render loop called after {@link #render(GL2)}. It also controls post-rendering of the {@link GControl GControls}.
	 * @see #controlsList
	 * @param gl2
	 */
	public void postRender(GL2 gl2)
	{
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible) gc.postRender(gl2, GameRegistry.mouse.xCoord, GameRegistry.mouse.yCoord);
			}
		}
	}
	
	/**
	 * Called when any {@link Mouse} button is pressed down. Calls {@link GControl#onClickStart(int, int, int)} on every active {@link GControl}.
	 * @see #controlsList
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onMouseClickStart(int x, int y, int mouseButton)
	{
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible)
				{
					if(x >= gc.xCoord && x <= gc.xCoord + gc.width && y >= gc.yCoord && y <= gc.yCoord + gc.height)
					{
						gc.onClickStart(x, y, mouseButton);
						gc.mouseDown = true;
					}
					else
					{
						gc.onClickEnd(x, y);
					}
				}
			}
		}
	}
	
	/**
	 * Called when any {@link Mouse} button is getting released. Calls {@link GControl#onClickEnd(int, int, int)} on every active {@link GControl}.
	 * @see #controlsList
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onMouseClickEnd(int x, int y, int mouseButton)
	{
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible && gc.mouseDown)
				{
					gc.onClickReleased(x, y, mouseButton);
					gc.mouseDown = false;
				}
			}
		}
	}
	
	/**
	 * Called when any double click happens. Calls {@link GControl#onDoubleClick(int, int, int)} on every active {@link GControl}.
	 * @see #controlsList
	 * @param x
	 * @param y
	 * @param mouseButton
	 */
	public void onDoubleClick(int x, int y, int mouseButton)
	{
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible)
				{
					gc.onDoubleClick(x, y, mouseButton);
				}
			}
		}
	}
	
	/**
	 * Is getting called when a Gui is set as active Gui with {@link #setGui(Gui)}.
	 */
	public void initGui(){}
	
	/**
	 * Is getting called when a Key is typed. Calls {@link GControl#onKeyTyped(char, int)} on every active {@link GControl}
	 * @see #controlsList
	 * @param k - the character typed
	 * @param keyCode - the key code from {@link KeyEvent}
	 */
	public void onKeyTyped(char k, int keyCode)
	{
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible) gc.onKeyTyped(k, keyCode);
			}
		}
	}
	
	/**
	 * Is getting called when the {@link Mouse} wheel is moved. Calls {@link GControl#onMouseWheelMoved(int)} on every active {@link GControl}
	 * @see #controlsList
	 * @param amount
	 */
	public void onMouseWheelMoved(int amount)
	{
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible) gc.onMouseWheelMoved(amount);
			}
		}
	}
	
	/**
	 * Is getting called by the main update loop {@link Game#tick()} every 0.2 seconds. Used for time consuming calculations to not slow down {@link #render(GL2)}.
	 * Handles mouse hover for the active {@link GControl GControls}.
	 * @see #controlsList
	 */
	public void updateGui()
	{
		int x = GameRegistry.mouse.xCoord;
		int y = GameRegistry.mouse.yCoord;
		
		synchronized(controlsList)
		{
			for(GControl gc : controlsList)
			{
				if(gc.isVisible)
				{			
					gc.tick();
					if(x >= gc.xCoord && x <= gc.xCoord + gc.width && y >= gc.yCoord && y <= gc.yCoord + gc.height)
					{
						gc.mouseHovered = true;
					}
					else if(gc.mouseHovered) gc.mouseHovered = false;
				}
			}
		}
	}
}