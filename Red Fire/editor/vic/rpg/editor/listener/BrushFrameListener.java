package vic.rpg.editor.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import vic.rpg.editor.gui.PanelTexture;

public class BrushFrameListener implements MouseListener
{
	public static BrushFrameListener instance = new BrushFrameListener();

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		changeTexture((PanelTexture)e.getComponent());
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	
	private void changeTexture(PanelTexture pTex)
	{
		
	}
}
