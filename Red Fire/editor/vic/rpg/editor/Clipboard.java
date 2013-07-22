package vic.rpg.editor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import vic.rpg.editor.listener.Mouse;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;

public class Clipboard 
{
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	public static ArrayList<Point> tiles = new ArrayList<Point>();
	public static HashMap<Point, Integer[]> tilesID = new HashMap<Point, Integer[]>();
	
	public static void paste(int x, int y)
	{
		for(Entity e : entities)
		{
			Entity e2 = e.clone();
			
			int x2 = (int) ((float)(x - Editor.editor.labelLevel.getX()) * (1 / Editor.editor.labelLevel.getScale()));
			int y2 = (int) ((float)(y - Editor.editor.labelLevel.getY()) * (1 / Editor.editor.labelLevel.getScale()));
			
			e2.xCoord += x2;
			e2.yCoord += y2;
			
			Editor.editor.level.addEntity(e2, e2.xCoord, e2.yCoord);
			Mouse.selectedEntities.add(e2);
		}
		for(Point p : tiles)
		{
			Point p2 = (Point) p.clone();
			
			int x2 = (int) ((float)(x - Editor.editor.labelLevel.getX()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
			int y2 = (int) ((float)(y - Editor.editor.labelLevel.getY()) / Level.CELL_SIZE * (1 / Editor.editor.labelLevel.getScale()));
			
			p2.x += x2;
			p2.y += y2;
			
			Integer[] data = tilesID.get(p).clone();
			Editor.editor.level.setTile(data[0].intValue(), p2.x, p2.y, data[1].intValue());
		}
		
		Editor.editor.labelLevel.update(false);
	}
	
	public static void copy()
	{
		int minX = Editor.editor.level.getWidth();
		int minY = Editor.editor.level.getHeight();
		
		entities.clear();
		for(Entity e : Mouse.selectedEntities)
		{
			entities.add(e.clone());
		}		
		for(Entity e : entities)
		{
			if(e.xCoord < minX) minX = e.xCoord;
			if(e.yCoord < minY) minY = e.yCoord;
		}
		for(Entity e : entities)
		{
			e.xCoord -= minX;
			e.yCoord -= minY;
		}
		
		minX = Editor.editor.level.width;
		minY = Editor.editor.level.height;
		
		tiles.clear();
		tilesID.clear();
		
		for(Point p : Mouse.selectedTiles)
		{
			tiles.add((Point) p.clone());
		}
		for(Point p : tiles)
		{
			if(p.x < minX) minX = p.x;
			if(p.y < minY) minY = p.y;
		}
		for(Point p : tiles)
		{
			Integer[] values = new Integer[]{Editor.editor.level.worldobjects[p.x][p.y][0], Editor.editor.level.worldobjects[p.x][p.y][1]};
			
			p.x -= minX;
			p.y -= minY;
			
			tilesID.put(p, values);
		}
	}
	
	public static void delete()
	{
		for(Entity e : Mouse.selectedEntities)
		{
			Editor.editor.level.entities.remove(e.UUID);
		}
		Mouse.selectedEntities.clear();
		Editor.editor.labelLevel.update(false);
	}
}
