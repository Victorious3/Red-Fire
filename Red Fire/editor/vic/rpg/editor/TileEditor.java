package vic.rpg.editor;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import vic.rpg.editor.gui.TileTextureSelector;
import vic.rpg.level.tiles.TileJSON;
import vic.rpg.utils.Utils;

public class TileEditor implements WindowListener
{
	public JDialog frame;
	
	public JTabbedPane tabbedPane;
	public JPanel mainPanel;
	public TileTextureSelector textureSelector;
	
	public void show()
	{
		show(null);
	}
	
	public void show(TileJSON t)
	{
		frame = new JDialog(Editor.instance.frame);		
		frame.setSize(800, 600);
		
		mainPanel = new JPanel();	
		tabbedPane = new JTabbedPane();
		textureSelector = new TileTextureSelector(t != null ? (Utils.getAppdata() + "/resources/tiles/" + t.getTexturePath()) : null);
		
		mainPanel.add(new JScrollPane(textureSelector));
		tabbedPane.add("Standard Settings", mainPanel);
		frame.add(tabbedPane);
		
		frame.setModal(true);
		frame.setLocationRelativeTo(Editor.instance.frame);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Tile Editor");
		frame.addWindowListener(this);
		GuiState.restore(frame, "tile_dialog");
		frame.setVisible(true);
	}

	@Override public void windowActivated(WindowEvent arg0){}
	@Override public void windowClosed(WindowEvent arg0){}
	
	@Override
	public void windowClosing(WindowEvent arg0)
	{
		GuiState.save(frame, "tile_dialog");
		frame.setVisible(false);
	}
	
	@Override public void windowDeactivated(WindowEvent arg0){}
	@Override public void windowDeiconified(WindowEvent arg0){}
	@Override public void windowIconified(WindowEvent arg0){}
	@Override public void windowOpened(WindowEvent arg0){}
}
