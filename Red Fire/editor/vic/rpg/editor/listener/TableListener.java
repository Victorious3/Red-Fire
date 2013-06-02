package vic.rpg.editor.listener;

import java.awt.Point;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import vic.rpg.editor.Editor;
import vic.rpg.level.Editable;
import vic.rpg.level.Entity;
import vic.rpg.level.Tile;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.utils.Utils;

public class TableListener implements TableModelListener
{
	public static HashMap<Integer, Entity> entities = new HashMap<Integer, Entity>();
	public static HashMap<Integer, Integer> tiles = new HashMap<Integer, Integer>();
	
	static
	{
		for(Integer i : LevelRegistry.entityRegistry.keySet())
		{
			entities.put(i, LevelRegistry.entityRegistry.get(i).clone());
		}
		for(Integer i : LevelRegistry.tileRegistry.keySet())
		{
			tiles.put(i, LevelRegistry.tileRegistry.get(i).data);
		}
	}
	
	@Override
	public void tableChanged(TableModelEvent e) 
	{
		if(e.getSource() == Editor.editor.tableLevel.getModel() && e.getType() == TableModelEvent.UPDATE)
		{
			if(Editor.editor.level == null) return;
			
			int row = e.getFirstRow();
			
	        TableModel model = (TableModel)e.getSource();
	        String name = (String) model.getValueAt(row, 0);
	        String type = (String) model.getValueAt(row, 1);
	        Object value = model.getValueAt(row, 2);	        	          
	        
	        try
	        {
	        	Utils.setFiled(name, value, type, Editor.editor.level);
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	JOptionPane.showMessageDialog(null, "Data with name \"" + name + "\" could'nt be changed to \"" + value +"\"\nIt's from type \"" + type + "\"", "Error", JOptionPane.ERROR_MESSAGE);	            
	        }
		}		
		if(e.getSource() == Editor.editor.tableEntities.getModel() && e.getType() == TableModelEvent.UPDATE)
		{
			if(Editor.editor.level == null) return;
			
			int row = e.getFirstRow();
			
	        TableModel model = (TableModel)e.getSource();
	        String name = (String) model.getValueAt(row, 0);
	        String type = (String) model.getValueAt(row, 1);
	        Object value = model.getValueAt(row, 2);	        	          
	        
	        try
	        {
	        	if(Mouse.selectedEntities.size() == 1) 
        		{
	        		Utils.setFiled(name, value, type, Mouse.selectedEntities.get(0));
	        		Editor.editor.labelLevel.update(false);
        		}
	        	else Utils.setFiled(name, value, type, entities.get(Integer.parseInt(Editor.editor.dropdownEntities.getSelectedItem().toString().split(":")[0])));
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	JOptionPane.showMessageDialog(null, "Data with name \"" + name + "\" could'nt be changed to \"" + value +"\"\nIt's from type \"" + type + "\"", "Error", JOptionPane.ERROR_MESSAGE);	            
	        }
	        
		}
		if(e.getSource() == Editor.editor.tableTiles.getModel() && e.getType() == TableModelEvent.UPDATE)
		{
			if(Mouse.selectedTiles.size() > 0) 
			{
				for(Point p: Mouse.selectedTiles)
				{
					Editor.editor.level.worldobjects[p.x][p.y][1] = Integer.parseInt(((TableModel)e.getSource()).getValueAt(e.getFirstRow(), 2).toString()); 
				}				
				
				Editor.editor.labelLevel.update(false);
			}
			else tiles.put(Integer.parseInt(Editor.editor.dropdownTiles.getSelectedItem().toString().split(":")[0]), Integer.parseInt(((TableModel)e.getSource()).getValueAt(e.getFirstRow(), 2).toString()));
		}
	}
	
	public static void setTile(Tile t, int data)
	{
		Editor.editor.dropdownTiles.setSelectedItem(t.id + ": " + t.getClass().getSimpleName());
		Editor.editor.labelTiles.setText("<html>" + t.getDescription() + "</html>");
		
		DefaultTableModel tableModel = (DefaultTableModel) Editor.editor.tableTiles.getModel();
		tableModel.setRowCount(0);
		
		Vector<Object> v = new Vector<Object>();
		
		v.add("data");
		v.add("int");
		v.add(data);
		
		tableModel.addRow(v);
	}
	
	public static void setEntity(Entity e)
	{
		Editor.editor.dropdownEntities.setSelectedItem(e.id + ": " + e.getClass().getSimpleName());
		
		DefaultTableModel tableModel = (DefaultTableModel) Editor.editor.tableEntities.getModel();
		tableModel.setRowCount(0);
		
		for(Field f : e.getClass().getFields())
		{
			if(f.getAnnotation(Editable.class) != null)
			{
				try {
					Vector<Object> v = new Vector<Object>();
					
					v.add(f.getName());
					v.add(f.getGenericType() instanceof Class ? ((Class<?>)f.getGenericType()).getSimpleName() : f.getGenericType());
					v.add(f.get(e));
					
					tableModel.addRow(v);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}
