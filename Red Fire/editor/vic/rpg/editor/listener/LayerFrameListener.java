package vic.rpg.editor.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import vic.rpg.editor.Editor;

public class LayerFrameListener implements ActionListener, TableModelListener, ListSelectionListener
{
	public static LayerFrameListener instance = new LayerFrameListener();
	
	public static void updateLayers()
	{
		JTable table = Editor.instance.tableLayers;	
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		
		tableModel.setRowCount(0);
		for(int i = 0; i < Editor.instance.level.layers.size(); i++)
		{
			Vector<Object> row = new Vector<Object>();
			row.add(i);
			row.add(Editor.instance.level.isLayerVisible(i));
			tableModel.addRow(row);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource() == Editor.instance.buttonNewLayer)
		{
			Editor.instance.level.addLayer();
			Editor.updateLayerFrame();
		}
		if(arg0.getSource() == Editor.instance.buttonRemoveLayer)
		{
			Integer levelID = (Integer) Editor.instance.tableLayers.getModel().getValueAt(Editor.instance.tableLayers.getSelectedRow(), 0);
			if(Editor.instance.level.getLayer() == levelID)
			{
				Editor.instance.tableLayers.setRowSelectionInterval(Editor.instance.level.layers.size() - 1, Editor.instance.level.layers.size() - 1);
			}
			Editor.instance.level.removeLayer(levelID);
			Editor.updateLayerFrame();
		}
	}

	@Override
	public void tableChanged(TableModelEvent arg0) 
	{
		if(arg0.getType() == TableModelEvent.UPDATE)
		{
			TableModel table = (TableModel)arg0.getSource();
			int id = (Integer) table.getValueAt(arg0.getFirstRow(), 0);
			boolean selected = (Boolean) table.getValueAt(arg0.getFirstRow(), 1);	
			Editor.instance.level.setLayerVisibility(id, selected);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) 
	{
		if(arg0.getValueIsAdjusting()) return;
		
		if(Editor.instance.tableLayers.getSelectedRow() != -1)
		{
			Editor.instance.level.setLayer((Integer) Editor.instance.tableLayers.getModel().getValueAt(Editor.instance.tableLayers.getSelectedRow(), 0));
		}
	}
}
