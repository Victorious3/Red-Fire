package vic.rpg.editor.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import vic.rpg.editor.EntityEditor;
import vic.rpg.registry.GameRegistry;
import vic.rpg.utils.Utils;

public class PanelEntity extends JBackgroundPanel implements MouseListener, MouseMotionListener
{
	private BufferedImage img;
	private EntityEditor editor;
	public ArrayList<Point> currPoly = new ArrayList<Point>();
	public ArrayList<ArrayList<Point>> polys = new ArrayList<ArrayList<Point>>();
	
	public int offX = 0;
	public int offY = 0;
	
	public PanelEntity(BufferedImage img, EntityEditor editor)
	{	
		super(Utils.readImageFromJar("/vic/rpg/resources/editor/transparent_bg.png"));
		this.img = img;
		this.polys.add(currPoly);
		this.editor = editor;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);		
	}
	
	public void setImage(BufferedImage i)
	{
		this.img = i;	
		this.updateUI();
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
	
	private String generateNewCollisionMethod()
	{
		String s = "";
		String n = System.getProperty("line.separator");	
		int i = 1;	
		
		s = s + 
		"\tpublic Area getCollisionBoxes(Area area)" + n +
		"\t{" + n;
		
		for(ArrayList<Point> poly : polys)
		{
			s = s + "\t\tPolygon p" + i + " = new Polygon();" + n;
			s = s + "\t\tp" + i + ".xpoints = new int[]{";
			
			int j = 1;
			for(Point p : poly)
			{
				if(j != 1) s = s + ", ";
				s = s + "xCoord + " + p.x;
				j += 1;
			}
			
			s = s + 
			"};" + n +
			"\t\tp" + i + ".ypoints = new int[]{";
			
			j = 1;
			for(Point p : poly)
			{
				if(j != 1) s = s + ", ";
				s = s + "yCoord + " + p.y;
				j += 1;			
			}
			
			s = s + 
			"};" + n +
			"\t\tp" + i +".npoints = p" + i + ".xpoints.length;" + n +
			n;
			
			i += 1;
		}		
		i = 1;
		
		for(@SuppressWarnings("unused") ArrayList<Point> poly : polys)
		{
			s = s + "\t\tarea.add(new Area(p" + i + "));" + n;
			i += 1;
		}
		
		s = s +
		n +
		"\t\treturn area;" + n +
		"\t}" + n;
		
		return s;
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);		
		Graphics2D g2d = (Graphics2D) g;
		
		if(editor.boxIsImageAuto.isSelected() && img != null) g2d.drawImage(img, offX, offY, this);		
		else 
		{
			int width = Integer.parseInt(editor.dimX);
			int height = Integer.parseInt(editor.dimY);
			try
			{
				width = Integer.parseInt(editor.fieldDimensionX.getText());
				height = Integer.parseInt(editor.fieldDimensionY.getText());
			} catch (Exception e) {}
			
			g2d.setColor(Color.black);
			g2d.fillRect(offX, offY, width, height);
		}
		g2d.setColor(Color.yellow);
		g2d.setStroke(new BasicStroke(3));
		
		if(editor.boxIsBoundsAuto.isSelected())
		{
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
	}

	@Override public void mouseClicked(MouseEvent arg0) {}
	@Override public void mouseEntered(MouseEvent arg0) {}
	@Override public void mouseExited(MouseEvent arg0) {}
	
	int preX;
	int preY;
	
	@Override 
	public void mousePressed(MouseEvent arg0)
	{
		preX = arg0.getX();
		preY = arg0.getY();
		
		if(arg0.isShiftDown()) this.setCursor(GameRegistry.CURSOR_DRAG);
		
		if(editor.boxIsBoundsAuto.isSelected() && !arg0.isShiftDown())
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
		
			String n = System.getProperty("line.separator");
			String s = generateNewCollisionMethod();
			int i1 = editor.editor.getText().indexOf("\tpublic Area getCollisionBoxes(Area area)");
			int i2 = editor.editor.getText().indexOf("\t\treturn area;" + n + "\t}" + n);
			editor.editor.replaceRange(s, i1, i2 + ("\t\treturn area;" + n + "\t}" + n).length());
		}
		this.updateUI();
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
		}
	}

	@Override public void mouseMoved(MouseEvent e) {}
}
