package vic.rpg.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

import vic.rpg.Game;
import vic.rpg.gui.Gui;

/**
 * Listens for mouse input on the Client.
 * @author Victorious3
 */
public class Mouse implements MouseListener, MouseMotionListener, MouseInputListener, MouseWheelListener {

	public boolean isRightDown = false;
	public boolean isLeftDown = false;
	public boolean isMiddleDown = false;
	
	public int xCoord = 0;
	public int yCoord = 0;
	
	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		this.mouseMoved(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		double xRes = (double)Game.WIDTH / (double)Game.RES_WIDTH;
		double yRes = (double)Game.HEIGHT / (double)Game.RES_HEIGHT;
		
		xCoord = (int) (arg0.getX() * xRes);
		yCoord = (int) (arg0.getY() * yRes);
		
		if(Game.level != null)
		{
			Game.level.onMouseMoved(xCoord, yCoord);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		if(Game.level != null)
		{
			Game.level.onMouseClicked(xCoord, yCoord, arg0.getButton());
		}
		if(Gui.currentGui != null && arg0.getClickCount() > 1)
		{
			Gui.currentGui.onDoubleClick(this.xCoord, this.yCoord, arg0.getButton());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{

	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		switch(arg0.getButton())
		{
		case MouseEvent.BUTTON1 : isLeftDown = true; break;
		case MouseEvent.BUTTON3 : isRightDown = true; break;
		case MouseEvent.BUTTON2 : isMiddleDown = true; break;
		}
		
		if(Gui.currentGui != null && arg0.getClickCount() == 1) Gui.currentGui.onMouseClickStart(this.xCoord, this.yCoord, arg0.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		switch(arg0.getButton())
		{
		case MouseEvent.BUTTON1 : isLeftDown = false; break;
		case MouseEvent.BUTTON3 : isRightDown = false; break;
		case MouseEvent.BUTTON2 : isMiddleDown = false; break;
		}
		
		Gui.currentGui.onMouseClickEnd(this.xCoord, this.yCoord, arg0.getButton());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) 
	{
		if(Gui.currentGui != null)Gui.currentGui.onMouseWheelMoved(arg0.getWheelRotation() * arg0.getScrollAmount());
	}
}
