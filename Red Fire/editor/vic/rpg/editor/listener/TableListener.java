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
		if(e.getSource() == Editor.instance.tableLevel.getModel() && e.getType() == TableModelEvent.UPDATE)
		{
			if(Editor.instance.level == null) return;
			
			int row = e.getFirstRow();
			
	        TableModel model = (TableModel)e.getSource();
	        String name = (String) model.getValueAt(row, 0);
	        String type = (String) model.getValueAt(row, 1);
	        Object value = model.getValueAt(row, 2);	        	          
	        
	        try
	        {
	        	Utils.setField(name, value, Editor.instance.level);
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	JOptionPane.showMessageDialog(null, "Data with name \"" + name + "\" could'nt be changed to \"" + value +"\"\nIt's from type \"" + type + "\"", "Error", JOptionPane.ERROR_MESSAGE);	            
	        }
		}		
		if(e.getSource() == Editor.instance.tableEntities.getModel() && e.getType() == TableModelEvent.UPDATE)
		{
			if(Editor.instance.level == null) return;
			
			int row = e.getFirstRow();
			
	        TableModel model = (TableModel)e.getSource();
	        String name = (String) model.getValueAt(row, 0);
	        String type = (String) model.getValueAt(row, 1);
	        Object value = model.getValueAt(row, 2);	        	          
	        
	        try
	        {
	        	if(Mouse.selectedEntities.size() == 1) 
        		{
	        		Utils.setField(name, value, Mouse.selectedEntities.get(0));
	        		Editor.instance.labelLevel.updateUI();
        		}
	        	else Utils.setField(name, value, entities.get(Integer.parseInt(Editor.instance.dropdownEntities.getSelectedItem().toString().split(":")[0])));
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	JOptionPane.showMessageDialog(null, "Data with name \"" + name + "\" could'nt be changed to \"" + value +"\"\nIt's from type \"" + type + "\"", "Error", JOptionPane.ERROR_MESSAGE);	            
	        }
	        
		}
		if(e.getSource() == Editor.instance.tableTiles.getModel() && e.getType() == TableModelEvent.UPDATE)
		{
			if(Mouse.selectedTiles.size() > 0) 
			{
				for(Point p : Mouse.selectedTiles)
				{
					Tile t = Editor.instance.level.getTileAt(p.x, p.y, Editor.layerID);
					if(t != null)
					{
						int data = Integer.parseInt(((TableModel)e.getSource()).getValueAt(e.getFirstRow(), 2).toString());
						Editor.instance.level.setTile(t.id, p.x, p.y, data, Editor.layerID);
					}
				}				
				
				Editor.instance.labelLevel.updateUI();
			}
			else tiles.put(Integer.parseInt(Editor.instance.dropdownTiles.getSelectedItem().toString().split(":")[0]), Integer.parseInt(((TableModel)e.getSource()).getValueAt(e.getFirstRow(), 2).toString()));
		}
	}
	
	public static void setTile(Tile t, int data)
	{
		Editor.instance.dropdownTiles.setSelectedItem(t.id + ": " + t.getClass().getSimpleName());
		Editor.instance.labelTiles.setText("<html>" + t.getDescription() + "</html>");
		
		DefaultTableModel tableModel = (DefaultTableModel) Editor.instance.tableTiles.getModel();
		tableModel.setRowCount(0);
		
		Vector<Object> v = new Vector<Object>();
		
		v.add("data");
		v.add("int");
		v.add(data);
		
		tableModel.addRow(v);
	}
	
	public static void setEntity(Entity e)
	{
		DefaultTableModel tableModel = (DefaultTableModel) Editor.instance.tableEntities.getModel();
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
