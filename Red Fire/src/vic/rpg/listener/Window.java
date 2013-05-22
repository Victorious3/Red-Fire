package vic.rpg.listener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import vic.rpg.Game;

public class Window implements ComponentListener {

	@Override
	public void componentHidden(ComponentEvent arg0) 
	{

	}

	@Override
	public void componentMoved(ComponentEvent arg0) 
	{

	}

	@Override
	public void componentResized(ComponentEvent arg0) 
	{
		if(Game.RES_WIDTH != Game.frame.getWidth() || Game.RES_HEIGHT != Game.frame.getHeight())
		{
			Game.RES_WIDTH = Game.frame.getWidth();
			Game.RES_HEIGHT = Game.frame.getHeight();			
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) 
	{

	}

}
