package vic.rpg.editor.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import vic.rpg.editor.Editor;
import vic.rpg.editor.listener.Key;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.editor.listener.ZoomListener;
import vic.rpg.level.Editable;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;
import vic.rpg.level.path.Node;
import vic.rpg.level.path.NodeMap;

public class LabelLevel extends JLabel 
{
	private int needsUpdate = 0;
	private float scale = 1;
	
	private int offX = 0;
	private int offY = 0;
	private int width = 0;
	private int height = 0;
	
	private BufferedImage img;
	
	@Override
	public void paintComponent(Graphics g) 
	{		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.scale(scale, scale);
		
		if(Editor.editor == null) return;
		
		if(needsUpdate == 1 && Editor.editor.level != null)
		{
			Editor.editor.level.render((Graphics2D)img.getGraphics(), 0, 0, Editor.editor.level.getWidth(), Editor.editor.level.getHeight(), 0, 0);
		}
		if(needsUpdate == 2 && Editor.editor.level != null)
		{
			Editor.editor.level.render((Graphics2D)img.getGraphics(), offX, offY, width, height, 0, 0);
		}
		
		g2d.drawImage(img, 0, 0, null);
		
		if(Mouse.selectedEntities != null)
		{
			for(Entity e : Mouse.selectedEntities)
			{
				g2d.setColor(Color.yellow);
				Stroke stroke = g2d.getStroke();
				g2d.setStroke(new BasicStroke(5));
				g2d.drawRect(e.xCoord, e.yCoord, e.getWidth(), e.getHeight());
				g2d.setStroke(stroke);
			}
		}
		if(Mouse.selectedTiles != null)
		{
			for(Point p : Mouse.selectedTiles)
			{
				g2d.setColor(Color.yellow);
				Stroke stroke = g2d.getStroke();
				g2d.setStroke(new BasicStroke(5));
				g2d.drawRect(p.x * Level.CELL_SIZE, p.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
				g2d.setStroke(stroke);
			}
		}		
		if(Mouse.selection != null)
		{
			g2d.setColor(new Color(0, 100, 255, 100));
			g2d.fill(Mouse.selection);
			
			g2d.setColor(new Color(0, 0, 0, 100));
			Stroke stroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(1 / getScale() * 2));
			g2d.draw(Mouse.selection);
			g2d.setStroke(stroke);
		}		
		if(Editor.editor.buttonPath.isSelected() || Key.keyListener.button == 3)
		{
			g2d.setColor(new Color(255, 0, 0, 120));
			for(Node[] n2 : Editor.editor.level.nodeMap.nodes)
			{
				for(Node n : n2)
				{
					if(n.isBlocked)
					{
						Point p = n.toPoint();					
						g2d.fillRect(p.x, p.y, Level.CELL_SIZE, Level.CELL_SIZE);
					}
				}
			}
			
			g2d.setColor(new Color(255, 255, 0));
			for(Entity e : Editor.editor.level.entities.values())
			{
				g2d.draw(e.getCollisionBoxes(new Area()));
			}
			
			g2d.setColor(new Color(0, 255, 0, 120));
			if(Mouse.start != null) g2d.fillRect(Mouse.start.x * Level.CELL_SIZE, Mouse.start.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
			g2d.setColor(new Color(0, 255, 255, 120));
			if(Mouse.end != null) g2d.fillRect(Mouse.end.x * Level.CELL_SIZE, Mouse.end.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
			g2d.setColor(new Color(0, 0, 255, 120));
			
			if(Mouse.path != null)
			{
				while(Mouse.path.hasNext())
				{
					Node n = Mouse.path.next();
					g2d.fillRect(n.x * Level.CELL_SIZE, n.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
				}
				Mouse.path.revert();
			}
		}
		
		needsUpdate = 0;				
	}
	
	public void update(boolean onlySelection)
	{
		this.needsUpdate = onlySelection ? 3 : 1;
		this.updateUI();
	}
	
	public void update(int xOffset, int yOffset, int width, int height)
	{
		this.needsUpdate = 2;
		
		this.offX = xOffset;
		this.offY = yOffset;
		this.width = width;
		this.height = height;
		
		this.updateUI();
	}
	
	public void scale(float scale)
	{
		if(Editor.editor.level != null)
		{
			if(scale >= 0.1 && scale <= 5) this.scale = scale;
			this.setSize((int)(Editor.editor.level.getWidth() * scale), (int)(Editor.editor.level.getHeight() * scale));
			ZoomListener.setZoom(Editor.editor.dropdownZoom, scale);
			this.updateUI();
		}
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setLevel(Level level)
	{
		img = new BufferedImage(level.getWidth(), level.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Editor.editor.level = level;
		
		DefaultTableModel tableModel = (DefaultTableModel) Editor.editor.tableLevel.getModel();
		tableModel.setRowCount(0);
		
		for(Field f : level.getClass().getDeclaredFields())
		{
			if(f.getAnnotation(Editable.class) != null)
			{
				try {
					Vector<Object> v = new Vector<Object>();
					
					v.add(f.getName());
					v.add(f.getGenericType() instanceof Class ? ((Class<?>)f.getGenericType()).getSimpleName() : f.getGenericType());
					v.add(f.get(level));
					
					tableModel.addRow(v);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}				
		
		Mouse.selectedEntities.clear();
		Mouse.selectedTiles.clear();
		Mouse.selection = null;
		
		Editor.editor.level.nodeMap = new NodeMap(Editor.editor.level);
		
		scale(1);
		update(false);
	}
}
