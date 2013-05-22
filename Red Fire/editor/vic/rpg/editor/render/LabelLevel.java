package vic.rpg.editor.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import vic.rpg.editor.Editor;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.editor.listener.ZoomListener;
import vic.rpg.level.Editable;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;

public class LabelLevel extends JLabel 
{
	private int needsUpdate = 0;
	private float scale = 1;
	
	private int offX = 0;
	private int offY = 0;
	private int width = 0;
	private int height = 0;
	
	@Override
	public void paintComponent(Graphics g) 
	{		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.scale(scale, scale);
		
		if(Editor.editor == null) return;
		
		if(needsUpdate == 1 && Editor.editor.level != null)
		{
			Editor.editor.level.render(g2d, 0, 0, Editor.editor.level.getWidth(), Editor.editor.level.getHeight());
		}
		if(needsUpdate == 2 && Editor.editor.level != null)
		{
			Editor.editor.level.render(g2d, offX, offY, width, height);
		}
		
		if(Editor.editor.level != null) g2d.drawImage(Editor.editor.level.img, 0, 0, null);
		
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
		
		scale(1);
		update(false);
	}
}
