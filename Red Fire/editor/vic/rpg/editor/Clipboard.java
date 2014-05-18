package vic.rpg.editor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import vic.rpg.editor.listener.Mouse;
import vic.rpg.world.Map;
import vic.rpg.world.entity.Entity;

public class Clipboard 
{
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	public static ArrayList<Point> tiles = new ArrayList<Point>();
	public static HashMap<Point, Integer[]> tilesID = new HashMap<Point, Integer[]>();
	
	/**
	 * Pastes the clipboard contents. Entities and Tiles
	 * @param x
	 * @param y
	 */
	public static void paste(int x, int y)
	{
		for(Entity e : entities)
		{
			Entity e2 = e.clone();
			
			int x2 = (int) ((float)(x - Editor.instance.labelMap.xOffset) * (1 / Editor.instance.labelMap.getScale()));
			int y2 = (int) ((float)(y - Editor.instance.labelMap.yOffset) * (1 / Editor.instance.labelMap.getScale()));
			
			e2.xCoord += x2;
			e2.yCoord += y2;
			
			Editor.instance.map.addEntity(e2, e2.xCoord, e2.yCoord);
			Mouse.selectedEntities.add(e2);
		}
		for(Point p : tiles)
		{
			Point p2 = (Point) p.clone();
			
			int x2 = (int) ((float)(x - Editor.instance.labelMap.xOffset) / Map.CELL_SIZE * (1 / Editor.instance.labelMap.getScale()));
			int y2 = (int) ((float)(y - Editor.instance.labelMap.yOffset) / Map.CELL_SIZE * (1 / Editor.instance.labelMap.getScale()));
			
			p2.x += x2;
			p2.y += y2;
			
			Integer[] data = tilesID.get(p).clone();
			Editor.instance.map.setTile(data[0].intValue(), p2.x, p2.y, data[1].intValue(), Editor.layerID);
		}
		
		Editor.instance.labelMap.updateUI();
	}
	
	/**
	 * Copies the currently selected Tiles and Entities.
	 */
	public static void copy()
	{		
		entities.clear();
		for(Entity e : Mouse.selectedEntities)
		{
			entities.add(e.clone());
		}		

		tiles.clear();
		tilesID.clear();
		
		for(Point p : Mouse.selectedTiles)
		{
			tiles.add((Point) p.clone());
		}
		for(Point p : tiles)
		{
			Integer[] values = new Integer[]{Editor.instance.map.getTileAt(p.x, p.y, Editor.layerID).id, Editor.instance.map.getTileDataAt(p.x, p.y, Editor.layerID)};
			tilesID.put(p, values);
		}
	}
	
	/**
	 * Deletes the currently selected Tiles and Entities.
	 */
	public static void delete()
	{
		for(Entity e : Mouse.selectedEntities)
		{
			Editor.instance.map.entityMap.remove(e.UUID);
		}
		Mouse.selectedEntities.clear();
		Editor.instance.labelMap.updateUI();
	}
}
