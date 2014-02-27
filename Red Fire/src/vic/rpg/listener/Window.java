package vic.rpg.listener;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import vic.rpg.Game;

/**
 * Used to change the resolution when the Game window is getting resized.
 * @author Victorious3
 */
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
		if(Game.RES_WIDTH != Game.game.getWidth() || Game.RES_HEIGHT != Game.game.getHeight())
		{
			Game.RES_WIDTH = Game.game.getWidth();
			Game.RES_HEIGHT = Game.game.getHeight();			
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) 
	{

	}

}
