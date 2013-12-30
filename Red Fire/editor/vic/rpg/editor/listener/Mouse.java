package vic.rpg.editor.listener;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;

import vic.rpg.editor.Editor;
import vic.rpg.editor.gui.PopupMenu;
import vic.rpg.editor.tiles.TileMaterial;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;
import vic.rpg.level.Tile;
import vic.rpg.level.path.Node;
import vic.rpg.level.path.Path;
import vic.rpg.registry.GameRegistry;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener
{
	public static boolean mouseHovered = false;
	public static boolean mouseDown = false;
	
	public static ArrayList<Point> selectedTiles = new ArrayList<Point>();
	public static ArrayList<Entity> selectedEntities = new ArrayList<Entity>();
	
	public static Rectangle selection;
	
	public static int xCoord = 0;
	public static int yCoord = 0;
	
	public static Node start;
	public static Node end;
	public static Path path;
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		if(Editor.instance.buttonMove.isSelected()) Editor.instance.frame.setCursor(GameRegistry.CURSOR_DROP);
		else Editor.instance.frame.setCursor(Cursor.getDefaultCursor());
		
		Editor.instance.labelLevel.requestFocus();
		mouseHovered = true;
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		Editor.instance.frame.setCursor(Cursor.getDefaultCursor());
		mouseHovered = false;
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		preX = arg0.getX();
		preY = arg0.getY();

		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(Editor.instance.buttonMove.isSelected()) Editor.instance.frame.setCursor(GameRegistry.CURSOR_DRAG);
			else Editor.instance.frame.setCursor(Cursor.getDefaultCursor());	
			
			mouseDown = true;
			
			if(Editor.instance.level == null) return;
			
			if(Editor.instance.buttonPaint.isSelected()) paint(arg0.getX(), arg0.getY());
			else if(Editor.instance.buttonErase.isSelected() && Editor.instance.tabpanelEditor.getSelectedIndex() == 1 && Editor.layerID != 0) paint(arg0.getX(), arg0.getY(), null, false);
			else if(Editor.instance.buttonPath.isSelected())
			{
				int x = (int) ((float)(arg0.getX() - Editor.instance.labelLevel.xOffset) / Level.CELL_SIZE * (1 / Editor.instance.labelLevel.getScale()));
				int y = (int) ((float)(arg0.getY() - Editor.instance.labelLevel.yOffset) / Level.CELL_SIZE * (1 / Editor.instance.labelLevel.getScale()));
				
				if(x >= Editor.instance.level.nodeMap.width || y >= Editor.instance.level.nodeMap.height || x < 0 || y < 0) return;
				
				start = Editor.instance.level.nodeMap.nodes[x][y];
				
				if(end != null)
				{
					path = new Path(Editor.instance.level.nodeMap, start, end, Integer.MAX_VALUE);
					path.compute();
				}
				
				Editor.instance.labelLevel.updateUI();
			}		
			else if(Editor.instance.buttonEdit.isSelected())
			{
				selection = null;
				
				if(Editor.instance.tabpanelEditor.getSelectedIndex() == 1)
				{
					Point p = Utils.convIsoToCart(new Point(arg0.getX(), arg0.getY()));
					int x = (int) ((float)(p.x - Editor.instance.labelLevel.xOffset) / (Level.CELL_SIZE / 2) * (1 / Editor.instance.labelLevel.getScale()));
					int y = (int) ((float)(p.y - Editor.instance.labelLevel.yOffset) / (Level.CELL_SIZE / 2) * (1 / Editor.instance.labelLevel.getScale()));
					
					if(x >= Editor.instance.level.width || y >= Editor.instance.level.height || x < 0 || y < 0) return;
					
					if(!arg0.isControlDown())
					{
						selectedTiles.clear();
					}
					selectedTiles.add(new Point(x, y));
					
					Editor.instance.tabpanelEditor.setSelectedComponent(Editor.instance.panelTiles);
					Tile t = Editor.instance.level.getTileAt(x, y, Editor.layerID);
					if(t != null) TableListener.setTile(t, Editor.instance.level.getTileDataAt(x, y, Editor.layerID));					
				}
				else if(Editor.instance.tabpanelEditor.getSelectedIndex() == 2)
				{
					int x = (int) ((float)(arg0.getX() - Editor.instance.labelLevel.xOffset) * (1 / Editor.instance.labelLevel.getScale()));
					int y = (int) ((float)(arg0.getY() - Editor.instance.labelLevel.yOffset) * (1 / Editor.instance.labelLevel.getScale()));
					
					Entity e = Editor.instance.level.intersectOnRender(x, y);
					
					if(e != null) 
					{
						if(!arg0.isControlDown())
						{
							selectedEntities.clear();
						}
						selectedEntities.add(e);
						
						Editor.instance.tabpanelEditor.setSelectedComponent(Editor.instance.panelEntities);					
						Editor.instance.dropdownEntities.setSelectedItem(e.id + ": " + e.getClass().getSimpleName());
						TableListener.setEntity(e);
					}
					else
					{
						if(!arg0.isControlDown())
						{
							selectedEntities.clear();
						}
					}
				}			
				Editor.instance.labelLevel.updateUI();			
			}
			else
			{
				if(selectedEntities.size() != 0 && !Editor.instance.buttonMove.isSelected())
				{
					selection = null;
					selectedEntities.clear();
					
					Editor.instance.labelLevel.updateUI();
				}
			}
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3)
		{
			if(!Editor.instance.buttonPath.isSelected()) PopupMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			else
			{
				if(start != null)
				{
					Point p = Utils.convIsoToCart(new Point(arg0.getX() - Editor.instance.labelLevel.xOffset, arg0.getY() - Editor.instance.labelLevel.yOffset));
					int x = (int) ((float)p.x / Level.CELL_SIZE / 2 * (1 / Editor.instance.labelLevel.getScale()));
					int y = (int) ((float)p.y / Level.CELL_SIZE / 2 * (1 / Editor.instance.labelLevel.getScale()));
					
					if(x >= Editor.instance.level.nodeMap.width || y >= Editor.instance.level.nodeMap.height || x < 0 || y < 0) return;
					
					end = Editor.instance.level.nodeMap.nodes[x][y];			
					path = new Path(Editor.instance.level.nodeMap, start, end, Integer.MAX_VALUE);
					path.compute();
					
					Editor.instance.labelLevel.updateUI();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		if(Editor.instance.buttonMove.isSelected())
		{
			Editor.instance.frame.setCursor(GameRegistry.CURSOR_DROP);		
		}
		
		else Editor.instance.frame.setCursor(Cursor.getDefaultCursor());
		
		mouseDown = false;
		
		if(Editor.instance.buttonEdit.isSelected())
		{
			if(Editor.instance.tabpanelEditor.getSelectedIndex() == 1)
			{
				if(selection != null)
				{
					if(!arg0.isControlDown()) selectedTiles.clear();
						
					for(int i = (int)selection.getMinX(); i < selection.getMaxX(); i += Level.CELL_SIZE / 2)
					{
						for(int j = (int)selection.getMinY(); j < selection.getMaxY(); j += Level.CELL_SIZE / 2)
						{
							Point p = Utils.convIsoToCart(new Point(i, j));
							int x = p.x / (Level.CELL_SIZE / 2);
							int y = p.y / (Level.CELL_SIZE / 2);							
							if(!(x >= Editor.instance.level.width || y >= Editor.instance.level.height || x < 0 || y < 0)) selectedTiles.add(new Point(x, y));
						}
					}
				}
			}
			else if(Editor.instance.tabpanelEditor.getSelectedIndex() == 2)
			{
				if(selection != null)
				{
					if(arg0.isControlDown())
					{
						selectedEntities.addAll(Editor.instance.level.intersectOnRender(selection));
					}
					else
					{
						selectedEntities = Editor.instance.level.intersectOnRender(selection);				
					}				
				}
			}
			Mouse.selection = null;
			Editor.instance.labelLevel.updateUI();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) 
	{
		Editor.instance.labelLevel.scale(Editor.instance.labelLevel.getScale() - arg0.getUnitsToScroll() / 100.0F);
	}
	
	int preX = 0;
	int preY = 0;
	
	@Override
	public void mouseDragged(MouseEvent arg0) 
	{ 
		xCoord = arg0.getX(); 
		yCoord = arg0.getY(); 
		
		if(Editor.instance.level == null) return;
		
		if(Editor.instance.buttonMove.isSelected())
		{
			Point p = Utils.convIsoToCart(new Point(xCoord - preX, yCoord - preY));

			int x = Editor.instance.labelLevel.xOffset + p.x;
			int y = Editor.instance.labelLevel.yOffset + p.y;
			
			Editor.instance.labelLevel.xOffset = x;
			Editor.instance.labelLevel.yOffset = y;
			
			preX = xCoord;
			preY = yCoord;
		}
		else if(Editor.instance.tabpanelEditor.getSelectedIndex() == 1)
		{
			if(Editor.instance.buttonPaint.isSelected())
			{
				paint(arg0.getX(), arg0.getY());
			}
			else if(Editor.instance.buttonErase.isSelected() && Editor.layerID != 0)
			{
				paint(arg0.getX(), arg0.getY(), null, false);
			}
		}		
		if(Editor.instance.buttonEdit.isSelected()) 
		{
			Point p1 = Utils.convIsoToCart(new Point(preX, preY));
			p1.x -= Editor.instance.labelLevel.xOffset;
			p1.y -= Editor.instance.labelLevel.yOffset;
			p1 = Utils.convCartToIso(p1);
			
			int x = (int) ((float)p1.x * (1 / Editor.instance.labelLevel.getScale())); 
			int y = (int) ((float)p1.y * (1 / Editor.instance.labelLevel.getScale()));		
			
			Point p2 = Utils.convIsoToCart(new Point(arg0.getX(), arg0.getY()));
			p2.x -= Editor.instance.labelLevel.xOffset;
			p2.y -= Editor.instance.labelLevel.yOffset;
			p2 = Utils.convCartToIso(p2);
			
			int x2 = (int) ((float)p2.x * (1 / Editor.instance.labelLevel.getScale()));
			int y2 = (int) ((float)p2.y * (1 / Editor.instance.labelLevel.getScale()));
			
			if(x2 > x && y2 > y) selection = new Rectangle(x, y, x2 - x, y2 - y);
			if(x2 < x && y2 < y) selection = new Rectangle(x2, y2, x - x2, y - y2);
			if(x2 < x && y2 > y) selection = new Rectangle(x2, y, x - x2, y2 - y);
			if(x2 > x && y2 < y) selection = new Rectangle(x, y2, x2 - x, y - y2);
			 
			Editor.instance.labelLevel.updateUI();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		xCoord = arg0.getX(); 
		yCoord = arg0.getY(); 
	}
	
	private void paint(int x, int y)
	{
		if(Editor.instance.tabpanelEditor.getSelectedIndex() == 1)
		{
			paint(x, y, Integer.parseInt(Editor.instance.dropdownTiles.getSelectedItem().toString().split(":")[0]), false);
		}
		else if(Editor.instance.tabpanelEditor.getSelectedIndex() == 2)
		{
			paint(x, y, Integer.parseInt(Editor.instance.dropdownEntities.getSelectedItem().toString().split(":")[0]), true);
		}
	}
	
	public static void brush(int ox, int oy, TileMaterial inner, TileMaterial outer, int r)
	{
		if(inner == null || outer == null) return;
		
		boolean[][] circle = new boolean[r * 2 + 4][r * 2 + 4];
		
		for(int x = 0; x < r * 2 + 4; x++)
		{
			for(int y = 0; y < r * 2 + 4; y++)
			{
				hfunc(x, y, ox, oy, r, inner, outer, circle);
			}
		}
		
		for(int x = -r; x < r ; x++)
		{
		    int height = (int)Math.sqrt(r * r - x * x);

		    for (int y = -height; y < height; y++)
		    {
		    	circle[x + r + 2][y + r + 2] = true;
		    }
		}
	
		for(int x = 1; x < r * 2 + 3; x++)
		{
			for(int y = 1; y < r * 2 + 3; y++)
			{
				Point p = null;
				
				int x2 = x + ox - r - 1;
				int y2 = y + oy - r - 1;
				
				if(circle[x][y] && circle[x - 1][y - 1] && circle[x][y - 1] && circle[x + 1][y - 1] && circle[x + 1][y] && circle[x + 1][y + 1] && circle[x][y + 1] && circle[x - 1][y + 1] && circle[x - 1][y]) p = inner.getTextureCoord(outer, Direction.CENTER);
				
				else if(circle[x][y] && !circle[x - 1][y] && !circle[x - 1][y - 1] && !circle[x][y - 1]) p = inner.getTextureCoord(outer, Direction.NORTH_WEST);
				else if(circle[x][y] && !circle[x][y - 1] && !circle[x + 1][y - 1] && !circle[x + 1][y]) p = inner.getTextureCoord(outer, Direction.NORTH_EAST);
				else if(circle[x][y] && !circle[x + 1][y] && !circle[x + 1][y + 1] && !circle[x][y + 1]) p = inner.getTextureCoord(outer, Direction.SOUTH_EAST);
				else if(circle[x][y] && !circle[x][y + 1] && !circle[x - 1][y + 1] && !circle[x - 1][y]) p = inner.getTextureCoord(outer, Direction.SOUTH_WEST);
				
				else if(circle[x][y] && circle[x - 1][y] && circle[x + 1][y] && !circle[x][y - 1]) p = inner.getTextureCoord(outer, Direction.NORTH);
				else if(circle[x][y] && circle[x][y - 1] && circle[x][y + 1] && !circle[x + 1][y]) p = inner.getTextureCoord(outer, Direction.EAST);
				else if(circle[x][y] && circle[x - 1][y] && circle[x + 1][y] && !circle[x][y + 1]) p = inner.getTextureCoord(outer, Direction.SOUTH);
				else if(circle[x][y] && circle[x][y - 1] && circle[x][y + 1] && !circle[x - 1][y]) p = inner.getTextureCoord(outer, Direction.WEST);				
				
				else if(circle[x][y] && circle[x][y + 1] && circle[x - 1][y] && !circle[x - 1][y - 1]) p = outer.getTextureCoord(inner, Direction.SOUTH_EAST);
				else if(circle[x][y] && circle[x + 1][y] && circle[x][y - 1] && !circle[x + 1][y - 1]) p = outer.getTextureCoord(inner, Direction.SOUTH_WEST);
				else if(circle[x][y] && circle[x + 1][y] && circle[x][y + 1] && !circle[x + 1][y + 1]) p = outer.getTextureCoord(inner, Direction.NORTH_WEST);
				else if(circle[x][y] && circle[x][y + 1] && circle[x - 1][y] && !circle[x - 1][y + 1]) p = outer.getTextureCoord(inner, Direction.NORTH_EAST);
			
				else continue;
				
				Editor.instance.level.setTile(LevelRegistry.TILE_TERRAIN.id, x2, y2, Utils.conv2Dto1Dint(p.x, p.y, 16D), Editor.layerID);
			}
		}
	}
	
	private static void hfunc(int x, int y, int ox, int oy, int r, TileMaterial inner, TileMaterial outer, boolean[][] circle)
	{
		int x2 = x + ox - r - 1;
		int y2 = y + oy - r - 1;
		
		if(x2 >= 0 && y2 >= 0 && x2 < Editor.instance.level.height && y2 < Editor.instance.level.width) 
		{
			Point op = Utils.conv1Dto2DPoint(Editor.instance.level.getTileDataAt(x2, y2, Editor.layerID), 10D);
			
			if(Arrays.asList(inner.getSubMaterials().get(outer.getName())).contains(op) || Arrays.asList(outer.getSubMaterials().get(inner.getName())).contains(op))
			{
				if(!outer.getTextureCoord(inner, Direction.CENTER).equals(op))
				{
					circle[x][y] = true;
				}
			}
		}
		else
		{
			circle[x][y] = true;
		}
	}
	
	public static void paint(int x, int y, Integer id, boolean isEntity)
	{
		if(!isEntity)
		{
			//TODO This is just a tiny bit off
			Point p = Utils.convIsoToCart(new Point(x, y));
			int x2 = (int)((float)(p.x - Editor.instance.labelLevel.xOffset) / (Level.CELL_SIZE / 2) * (1 / Editor.instance.labelLevel.getScale()));
			int y2 = (int)((float)(p.y - Editor.instance.labelLevel.yOffset) / (Level.CELL_SIZE / 2) * (1 / Editor.instance.labelLevel.getScale()));
			
			if(id != null && id == LevelRegistry.TILE_TERRAIN.id)
			{
				brush(x2, y2, BrushFrameListener.instance.inner, BrushFrameListener.instance.outer, Editor.instance.sliderBrushSize.getValue());
			}
			else
			{
				if(id != null) Editor.instance.level.setTile(id, x2, y2, TableListener.tiles.get(id), Editor.layerID);
				else Editor.instance.level.setTile(id, x2, y2, 0, Editor.layerID);
				Editor.instance.labelLevel.updateUI();
			}
		}
		else
		{
			Point p = Utils.convIsoToCart(new Point(x, y));
			int x2 = (int) ((float)(p.x - Editor.instance.labelLevel.xOffset) * (1 / Editor.instance.labelLevel.getScale()));
			int y2 = (int) ((float)(p.y - Editor.instance.labelLevel.yOffset) * (1 / Editor.instance.labelLevel.getScale()));
						
			if(x2 < 0 || y2 < 0 || x2 >= Editor.instance.level.getWidth() || y2 >= Editor.instance.level.getHeight()) return;
			
			Editor.instance.level.addEntity(TableListener.entities.get(id).clone(), x2, y2);			
			Editor.instance.labelLevel.updateUI();
		}
	}
}
