package vic.rpg.editor.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import vic.rpg.editor.Clipboard;
import vic.rpg.editor.Editor;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.editor.listener.TableListener;
import vic.rpg.level.Entity;
import vic.rpg.level.Tile;
import vic.rpg.registry.LevelRegistry;

public class PopupMenu implements ActionListener
{
	public static PopupMenu popupMenu = new PopupMenu();
	
	public JPopupMenu menu = new JPopupMenu();	
	public JMenu menuSelection = new JMenu("Selection");
	
	public JMenuItem copy = new JMenuItem("Copy");
	public JMenuItem cut = new JMenuItem("Cut");
	public JMenuItem replace = new JMenuItem("Replace");
	public JMenuItem delete = new JMenuItem("Delete");
	
	public JMenu menuNew = new JMenu("New");
	
	public JMenu menuTile = new JMenu("Tile");
	public JMenu menuEntity = new JMenu("Entity");
	
	public JMenuItem paste = new JMenuItem("Paste");
	public JMenuItem help = new JMenuItem("Help");
	
	public PopupMenu()
	{
		copy.addActionListener(this);
		cut.addActionListener(this);
		replace.addActionListener(this);
		delete.addActionListener(this);
		paste.addActionListener(this);
		
		menuSelection.add(copy);
		menuSelection.add(cut);
		menuSelection.add(replace);
		menuSelection.add(delete);
		
		for(Tile t : LevelRegistry.tileRegistry.values())
		{
			JMenuItem item = new JMenuItem(t.id + ": " + t.getClass().getSimpleName());
			item.addActionListener(this);
			menuTile.add(item);
		}
		
		for(Entity e : LevelRegistry.entityRegistry.values())
		{
			JMenuItem item = new JMenuItem(e.id + ": " + e.getClass().getSimpleName());
			item.addActionListener(this);
			menuEntity.add(item);
		}
		
		menuNew.add(menuTile);
		menuNew.add(menuEntity);
		
		menu.add(menuSelection);
		menu.add(menuNew);
		menu.add(help);
		menu.addSeparator();
		menu.add(paste);
	}
	
	private int x = 0;
	private int y = 0;
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource() == copy || arg0.getSource() == cut)
		{
			Clipboard.copy();				
		}
		if(arg0.getSource() == delete || arg0.getSource() == cut || arg0.getSource() == replace)
		{
			Clipboard.delete();
		}
		if(arg0.getSource() == paste || arg0.getSource() == replace)
		{
			Clipboard.paste(x, y);
		}
		if(arg0.getSource() instanceof JMenuItem)
		{
			if(((JPopupMenu)((JMenuItem)arg0.getSource()).getParent()).getInvoker() == menuEntity)
			{
				int id =  Integer.parseInt(((JMenuItem)arg0.getSource()).getText().split(":")[0]);
						
				Mouse.paint(x, y, id, true);
			}
			else if(((JPopupMenu)((JMenuItem)arg0.getSource()).getParent()).getInvoker() == menuTile)
			{
				int id =  Integer.parseInt(((JMenuItem)arg0.getSource()).getText().split(":")[0]);
				
				int minX = Editor.instance.level.width;
				int minY = Editor.instance.level.height;
				int maxX = 0;
				int maxY = 0;
				
				if(Mouse.selectedTiles.size() != 0)
				{
					for(Point p : Mouse.selectedTiles)
					{
						if(p.x < minX) minX = p.x;
						if(p.y < minY) minY = p.y;
						if(p.x > maxX) maxX = p.x;
						if(p.y > maxY) maxY = p.y;
						
						Editor.instance.level.setTile(id, p.x, p.y, TableListener.tiles.get(id), Editor.layerID);			
					}
					Editor.instance.labelLevel.updateUI();
				}
				else Mouse.paint(x, y, id, false);
			}
		}
	}

	/**
	 * Opens the right click menu.
	 * @param component
	 * @param x
	 * @param y
	 */
	public static void show(Component component, int x, int y) 
	{
		popupMenu.x = x;
		popupMenu.y = y;
		
		if(Mouse.selectedEntities.size() == 0 && Mouse.selectedTiles.size() == 0)
		{
			popupMenu.menuSelection.setEnabled(false);
		}
		else popupMenu.menuSelection.setEnabled(true); 
		
		if(Clipboard.entities.size() == 0 && Clipboard.tiles.size() == 0)
		{
			popupMenu.paste.setEnabled(false);
		}
		else popupMenu.paste.setEnabled(true); 
		
		popupMenu.menu.show(component, x, y);
	}	
}
