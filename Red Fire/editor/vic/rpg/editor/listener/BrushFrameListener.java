package vic.rpg.editor.listener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import vic.rpg.editor.Editor;
import vic.rpg.editor.gui.LabelEditMaterials;
import vic.rpg.editor.gui.PanelTexture;
import vic.rpg.editor.tiles.TileMaterial;
import vic.rpg.level.Level;
import vic.rpg.level.TexturePath;
import vic.rpg.level.tiles.TileTerrain;
import vic.rpg.utils.Direction;
import vic.rpg.utils.Utils;

public class BrushFrameListener implements MouseListener, ActionListener, TreeSelectionListener
{
	public static BrushFrameListener instance = new BrushFrameListener();
	public static BufferedImage terrainImg = Utils.readImageFromJar(TileTerrain.class.getAnnotation(TexturePath.class).path());
	
	public LabelEditMaterials labelEditMaterials;
	public JTree treeMaterials;
	public JDialog dialog;
	public JButton buttonNew;
	public JButton buttonDelete;
	
	public TileMaterial inner;
	public TileMaterial outer;
	
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
		String[] values = null;
		if(pTex == Editor.instance.panelTexture1)
		{
			if(outer == null) 
			{
				JOptionPane.showMessageDialog(Editor.instance.frame, "Please select a parent material first.");
				return;
			}
			else
			{
				values = outer.getSubMaterials().keySet().toArray(new String[outer.getSubMaterials().size()]);
			}
		}
		if(pTex == Editor.instance.panelTexture2)
		{
			values = TileMaterial.getMaterials().keySet().toArray(new String[TileMaterial.getMaterials().size()]);
		}
		
		JComboBox<String> mats = new JComboBox<String>(values);
		JOptionPane.showMessageDialog(Editor.instance.frame, mats, "Select material", JOptionPane.PLAIN_MESSAGE);
		
		if(pTex == Editor.instance.panelTexture1)
		{
			inner = TileMaterial.getMaterial((String)mats.getSelectedItem());
		}
		if(pTex == Editor.instance.panelTexture2)
		{
			outer = TileMaterial.getMaterial((String)mats.getSelectedItem());		
		}
		if(inner != null && outer != null)
		{
			Point texLoc = inner.getTextureCoord(outer, Direction.CENTER);
			if(texLoc != null) Editor.instance.panelTexture1.setTexture(terrainImg.getSubimage(texLoc.x * Level.CELL_SIZE, texLoc.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE));
			else Editor.instance.panelTexture1.setTexture(Editor.NO_TEXTURE);
			Point texLoc2 = outer.getTextureCoord(inner, Direction.CENTER);
			if(texLoc2 != null) Editor.instance.panelTexture2.setTexture(terrainImg.getSubimage(texLoc2.x * Level.CELL_SIZE, texLoc2.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE));
			else Editor.instance.panelTexture2.setTexture(Editor.NO_TEXTURE); 
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == Editor.instance.buttonEditBrush)
		{
			labelEditMaterials = new LabelEditMaterials();
			
			treeMaterials = new JTree(new DefaultMutableTreeNode());
			treeMaterials.addTreeSelectionListener(instance);
			reloadTree();
			
			dialog = new JDialog(Editor.instance.frame, "Edit Materials");
			dialog.add(labelEditMaterials, BorderLayout.CENTER);
			JScrollPane scroll1 = new JScrollPane(treeMaterials);
			scroll1.setPreferredSize(new Dimension(150, 0));
			
			JPanel p1 = new JPanel();
			GridBagConstraints gbConstraints = new GridBagConstraints();
			p1.setLayout(new GridBagLayout());
			
			gbConstraints.weightx = 1;
			gbConstraints.weighty = 1;
			gbConstraints.fill = GridBagConstraints.BOTH;
			p1.add(scroll1, gbConstraints);
			buttonNew = new JButton("New...");
			buttonDelete = new JButton("Delete");
			buttonNew.setAlignmentX(Component.LEFT_ALIGNMENT);
			buttonDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
			buttonNew.addActionListener(instance);
			buttonDelete.addActionListener(instance);
			
			gbConstraints.weighty = 0;
			gbConstraints.gridy = 1;
			p1.add(buttonNew, gbConstraints);
			gbConstraints.gridy = 2;
			p1.add(buttonDelete, gbConstraints);
			
			dialog.add(p1, BorderLayout.EAST);			
			
			dialog.setModal(true);
			dialog.setResizable(false);
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
		else if(e.getSource() == buttonDelete)
		{
			if(treeMaterials.getSelectionPaths() == null) return;
			for(TreePath path : treeMaterials.getSelectionPaths())
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if(node.getParent() != null) 
				{
					if(node.getLevel() == 1)
					{
						TileMaterial.getMaterials().remove(node.getUserObject());
					}
					if(node.getLevel() == 2)
					{
						TileMaterial t = TileMaterial.getMaterial((String)((DefaultMutableTreeNode)node.getParent()).getUserObject());
						if(t != null)
						{
							t.getSubMaterials().remove(node.getUserObject());
						}
					}
					((DefaultTreeModel)treeMaterials.getModel()).removeNodeFromParent(node);
				}		
			}
		}
		else if(e.getSource() == buttonNew)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMaterials.getLastSelectedPathComponent();
			if(node != null)
			{
				if(node.getLevel() == 0)
				{
					String name = JOptionPane.showInputDialog(dialog, "Please enter a name:", "Create a new material", JOptionPane.OK_CANCEL_OPTION);
					if(TileMaterial.getMaterials().containsKey(name))
					{
						JOptionPane.showMessageDialog(dialog, "Name already taken!", "Create a new material", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						TileMaterial material = new TileMaterial(name);
						TileMaterial.addMaterial(material);
						reloadTree();
					}
				}
				if(node.getLevel() > 0)
				{
					String name = JOptionPane.showInputDialog(dialog, "Please enter the name of an existing material:", "Create a new material transition", JOptionPane.OK_CANCEL_OPTION);
					String matName = "";
					if(node.getLevel() == 1)
					{
						matName = (String)node.getUserObject();
					}
					else if(node.getLevel() == 2)
					{
						matName = (String) ((DefaultMutableTreeNode)node.getParent()).getUserObject();
					}
					else
					{
						System.err.println("Illegal operation!");
						JOptionPane.showMessageDialog(dialog, "Illegal operation! (U no hacker?)", "Create a new material transition", JOptionPane.ERROR_MESSAGE);
					}
					TileMaterial subMaterial = TileMaterial.getMaterial(name);
					if(subMaterial == null)
					{
						JOptionPane.showMessageDialog(dialog, "There is no such material existing! (" + name +")", "Create a new material transition", JOptionPane.ERROR_MESSAGE);
						return;
					}
					TileMaterial material = TileMaterial.getMaterial(matName);
					material.addSubMaterial(subMaterial);
					reloadTree();
				}
			}
		}
	}

	private void reloadTree() 
	{
		DefaultTreeModel model = (DefaultTreeModel)treeMaterials.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		root.removeAllChildren();
		root.setUserObject("Materials");
		
		String[][] rowData = new String[][]{new String[TileMaterial.getMaterials().size()]};
		TileMaterial[] materials = TileMaterial.getMaterials().values().toArray(new TileMaterial[TileMaterial.getMaterials().size()]);
		for(int i = 0; i < TileMaterial.getMaterials().size(); i++)
		{
			TileMaterial mat = materials[i];
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(mat.getName());
			for(String s : mat.getSubMaterials().keySet())
			{
				node.add(new DefaultMutableTreeNode(s));
			}
			root.add(node);
			rowData[0][i] = mat.getName();
		}
		model.reload();
	}
	
	private void loadSubMaterial(TileMaterial main, TileMaterial subMaterial)
	{
		labelEditMaterials.setMaterials(main, subMaterial);
		labelEditMaterials.clear();
		for(Direction d : Direction.values())
		{
			Point p = main.getTextureCoord(subMaterial, d);
			if(p != null) labelEditMaterials.setDirection(d, p.x, p.y);
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) 
	{
		if(!labelEditMaterials.saveMaterial())
		{
			JOptionPane.showMessageDialog(dialog, "An error occured while saving the last selected material transition.\nPlease check your input.", "Saving material transition", JOptionPane.WARNING_MESSAGE);
		}
		
		if(arg0.getNewLeadSelectionPath() == null) return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)arg0.getNewLeadSelectionPath().getLastPathComponent();
		if(node != null)
		{
			if(node.getLevel() == 2) 
			{
				loadSubMaterial(TileMaterial.getMaterial((String)((DefaultMutableTreeNode)node.getParent()).getUserObject()), TileMaterial.getMaterial((String) node.getUserObject()));
			}
			else labelEditMaterials.clear();
		}
	}
}
