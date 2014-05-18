package vic.rpg.editor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import vic.rpg.editor.gui.TileTextureSelector;
import vic.rpg.utils.Utils;
import vic.rpg.world.tiles.TileJSON;

public class TileEditor implements WindowListener
{
	public JDialog frame;
	
	public JTabbedPane tabbedPane;
	public JPanel mainPanel;
	public TileTextureSelector textureSelector;
	
	public JPanel statusBar;
	public JLabel selectedXLabel;
	public JLabel selectedYLabel;
	
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
		textureSelector.setTextureDimension(1, 3);
		textureSelector.addMouseListener(new MouseListener() 
		{
			@Override public void mouseClicked(MouseEvent arg0){}
			@Override public void mouseEntered(MouseEvent arg0){}
			@Override public void mouseExited(MouseEvent arg0){}	
			@Override public void mousePressed(MouseEvent arg0){}

			@Override
			public void mouseReleased(MouseEvent arg0) 
			{
				Point texPoint = textureSelector.getSelectedTexturePoint();
				selectedXLabel.setText(String.valueOf(texPoint.x));
				selectedYLabel.setText(String.valueOf(texPoint.y));
			}
		});
		selectedXLabel = new JLabel("0");
		selectedYLabel = new JLabel("0");
		statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		mainPanelConstraints.fill = GridBagConstraints.BOTH;
		mainPanelConstraints.weightx = 1;
		mainPanelConstraints.weighty = 1;
		JScrollPane sp1 = new JScrollPane(textureSelector);
		sp1.getVerticalScrollBar().setUnitIncrement(16);
		sp1.setBorder(BorderFactory.createTitledBorder("Texture"));
		mainPanel.add(sp1, mainPanelConstraints);
		
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBar.add(new JLabel("X: "));
		statusBar.add(selectedXLabel);
		JSeparator sep1 = new JSeparator(JSeparator.VERTICAL);
		sep1.setPreferredSize(new Dimension(1, 15));
		statusBar.add(sep1);
		statusBar.add(new JLabel("Y: "));
		statusBar.add(selectedYLabel);
		mainPanelConstraints.gridy = 1;
		mainPanelConstraints.weighty = 0;
		mainPanel.add(statusBar, mainPanelConstraints);
		
		tabbedPane.add("Standard Settings", mainPanel);
		frame.add(tabbedPane);
		
		frame.setModal(true);
		frame.setLocationRelativeTo(Editor.instance.frame);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Tile Editor " + (t != null ? "(" + t.getName() + ")" : ""));
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
