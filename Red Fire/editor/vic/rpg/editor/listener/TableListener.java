package vic.rpg.editor.listener;

import java.lang.reflect.Field;
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
import vic.rpg.utils.Utils;

public class TableListener implements TableModelListener
{
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
		if(e.getSource() == Editor.editor.tableEntities.getModel() && e.getType() == TableModelEvent.UPDATE && Mouse.selectedEntities.size() == 1)
		{
			if(Editor.editor.level == null) return;
			
			int row = e.getFirstRow();
			
	        TableModel model = (TableModel)e.getSource();
	        String name = (String) model.getValueAt(row, 0);
	        String type = (String) model.getValueAt(row, 1);
	        Object value = model.getValueAt(row, 2);	        	          
	        
	        try
	        {
	        	Utils.setFiled(name, value, type, Mouse.selectedEntities.get(0));
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	JOptionPane.showMessageDialog(null, "Data with name \"" + name + "\" could'nt be changed to \"" + value +"\"\nIt's from type \"" + type + "\"", "Error", JOptionPane.ERROR_MESSAGE);	            
	        }
	        Editor.editor.labelLevel.update(false);
		}		
	}
	
	public static void setTile(Tile t)
	{
		Editor.editor.dropdownTiles.setSelectedItem(t.id + ": " + t.getClass().getSimpleName());
		
		DefaultTableModel tableModel = (DefaultTableModel) Editor.editor.tableTiles.getModel();
		tableModel.setRowCount(0);
		
		Vector<Object> v = new Vector<Object>();
		
		v.add("data");
		v.add("int");
		v.add(0);
		
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
