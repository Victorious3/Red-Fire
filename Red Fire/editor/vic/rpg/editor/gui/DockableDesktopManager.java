package vic.rpg.editor.gui;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;

public class DockableDesktopManager extends DefaultDesktopManager implements ComponentListener
{
	public void dragFrame(JComponent component, int xCoord, int yCoord) 
	{
		if(component instanceof JDockableFrame) 
		{
			JDockableFrame frame = (JDockableFrame) component;
			JDesktopPane desktop = frame.getDesktopPane();
			
			int width = desktop.getSize().width;
			int height = desktop.getSize().height;
			int w = component.getSize().width;
			int h = component.getSize().height;
						
			frame.clearDocks();
			
			if(xCoord <= 10) 
			{
				xCoord = 0;
				frame.addDock(JDockableFrame.WEST);
	      	}
			if(xCoord + w + 10 >= width) 
			{
				xCoord = width - w;
				frame.addDock(JDockableFrame.EAST);
			}
			if(yCoord <= 10) 
			{
				yCoord = 0;
				frame.addDock(JDockableFrame.NORTH);
			}
			if(yCoord + h + 10 >= height) 
			{
				yCoord = height - h;
				frame.addDock(JDockableFrame.SOUTH);
			}
	      	
			super.dragFrame(component, xCoord, yCoord);
		}
	}

	public void resizeFrame(JComponent component, int xCoord, int yCoord, int w, int h) 
	{
		if(component instanceof JDockableFrame) 
		{
			JDockableFrame frame = (JDockableFrame)component;
			JDesktopPane desktop = frame.getDesktopPane();
			
			int width = desktop.getSize().width;
			int height = desktop.getSize().height;
			
			if(xCoord < 0) 
			{
				w += xCoord;
			}
			if(xCoord + w > width) 
			{
				w = width - xCoord;
			}
			if(yCoord < 0) 
			{
				h += yCoord;
			}
			if(yCoord + h > height) 
			{
				h = height - yCoord;
			}
			if(xCoord < 0) 
			{
				xCoord = 0;
			}
			if(xCoord + w > width) 
			{
				w = width - xCoord;
			}
			if(yCoord < 0) 
			{
				yCoord = 0;
			}
			if(yCoord + h > height) 
			{
				yCoord = height - h;
			}
		}
		super.resizeFrame(component, xCoord, yCoord, w, h);
	}

	@Override public void componentHidden(ComponentEvent arg0) {}
	@Override public void componentMoved(ComponentEvent arg0) {}

	@Override 
	public void componentResized(ComponentEvent arg0) 
	{
		Component comp = arg0.getComponent();
		if(comp instanceof JDesktopPane)
		{
			JDesktopPane desktop = (JDesktopPane) comp;
			
			int width = desktop.getWidth();
			int height = desktop.getHeight();
			
			for(Component innerComp : desktop.getComponents())
			{
				if(innerComp instanceof JDockableFrame)
				{
					JDockableFrame frame = (JDockableFrame) innerComp;
					ArrayList<Integer> docks = frame.getDocks();
					
					int x = frame.getX();
					int y = frame.getY();
					
					if(docks.contains(JDockableFrame.WEST))
					{
						x = 0;
					}
					if(docks.contains(JDockableFrame.EAST))
					{
						x = width - frame.getWidth();
					}
					if(docks.contains(JDockableFrame.NORTH))
					{
						y = 0;
					}
					if(docks.contains(JDockableFrame.SOUTH))
					{
						y = height - frame.getHeight();
					}		
					frame.setLocation(x, y);
					
					
					if(frame.getX() + frame.getWidth() > width)
					{
						frame.setLocation(width - frame.getWidth(), frame.getY());
					}
					if(frame.getY() + frame.getHeight() > height)
					{
						frame.setLocation(frame.getX(), height - frame.getHeight());
					}
					if(frame.getX() < 0)
					{
						frame.setLocation(0, frame.getY());
					}
					if(frame.getY() < 0)
					{
						frame.setLocation(frame.getX(), 0);
					}				
				}
			}
		}
	}

	@Override public void componentShown(ComponentEvent arg0) {}
}
