package vic.rpg.editor.listener;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import vic.rpg.editor.Clipboard;
import vic.rpg.editor.Editor;
import vic.rpg.level.Entity;
import vic.rpg.registry.GameRegistry;

public class Key implements KeyListener
{
	public static Key keyListener = new Key();
	
	public boolean shiftPressed = false;
	private int button;
	
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		if(arg0.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shiftPressed = true;
			if(Editor.editor.buttonEdit.isSelected()) button = 1;
			if(Editor.editor.buttonPaint.isSelected()) button = 2;
			
			Editor.editor.buttonEdit.setSelected(false);
			Editor.editor.buttonPaint.setSelected(false);
			
			Editor.editor.buttonMove.setSelected(true);
			
			if(Mouse.mouseHovered)
			{
				if(Editor.editor.frame.getCursor() != GameRegistry.CURSOR_DROP && Editor.editor.frame.getCursor() != GameRegistry.CURSOR_DRAG) 
				{				
					if(!Mouse.mouseDown) Editor.editor.frame.setCursor(GameRegistry.CURSOR_DROP);
					else Editor.editor.frame.setCursor(GameRegistry.CURSOR_DRAG); 
				}
			}
		}
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if(Editor.editor.dropdownMode.getSelectedIndex() == 0) Editor.editor.dropdownMode.setSelectedIndex(1);
			else Editor.editor.dropdownMode.setSelectedIndex(0);
		}
		if(Editor.editor.buttonEdit.isSelected())
		{
			if(arg0.getKeyCode() == KeyEvent.VK_DELETE)
			{
				Clipboard.delete();
			}
				
			if(Editor.editor.dropdownMode.getSelectedIndex() == 1)
			{
				int minX = Editor.editor.level.getWidth();
				int minY = Editor.editor.level.getHeight();
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
						Editor.editor.level.entities.put(e.uniqueUUID, e);
						if(e.xCoord < minX) minX = e.xCoord;
						if(e.yCoord < minY) minY = e.yCoord;
						if(e.xCoord + e.getWidth() > maxX) maxX = e.xCoord + e.getWidth();
						if(e.yCoord + e.getHeight() > maxY) maxY = e.yCoord + e.getHeight();
					}			
					Editor.editor.labelLevel.update(minX, minY, maxX - minX, maxY - minY);
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
				if(button == 1) Editor.editor.buttonEdit.setSelected(true);
				if(button == 2) Editor.editor.buttonPaint.setSelected(true);
				Editor.editor.buttonMove.setSelected(false);
				if(Editor.editor.frame.getCursor() == GameRegistry.CURSOR_DRAG || Editor.editor.frame.getCursor() == GameRegistry.CURSOR_DROP)
				{
					Editor.editor.frame.setCursor(Cursor.getDefaultCursor());
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
