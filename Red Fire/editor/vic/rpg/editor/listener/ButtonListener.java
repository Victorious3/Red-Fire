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
		if(arg0.getSource() == Editor.editor.open)
		{
			Editor.openLevel();
		}
		else if(arg0.getSource() == Editor.editor.newLevel)
		{
			Editor.createNewLevel();
		}
		else if(arg0.getSource() == Editor.editor.save)
		{
			if(file == null)
			{
				JOptionPane.showMessageDialog(null, "No file selected", "Save...", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				Editor.editor.level.writeToFile(file);
            	JOptionPane.showMessageDialog(null, "Level \"" + Editor.editor.level.name + "\" was saved", "Save", JOptionPane.INFORMATION_MESSAGE);  	
			}
		}
		else if(arg0.getSource() == Editor.editor.saveas)
		{
			Editor.saveLevel();
		}
		else if(arg0.getSource() == Editor.editor.exit)
		{
			
		}
		else if(arg0.getSource() == Editor.editor.undo)
		{
			
		}
		else if(arg0.getSource() == Editor.editor.redo)
		{
			
		}
		else if(arg0.getSource() == Editor.editor.newTile)
		{
			
		}
		else if(arg0.getSource() instanceof JMenuItem)
		{
			if(((JPopupMenu)((JMenuItem)arg0.getSource()).getParent()).getInvoker() == Editor.editor.run)
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
		else if(arg0.getSource() == Editor.editor.copy)
		{
			Clipboard.copy();
		}
		else if(arg0.getSource() == Editor.editor.paste)
		{
			Clipboard.paste(0, 0);
		}
		else if(arg0.getSource() == Editor.editor.delete)
		{
			Clipboard.delete();
		}
		else if(arg0.getSource() == Editor.editor.buttonZoomIn)
		{
			ZoomListener.setZoom(Editor.editor.dropdownZoom, ZoomListener.getZoom(Editor.editor.dropdownZoom, 0) + 0.1F);
		}
		else if(arg0.getSource() == Editor.editor.buttonZoomOut)
		{
			ZoomListener.setZoom(Editor.editor.dropdownZoom, ZoomListener.getZoom(Editor.editor.dropdownZoom, 0) - 0.1F);
		}
		else if(arg0.getSource() == Editor.editor.buttonRefresh)
		{
			Editor.editor.labelLevel.update(false);
			Editor.editor.frame.setTitle("Red Fire Level Editor (" + Editor.editor.level.name + ")");			
		}
		else if(arg0.getSource() == Editor.editor.buttonEdit)
		{
			Editor.editor.buttonMove.setSelected(false);
			Editor.editor.buttonPaint.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.editor.labelLevel.update(true);
		}
		else if(arg0.getSource() == Editor.editor.buttonMove)
		{
			Editor.editor.buttonEdit.setSelected(false);
			Editor.editor.buttonPaint.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.editor.labelLevel.update(true);
		}
		else if(arg0.getSource() == Editor.editor.buttonPaint)
		{
			Editor.editor.buttonEdit.setSelected(false);
			Editor.editor.buttonMove.setSelected(false);
			
			Mouse.selectedEntities.clear();
			Mouse.selectedTiles.clear();
			Editor.editor.labelLevel.update(true);
		}
	}
}
