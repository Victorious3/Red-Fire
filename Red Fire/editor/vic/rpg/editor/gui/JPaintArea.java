package vic.rpg.editor.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import vic.rpg.registry.GameRegistry;
import vic.rpg.utils.Utils;

public class JPaintArea extends JBackgroundPanel implements MouseListener, MouseMotionListener
{
	private BufferedImage img;
	public ArrayList<Point> currPoly = new ArrayList<Point>();
	public ArrayList<ArrayList<Point>> polys = new ArrayList<ArrayList<Point>>(); 
	
	public int offX = 0;
	public int offY = 0;
	
	public JPaintArea(Image img)
	{	
		super(Utils.readImageFromJar("/vic/rpg/resources/editor/transparent_bg.png"));
		this.img = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		this.img.getGraphics().drawImage(img, 0, 0, null);
		this.polys.add(currPoly);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);		
	}
	
	public void createNewPoly()
	{
		currPoly = new ArrayList<Point>();
		polys.add(currPoly);
		this.updateUI();
	}
	
	public void removeCurrentPoly()
	{
		if(polys.size() > 1) polys.remove(currPoly);
		this.updateUI();
	}
	
	public void changePoly(int i)
	{
		int index = polys.indexOf(currPoly);	
		index += i;
		
		if(index >= 0 && index < polys.size())
		{
			currPoly = polys.get(index);
		}
		this.updateUI();
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.drawImage(img, offX, offY, this);
		g2d.setColor(Color.yellow);
		g2d.setStroke(new BasicStroke(3));
		
		for(ArrayList<Point> currPoly : polys)
		{
			if(currPoly == this.currPoly) g2d.setColor(Color.red);
			else g2d.setColor(Color.yellow); 
			for(Point p : currPoly)
			{
				if(currPoly.indexOf(p) != currPoly.size() - 1)
				{
					Point p2 = currPoly.get(currPoly.indexOf(p) + 1);
					g2d.drawLine((int)p.getX() + offX, (int)p.getY() + offY, (int)p2.getX() + offX, (int)p2.getY() + offY);
				}
				else
				{
					Point p2 = currPoly.get(0);
					g2d.drawLine((int)p.getX() + offX, (int)p.getY() + offY, (int)p2.getX() + offX, (int)p2.getY() + offY);
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{	
		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(!currPoly.contains(new Point(arg0.getX() - offX, arg0.getY() - offY)))
			{
				currPoly.add(new Point(arg0.getX() - offX, arg0.getY() - offY));
			}
		}
		if(arg0.getButton() == MouseEvent.BUTTON3)
		{
			currPoly.clear();
		}
		this.updateUI();
	}

	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	
	int preX;
	int preY;
	
	@Override 
	public void mousePressed(MouseEvent arg0)
	{
		preX = arg0.getX();
		preY = arg0.getY();
	}
	
	@Override 
	public void mouseReleased(MouseEvent arg0) 
	{
		this.setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if(e.isShiftDown())
		{
			this.offX += (e.getX() - preX);
			this.offY += (e.getY() - preY);
			
			preX = e.getX();
			preY = e.getY();
			this.updateUI();
			this.setCursor(GameRegistry.CURSOR_DRAG);
		}
	}

	@Override public void mouseMoved(MouseEvent e) {}
}
