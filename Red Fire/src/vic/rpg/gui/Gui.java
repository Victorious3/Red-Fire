package vic.rpg.gui;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.controls.GControl;
import vic.rpg.registry.GameRegistry;

public class Gui 
{
	public static Gui currentGui = null;
	
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
	
	public List<GControl> controlsList = Collections.synchronizedList(new ArrayList<GControl>()); 
	
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
	
	public void onMouseClickStart(int x, int y, int mouseButton)
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
	
	public void onMouseClickEnd(int x, int y, int mouseButton)
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
	
	public void onDoubleClick(int x, int y, int mouseButton)
	{
		for(GControl gc : controlsList)
		{
			if(gc.isVisible)
			{
				gc.onDoubleClick(x, y, mouseButton);
			}
		}
	}
	
	public void initGui(){}
	
	public void keyTyped(char k, int keyCode)
	{
		for(GControl gc : controlsList)
		{
			if(gc.isVisible) gc.onKeyPressed(k, keyCode);
		}
	}
	
	public void onMouseWheelMoved(int amount)
	{
		for(GControl gc : controlsList)
		{
			if(gc.isVisible) gc.onMouseWheelMoved(amount);
		}
	}
	
	public void updateGui()
	{
		int x = GameRegistry.mouse.xCoord;
		int y = GameRegistry.mouse.yCoord;
		
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