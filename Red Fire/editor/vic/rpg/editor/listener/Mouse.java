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

import vic.rpg.editor.Editor;
import vic.rpg.editor.gui.PopupMenu;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;
import vic.rpg.level.path.Node;
import vic.rpg.level.path.Path;
import vic.rpg.registry.GameRegistry;
import vic.rpg.registry.LevelRegistry;

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
		if(Editor.editor.buttonMove.isSelected()) Editor.editor.frame.setCursor(GameRegistry.CURSOR_DROP);
		else Editor.editor.frame.setCursor(Cursor.getDefaultCursor());
		
		Editor.editor.panelRender.requestFocus();
		mouseHovered = true;
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		Editor.editor.frame.setCursor(Cursor.getDefaultCursor());
		mouseHovered = false;
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		preX = arg0.getX();
		preY = arg0.getY();

		if(arg0.getButton() == MouseEvent.BUTTON1)
		{
			if(Editor.editor.buttonMove.isSelected()) Editor.editor.frame.setCursor(GameRegistry.CURSOR_DRAG);
			else Editor.editor.frame.setCursor(Cursor.getDefaultCursor());	
			
			mouseDown = true;
			
			if(Editor.editor.level == null) return;
			
			if(Editor.editor.buttonPaint.isSelected()) paint(arg0.getX(), arg0.getY());
			else if(Editor.editor.buttonPath.isSelected())
			{
				int x = (int) ((float)(arg0.getX() - Editor.editor.labelLevel.getX()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
				int y = (int) ((float)(arg0.getY() - Editor.editor.labelLevel.getY()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
				
				if(x >= Editor.editor.level.nodeMap.width || y >= Editor.editor.level.nodeMap.height || x < 0 || y < 0) return;
				
				start = Editor.editor.level.nodeMap.nodes[x][y];
				
				if(end != null)
				{
					path = Path.create(Editor.editor.level.nodeMap, start, end, Integer.MAX_VALUE);
				}
				
				Editor.editor.labelLevel.updateUI();
			}		
			else if(Editor.editor.buttonEdit.isSelected())
			{
				selection = null;
				
				if(Editor.editor.dropdownMode.getSelectedIndex() == 0)
				{
					int x = (int) ((float)(arg0.getX() - Editor.editor.labelLevel.getX()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
					int y = (int) ((float)(arg0.getY() - Editor.editor.labelLevel.getY()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
					
					if(x >= Editor.editor.level.width || y >= Editor.editor.level.height || x < 0 || y < 0) return;
					
					if(!arg0.isControlDown())
					{
						selectedTiles.clear();
					}
					selectedTiles.add(new Point(x, y));
					
					Editor.editor.tabpanelEditor.setSelectedComponent(Editor.editor.panelTiles);
					TableListener.setTile(LevelRegistry.tileRegistry.get(Editor.editor.level.worldobjects[x][y][0]), Editor.editor.level.worldobjects[x][y][1]);					
				}
				else
				{
					int x = (int) ((float)(arg0.getX() - Editor.editor.labelLevel.getX()) * (1 / Editor.editor.labelLevel.getScale()));
					int y = (int) ((float)(arg0.getY() - Editor.editor.labelLevel.getY()) * (1 / Editor.editor.labelLevel.getScale()));
					
					Entity e = Editor.editor.level.intersectOnRender(x, y);
					
					if(e != null) 
					{
						if(!arg0.isControlDown())
						{
							selectedEntities.clear();
						}
						selectedEntities.add(e);
						
						Editor.editor.tabpanelEditor.setSelectedComponent(Editor.editor.panelEntities);					
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
				
				Editor.editor.labelLevel.update(true);
				
			}
			else
			{
				if(selectedEntities.size() != 0 && !Editor.editor.buttonMove.isSelected())
				{
					selection = null;
					selectedEntities.clear();
					
					Editor.editor.labelLevel.update(true);
				}
			}
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3)
		{
			if(!Editor.editor.buttonPath.isSelected()) PopupMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			else
			{
				if(start != null)
				{
					int x = (int) ((float)(arg0.getX() - Editor.editor.labelLevel.getX()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
					int y = (int) ((float)(arg0.getY() - Editor.editor.labelLevel.getY()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
					
					if(x >= Editor.editor.level.nodeMap.width || y >= Editor.editor.level.nodeMap.height || x < 0 || y < 0) return;
					
					end = Editor.editor.level.nodeMap.nodes[x][y];			
					path = Path.create(Editor.editor.level.nodeMap, start, end, Integer.MAX_VALUE);
					
					Editor.editor.labelLevel.updateUI();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		if(Editor.editor.buttonMove.isSelected())
		{
			Editor.editor.frame.setCursor(GameRegistry.CURSOR_DROP);		
		}
		
		else Editor.editor.frame.setCursor(Cursor.getDefaultCursor());
		
		mouseDown = false;
		
		if(Editor.editor.buttonEdit.isSelected())
		{
			if(Editor.editor.dropdownMode.getSelectedIndex() == 0)
			{
				if(selection != null)
				{
					if(!arg0.isControlDown()) selectedTiles.clear();
						
					for(int i = (int)selection.getMinX(); i < selection.getMaxX(); i += Level.CELL_SIZE)
					{
						for(int j = (int)selection.getMinY(); j < selection.getMaxY(); j += Level.CELL_SIZE)
						{
							int x = i / Level.CELL_SIZE;
							int y = j / Level.CELL_SIZE;
							
							selectedTiles.add(new Point(x, y));
						}
					}
				}
			}
			else
			{
				if(selection != null)
				{
					if(arg0.isControlDown())
					{
						selectedEntities.addAll(Editor.editor.level.intersectOnRender(selection));
					}
					else
					{
						selectedEntities = Editor.editor.level.intersectOnRender(selection);				
					}				
				}
			}
			Mouse.selection = null;
			Editor.editor.labelLevel.update(true);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) 
	{
		Editor.editor.labelLevel.scale(Editor.editor.labelLevel.getScale() - arg0.getUnitsToScroll() / 100.0F);
	}
	
	int preX = 0;
	int preY = 0;
	
	@Override
	public void mouseDragged(MouseEvent arg0) 
	{ 
		xCoord = arg0.getX(); 
		yCoord = arg0.getY(); 
		
		if(Editor.editor.level == null) return;
		
		if(Editor.editor.buttonMove.isSelected())
		{
			int x = Editor.editor.labelLevel.getX() + (arg0.getX() - preX);
			int y = Editor.editor.labelLevel.getY() + (arg0.getY() - preY);
			
			Editor.editor.labelLevel.setLocation(x, y);
			
			preX = arg0.getX();
			preY = arg0.getY();
		}
		else if(Editor.editor.buttonPaint.isSelected() && Editor.editor.dropdownMode.getSelectedIndex() == 0)
		{
			paint(arg0.getX(), arg0.getY());
		}
		
		if(Editor.editor.buttonEdit.isSelected()) 
		{
			int x = (int) ((float)(preX - Editor.editor.labelLevel.getX()) * (1 / Editor.editor.labelLevel.getScale())); 
			int y = (int) ((float)(preY - Editor.editor.labelLevel.getY()) * (1 / Editor.editor.labelLevel.getScale()));		
			
			int x2 = (int) ((float)(arg0.getX() - Editor.editor.labelLevel.getX()) * (1 / Editor.editor.labelLevel.getScale()));
			int y2 = (int) ((float)(arg0.getY() - Editor.editor.labelLevel.getY()) * (1 / Editor.editor.labelLevel.getScale()));
			
			if(x2 > x && y2 > y) selection = new Rectangle(x, y, x2 - x, y2 - y);
			if(x2 < x && y2 < y) selection = new Rectangle(x2, y2, x - x2, y - y2);
			if(x2 < x && y2 > y) selection = new Rectangle(x2, y, x - x2, y2 - y);
			if(x2 > x && y2 < y) selection = new Rectangle(x, y2, x2 - x, y - y2);
			 
			Editor.editor.labelLevel.update(true);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		xCoord = arg0.getX(); 
		yCoord = arg0.getY(); 
	}
	
	private synchronized void paint(int x, int y)
	{
		if(Editor.editor.dropdownMode.getSelectedIndex() == 0)
		{
			paint(x, y, Integer.parseInt(Editor.editor.dropdownTiles.getSelectedItem().toString().split(":")[0]), false);
		}
		else
		{
			paint(x, y, Integer.parseInt(Editor.editor.dropdownEntities.getSelectedItem().toString().split(":")[0]), true);
		}
	}
	
	public static synchronized void paint(int x, int y, int id, boolean isEntity)
	{
		if(!isEntity)
		{
			int x2 = (int) ((float)(x - Editor.editor.labelLevel.getX()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
			int y2 = (int) ((float)(y - Editor.editor.labelLevel.getY()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
			
			if(x2 < 0 || y2 < 0 || x2 >= Editor.editor.level.width || y2 >= Editor.editor.level.height) return;
			
			Editor.editor.level.setTile(id, x2, y2, TableListener.tiles.get(id));
			Editor.editor.labelLevel.update((x2 * Level.CELL_SIZE) - Level.CELL_SIZE * 5, (y2 * Level.CELL_SIZE) - Level.CELL_SIZE * 5, Level.CELL_SIZE * 5, Level.CELL_SIZE * 5);
		}
		else
		{
			int x2 = (int) ((float)(x - Editor.editor.labelLevel.getX()) * (1 / Editor.editor.labelLevel.getScale()));
			int y2 = (int) ((float)(y - Editor.editor.labelLevel.getY()) * (1 / Editor.editor.labelLevel.getScale()));
			
			if(x2 < 0 || y2 < 0 || x2 >= Editor.editor.level.getWidth() || y2 >= Editor.editor.level.getHeight()) return;
			
			Editor.editor.level.addEntity(TableListener.entities.get(id).clone(), x2, y2);			
			Editor.editor.labelLevel.update(x2 - Level.CELL_SIZE * 5, y2 - Level.CELL_SIZE * 5, Level.CELL_SIZE * 5, Level.CELL_SIZE * 5);
		}
	}
}
