package vic.rpg.editor.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import vic.rpg.editor.Clipboard;
import vic.rpg.editor.Editor;
import vic.rpg.utils.Utils;

public class ButtonListener implements ActionListener 
{
	public static ButtonListener listener = new ButtonListener();
	public static File file;		
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource() == Editor.instance.open)
		{
			Editor.openLevel();
		}
		else if(arg0.getSource() == Editor.instance.newLevel)
		{
			Editor.createNewLevel();
		}
		else if(arg0.getSource() == Editor.instance.save)
		{
			if(file == null)
			{
				JOptionPane.showMessageDialog(null, "No file selected", "Save...", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				Editor.instance.level.writeToFile(file);
            	JOptionPane.showMessageDialog(null, "Level \"" + Editor.instance.level.name + "\" was saved", "Save", JOptionPane.INFORMATION_MESSAGE);  	
			}
		}
		else if(arg0.getSource() == Editor.instance.saveas)
		{
			Editor.saveLevel();
		}
		else if(arg0.getSource() == Editor.instance.exit)
		{
			
		}
		else if(arg0.getSource() == Editor.instance.undo)
		{
			
		}
		else if(arg0.getSource() == Editor.instance.redo)
		{
			
		}
		else if(arg0.getSource() instanceof JMenuItem)
		{
			if(((JPopupMenu)((JMenuItem)arg0.getSource()).getParent()).getInvoker() == Editor.instance.run)
			{
				String s = ((JMenuItem)arg0.getSource()).getText();
				String s1 = Utils.getAppdata() + "/scripts/" + s + ".jar";		
				
				try {			
					Editor.runScript(s, s1);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, s1 + " couldn't be run!\nThe File is not valid", "Run...", JOptionPane.ERROR_MESSAGE);	            
				}
			}
		}
		else if(arg0.getSource() == Editor.instance.copy)
		{
			Clipboard.copy();
		}
		else if(arg0.getSource() == Editor.instance.paste)
		{
			Clipboard.paste(0, 0);
		}
		else if(arg0.getSource() == Editor.instance.delete)
		{
			Clipboard.delete();
		}
		else if(arg0.getSource() == Editor.instance.buttonZoomIn)
		{
			ZoomListener.setZoom(Editor.instance.dropdownZoom, ZoomListener.getZoom(Editor.instance.dropdownZoom, 0) + 0.1F);
		}
		else if(arg0.getSource() == Editor.instance.buttonZoomOut)
		{
			ZoomListener.setZoom(Editor.instance.dropdownZoom, ZoomListener.getZoom(Editor.instance.dropdownZoom, 0) - 0.1F);
		}
		else if(arg0.getSource() == Editor.instance.buttonRefresh)
		{
			Editor.instance.labelLevel.update(false);
			Editor.instance.level.nodeMap.recreate(Editor.instance.level);
			Editor.instance.frame.setTitle("Red Fire Level Editor (" + Editor.instance.level.name + ")");			
		}
		else if(arg0.getSource() == Editor.instance.buttonEdit)
		{
			Editor.instance.buttonMove.setSelected(false);
			Editor.instance.buttonPaint.setSelected(false);
			Editor.instance.buttonPath.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.instance.labelLevel.update(true);
		}
		else if(arg0.getSource() == Editor.instance.buttonMove)
		{
			Editor.instance.buttonEdit.setSelected(false);
			Editor.instance.buttonPaint.setSelected(false);
			Editor.instance.buttonPath.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.instance.labelLevel.update(true);
		}
		else if(arg0.getSource() == Editor.instance.buttonPaint)
		{
			Editor.instance.buttonEdit.setSelected(false);
			Editor.instance.buttonMove.setSelected(false);
			Editor.instance.buttonPath.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.instance.labelLevel.update(true);
		}
		else if(arg0.getSource() == Editor.instance.buttonPath)
		{
			Editor.instance.buttonEdit.setSelected(false);
			Editor.instance.buttonMove.setSelected(false);
			Editor.instance.buttonPaint.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.instance.labelLevel.update(true);
		}
		else if(arg0.getSource() == Editor.instance.buttonNewEntity)
		{
			Editor.instance.entityEditor.show(null);
		}
	}
}
