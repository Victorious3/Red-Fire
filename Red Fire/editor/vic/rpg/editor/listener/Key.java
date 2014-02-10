package vic.rpg.editor.listener;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import vic.rpg.editor.Clipboard;
import vic.rpg.editor.Editor;
import vic.rpg.level.entity.Entity;
import vic.rpg.registry.GameRegistry;

public class Key implements KeyListener
{
	public static Key keyListener = new Key();
	
	public boolean shiftPressed = false;
	public int button;
	
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		if(arg0.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shiftPressed = true;
			if(Editor.instance.buttonEdit.isSelected()) button = 1;
			if(Editor.instance.buttonPaint.isSelected()) button = 2;
			if(Editor.instance.buttonPath.isSelected()) button = 3;
			if(Editor.instance.buttonErase.isSelected()) button = 4;
			
			Editor.instance.buttonEdit.setSelected(false);
			Editor.instance.buttonPaint.setSelected(false);
			Editor.instance.buttonPath.setSelected(false);
			Editor.instance.buttonErase.setSelected(false);

			
			Editor.instance.buttonMove.setSelected(true);
			
			if(Mouse.mouseHovered)
			{
				if(Editor.instance.frame.getCursor() != GameRegistry.CURSOR_DROP && Editor.instance.frame.getCursor() != GameRegistry.CURSOR_DRAG) 
				{				
					if(!Mouse.mouseDown) Editor.instance.frame.setCursor(GameRegistry.CURSOR_DROP);
					else Editor.instance.frame.setCursor(GameRegistry.CURSOR_DRAG); 
				}
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(Editor.instance.tabpanelEditor.getSelectedIndex() == 1) Editor.instance.tabpanelEditor.setSelectedIndex(2);
			else Editor.instance.tabpanelEditor.setSelectedIndex(1);
		}
		if(Editor.instance.buttonEdit.isSelected())
		{
			if(arg0.getKeyCode() == KeyEvent.VK_DELETE)
			{
				Clipboard.delete();
			}
				
			if(Editor.instance.tabpanelEditor.getSelectedIndex() == 2)
			{
				int minX = Editor.instance.level.getWidth();
				int minY = Editor.instance.level.getHeight();
				int maxX = 0;
				int maxY = 0;
				
				int plusX = 0;
				int plusY = 0;
				
				if(arg0.getKeyCode() == KeyEvent.VK_RIGHT) plusX = 5;
				if(arg0.getKeyCode() == KeyEvent.VK_LEFT) plusX = -5;
				if(arg0.getKeyCode() == KeyEvent.VK_UP) plusY = -5;
				if(arg0.getKeyCode() == KeyEvent.VK_DOWN) plusY = 5;
				
				if(plusY != 0 || plusX != 0)
				{
					for(Entity e : Mouse.selectedEntities)
					{
						e.xCoord += plusX;
						e.yCoord += plusY;
						Editor.instance.level.entityMap.put(e.UUID, e);
						if(e.xCoord < minX) minX = e.xCoord;
						if(e.yCoord < minY) minY = e.yCoord;
						if(e.xCoord + e.getWidth() > maxX) maxX = e.xCoord + e.getWidth();
						if(e.yCoord + e.getHeight() > maxY) maxY = e.yCoord + e.getHeight();
					}			
					Editor.instance.labelLevel.updateUI();
					Mouse.selection = null;
				}
			}
			
			if(arg0.isControlDown())
			{
				if(arg0.getKeyCode() == KeyEvent.VK_C)
				{
					Clipboard.copy();
				}
				if(arg0.getKeyCode() == KeyEvent.VK_V)
				{
					Clipboard.paste(Mouse.xCoord, Mouse.yCoord);
				}
				if(arg0.getKeyCode() == KeyEvent.VK_X)
				{
					Clipboard.copy();
					Clipboard.delete();				
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) 
	{
		if(shiftPressed) 
		{
			shiftPressed = false;
			if(button > 0)
			{
				if(button == 1) Editor.instance.buttonEdit.setSelected(true);
				if(button == 2) Editor.instance.buttonPaint.setSelected(true);
				if(button == 3) Editor.instance.buttonPath.setSelected(true);
				if(button == 4) Editor.instance.buttonErase.setSelected(true);
				
				Editor.instance.buttonMove.setSelected(false);
				Editor.instance.labelLevel.updateUI();
				
				if(Editor.instance.frame.getCursor() == GameRegistry.CURSOR_DRAG || Editor.instance.frame.getCursor() == GameRegistry.CURSOR_DROP)
				{
					Editor.instance.frame.setCursor(Cursor.getDefaultCursor());
				}
			}
			button = 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		
	}
}
