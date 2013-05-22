package vic.rpg.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import vic.rpg.Game;
import vic.rpg.gui.Gui;
import vic.rpg.gui.GuiDebug;
import vic.rpg.gui.GuiIngame;
import vic.rpg.gui.GuiIngameMenu;
import vic.rpg.gui.GuiPlayer;

public class Key implements KeyListener {

	public char currChar = 0;
	public int currKey = 0;
	
	public boolean APressed = false;
	public boolean WPressed = false;
	public boolean SPressed = false;
	public boolean DPressed = false;
	
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		currChar = arg0.getKeyChar();
		currKey = arg0.getKeyCode();
		
		if(arg0.getKeyCode() == KeyEvent.VK_A) APressed = true;
		if(arg0.getKeyCode() == KeyEvent.VK_W) WPressed = true;
		if(arg0.getKeyCode() == KeyEvent.VK_S) SPressed = true;
		if(arg0.getKeyCode() == KeyEvent.VK_D) DPressed = true;

	}

	@Override
	public void keyReleased(KeyEvent arg0) 
	{
		currChar = 0;
		currKey = 0;

		if(arg0.getKeyCode() == KeyEvent.VK_A) APressed = false;
		if(arg0.getKeyCode() == KeyEvent.VK_W) WPressed = false;
		if(arg0.getKeyCode() == KeyEvent.VK_S) SPressed = false;
		if(arg0.getKeyCode() == KeyEvent.VK_D) DPressed = false;
		
		if(arg0.getKeyCode() == KeyEvent.VK_F3)
		{
			if(Gui.currentGui != null)
			{
				if(Gui.currentGui instanceof GuiDebug)
				{
					Gui.setGui(null);
				}
				else
				{
					Gui.setGui(new GuiDebug());
				}
			}
			else 
			{
				Gui.setGui(new GuiDebug());
			}			
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_I)
		{
			if(Gui.currentGui != null)
			{
				if(Gui.currentGui instanceof GuiPlayer)
				{
					Gui.setGui(null);
				}
				else if(Gui.currentGui instanceof GuiIngame)
				{
					if(!((GuiIngame)Gui.currentGui).chatField.isVisible) Gui.setGui(new GuiPlayer());
				}
			}
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(Gui.currentGui instanceof GuiIngame)
			{
				Gui.setGui(new GuiIngameMenu());
			}				
			else if(!Gui.currentGui.overridesEsc) 
			{
				Gui.setGui(null);
			}			
		}
		
		if(Gui.currentGui != null) Gui.currentGui.keyTyped(arg0.getKeyChar(), arg0.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		if(Game.level != null)
		{
			Game.level.onKeyPressed(arg0);
		}
	}

}
