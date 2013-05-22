package vic.rpg.gui;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
	
	public void render(Graphics2D g2d)
	{
		synchronized(controlsList)
		{
			Iterator<GControl> i = controlsList.iterator();
			
			while(i.hasNext())
			{
				GControl gc = i.next();
				if(gc.isVisible) gc.render(g2d, GameRegistry.mouse.xCoord, GameRegistry.mouse.yCoord);
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
					gc.onClickStart(x, y);
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
				gc.onClickReleased(x, y);
				gc.mouseDown = false;
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