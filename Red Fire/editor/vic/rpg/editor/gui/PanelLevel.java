package vic.rpg.editor.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.table.DefaultTableModel;

import vic.rpg.editor.Editor;
import vic.rpg.editor.listener.Key;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.editor.listener.ZoomListener;
import vic.rpg.level.Editable;
import vic.rpg.level.Level;
import vic.rpg.level.entity.Entity;
import vic.rpg.level.path.Node;
import vic.rpg.level.path.NodeMap;
import vic.rpg.level.path.Path;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

public class PanelLevel extends GLJPanel
{
	private float scale = 1;
	public int xOffset = 0;
	public int yOffset = 0;
	
	public PanelLevel(GLCapabilities glCapabilities)
	{
		super(glCapabilities);
		
		this.addGLEventListener(new GLEventListener() 
		{		
			@Override public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
			
			@Override
			public void init(GLAutoDrawable drawable) 
			{
				GL2 gl2 = drawable.getGL().getGL2();
				gl2.glEnable(GL2.GL_ALPHA_TEST);
		    	gl2.glAlphaFunc(GL2.GL_GREATER, 0.1F);
		    	gl2.glEnable(GL2.GL_BLEND);
		    	gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		    	gl2.glDisable(GL2.GL_DEPTH_TEST);
			}
			
			@Override public void dispose(GLAutoDrawable drawable) {}
			
			@Override
			public void display(GLAutoDrawable drawable) 
			{
				GL2 gl2 = drawable.getGL().getGL2();
				gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
				
		    	DrawUtils.setGL(gl2);
		    	TextureLoader.setupTextures(gl2);
				
				if(Editor.instance.level != null)
				{										
					gl2.glPushMatrix();
					
					gl2.glMatrixMode(GL2.GL_PROJECTION);
			    	gl2.glLoadIdentity();
			    	gl2.glViewport(0, 0, Editor.instance.labelLevel.getWidth(), Editor.instance.labelLevel.getHeight());
			    	gl2.glOrtho(0, Editor.instance.labelLevel.getWidth(), Editor.instance.labelLevel.getHeight(), 0, -1, 1);
			    	gl2.glMatrixMode(GL2.GL_MODELVIEW);
			    	
			    	gl2.glScalef(scale, scale, scale);
			    	
			    	Editor.instance.level.render(gl2, (int)(-xOffset / scale), (int)(-yOffset / scale), (int)(Editor.instance.labelLevel.getWidth() / scale), (int)(Editor.instance.labelLevel.getHeight() / scale));
					gl2.glPopMatrix();
				}
				gl2.glFlush();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{		
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform oldTransform = g2d.getTransform();
		AffineTransform transform = new AffineTransform();
		transform.scale(scale, scale);
		Point p1 = Utils.convCartToIso(new Point(xOffset, yOffset));
		transform.translate(p1.x / scale, p1.y / scale);
		g2d.transform(transform);
		
		if(Editor.instance == null) return;
				
		if(Mouse.selectedEntities != null)
		{
			for(Entity e : Mouse.selectedEntities)
			{
				g2d.setColor(Color.yellow);
				Stroke stroke = g2d.getStroke();
				g2d.setStroke(new BasicStroke(5));
				Point p = Utils.convCartToIso(new Point(e.xCoord, e.yCoord));
				Dimension off = e.getRenderOffset();
				p.x -= off.width;
				p.y -= off.height;
				g2d.drawRect(p.x, p.y, e.getWidth(), e.getHeight());
				g2d.setStroke(stroke);
			}
		}
		if(Mouse.selectedTiles != null)
		{
			for(Point p2 : Mouse.selectedTiles)
			{
				g2d.setColor(Color.yellow);
				Stroke stroke = g2d.getStroke();
				g2d.setStroke(new BasicStroke(5));
				Point p3 = Utils.convCartToIso(new Point(p2.x * (Level.CELL_SIZE / 2), p2.y * (Level.CELL_SIZE / 2)));
				g2d.drawLine(p3.x - Level.CELL_SIZE / 2, p3.y + Level.CELL_SIZE / 4, p3.x, p3.y);
				g2d.drawLine(p3.x - Level.CELL_SIZE / 2, p3.y + Level.CELL_SIZE / 4, p3.x, p3.y + Level.CELL_SIZE / 2);
				g2d.drawLine(p3.x, p3.y, p3.x + Level.CELL_SIZE / 2, p3.y + Level.CELL_SIZE / 4);
				g2d.drawLine(p3.x, p3.y + Level.CELL_SIZE / 2, p3.x + Level.CELL_SIZE / 2, p3.y + Level.CELL_SIZE / 4);
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
		if(Editor.instance.buttonPath.isSelected() || Key.keyListener.button == 3)
		{
			if(Editor.instance.level != null)
			{
				g2d.setColor(new Color(255, 0, 0, 120));
				for(Node[] n2 : Editor.instance.level.nodeMap.nodes)
				{
					for(Node n : n2)
					{
						if(Path.isNodeBlocked(n, Editor.instance.level.nodeMap))
						{
							Point p = Utils.convCartToIso(new Point(n.x * (Level.CELL_SIZE / 2), n.y * (Level.CELL_SIZE / 2)));
							Polygon poly = new Polygon(new int[]{p.x - Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2}, new int[]{p.y + Level.CELL_SIZE / 4, p.y, p.y + Level.CELL_SIZE / 4, p.y + Level.CELL_SIZE / 2, p.y + Level.CELL_SIZE / 4}, 4);
							g2d.fill(poly);
						}
					}
				}
				
				g2d.setColor(new Color(255, 255, 0));
				for(Entity e : Editor.instance.level.entityMap.values())
				{
					g2d.draw(e.getCollisionBoxes(new Area()));
				}
				
				g2d.setColor(new Color(0, 255, 0, 120));
				if(Mouse.start != null) 
				{
					Point p = Utils.convCartToIso(new Point(Mouse.start.x * (Level.CELL_SIZE / 2), Mouse.start.y * (Level.CELL_SIZE / 2)));
					Polygon poly = new Polygon(new int[]{p.x - Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2}, new int[]{p.y + Level.CELL_SIZE / 4, p.y, p.y + Level.CELL_SIZE / 4, p.y + Level.CELL_SIZE / 2, p.y + Level.CELL_SIZE / 4}, 4);
					g2d.fill(poly);
				}
				
				g2d.setColor(new Color(0, 255, 255, 120));
				if(Mouse.end != null)
				{
					Point p = Utils.convCartToIso(new Point(Mouse.end.x * (Level.CELL_SIZE / 2), Mouse.end.y * (Level.CELL_SIZE / 2)));
					Polygon poly = new Polygon(new int[]{p.x - Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2}, new int[]{p.y + Level.CELL_SIZE / 4, p.y, p.y + Level.CELL_SIZE / 4, p.y + Level.CELL_SIZE / 2, p.y + Level.CELL_SIZE / 4}, 4);
					g2d.fill(poly);
				}
				
				g2d.setColor(new Color(0, 0, 255, 120));		
				if(Mouse.path != null)
				{
					while(Mouse.path.hasNext())
					{
						Node n = Mouse.path.next();
						Point p = Utils.convCartToIso(new Point(n.x * (Level.CELL_SIZE / 2), n.y * (Level.CELL_SIZE / 2)));
						Polygon poly = new Polygon(new int[]{p.x - Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2, p.x, p.x + Level.CELL_SIZE / 2}, new int[]{p.y + Level.CELL_SIZE / 4, p.y, p.y + Level.CELL_SIZE / 4, p.y + Level.CELL_SIZE / 2, p.y + Level.CELL_SIZE / 4}, 4);
						g2d.fill(poly);
					}
					Mouse.path.revert();
				}
			}
		}
		g2d.setTransform(oldTransform);
	}
	
	public void scale(float scale)
	{
		if(Editor.instance.level != null)
		{
			if(scale < 0.1F) scale = 0.1F;
			if(scale > 5F) scale = 5F;
			this.scale = scale;
			ZoomListener.setZoom(Editor.instance.dropdownZoom, this.scale);
			this.updateUI();
		}
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setLevel(Level level)
	{	
		Editor.instance.level = level;
		
		DefaultTableModel tableModel = (DefaultTableModel) Editor.instance.tableLevel.getModel();
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
		
		Editor.instance.level.nodeMap = new NodeMap(Editor.instance.level);
		
		scale(1);
		this.updateUI();
	}
}
