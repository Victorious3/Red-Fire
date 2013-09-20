package vic.rpg.editor.listener;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;

import vic.rpg.editor.Editor;
import vic.rpg.editor.gui.LabelEditMaterials;
import vic.rpg.editor.gui.PanelTexture;
import vic.rpg.level.Level;
import vic.rpg.level.TexturePath;
import vic.rpg.level.tiles.TileTerrain;
import vic.rpg.utils.Utils;

public class BrushFrameListener implements MouseListener, ActionListener
{
	public static BrushFrameListener instance = new BrushFrameListener();
	public static BufferedImage terrainImg = Utils.readImageFromJar(TileTerrain.class.getAnnotation(TexturePath.class).path());
	
	public LabelEditMaterials labelEditMaterials;
	public JDialog dialog;
	
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
		Point texLoc = new Point(0, 0);
		pTex.setTexture(terrainImg.getSubimage(texLoc.x * Level.CELL_SIZE, texLoc.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE));
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		labelEditMaterials = new LabelEditMaterials();
		dialog = new JDialog(Editor.instance.frame, "Edit Materials");
		dialog.add(labelEditMaterials, BorderLayout.CENTER);
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
}
