package vic.rpg.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import vic.rpg.editor.gui.JPaintArea;
import vic.rpg.level.Entity;
import vic.rpg.utils.Utils;

public class EntityEditor
{
	public JDialog frame;
	public JTextArea editor = new JTextArea();
	public JPaintArea pArea;
	public JSplitPane splitPane = new JSplitPane();
	public JPanel panelEdit = new JPanel();
	
	public JButton buttonAdd    = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/add.png")));
	public JButton buttonRemove = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/remove.png")));
	public JButton buttonLeft   = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/arrow-left.png")));
	public JButton buttonRight  = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/arrow-right.png")));
	
	public JTextField fieldDimensionX = new JTextField("120");
	public JTextField fieldDimensionY = new JTextField("240");
	public JCheckBox itemIsDimensionAuto = new JCheckBox("Dimensions set automatically?");
	
	public JTextField fieldName = new JTextField("Test");
	public JCheckBox itemIsNameAuto = new JCheckBox("Name set automatically?");
	
	public JTextField fieldImageURL = new JTextField("C:/test/test.img");
	public JButton buttonChooseURL = new JButton("Change...");
	public JButton buttonRefreshImage = new JButton("Refresh");
	public JCheckBox itemIsImageAuto = new JCheckBox("Image set automatically?");
	
	public JCheckBox itemIsBoundsAuto = new JCheckBox("Bounds set automatically?"); 
	
	private GridBagConstraints gbConstraints = new GridBagConstraints();
	
	public EntityEditor()
	{
		
	}
	
	public void show(Entity e)
	{
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.weightx = 1;
		gbConstraints.weighty = 1;
			
		frame = new JDialog();	

		frame.setSize(800, 600);
		frame.setLocationRelativeTo(Editor.instance.frame);
		frame.setTitle("Entity Editor");
		frame.setModal(true);
		
		editor.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
		pArea = new JPaintArea(Utils.readImageFromJar("/vic/rpg/resources/terrain/house.png"));
		
		buttonAdd.setPreferredSize(new Dimension(25, 25));
		buttonRemove.setPreferredSize(new Dimension(25, 25));
		buttonLeft.setPreferredSize(new Dimension(25, 25));
		buttonRight.setPreferredSize(new Dimension(25, 25));
		
		buttonAdd.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				pArea.createNewPoly();
			}
		});
		buttonRemove.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				pArea.removeCurrentPoly();
			}
		});
		buttonLeft.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				pArea.changePoly(-1);
			}
		});
		buttonRight.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				pArea.changePoly(1);
			}
		});
		
		panelEdit.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		panelEdit.add(buttonAdd);
		panelEdit.add(buttonRemove);
		panelEdit.add(buttonLeft);
		panelEdit.add(buttonRight);
		
		JPanel panelLeftBottom = new JPanel();
		JPanel panelLeft = new JPanel();
		
		panelLeftBottom.setLayout(new BoxLayout(panelLeftBottom, BoxLayout.Y_AXIS));
		
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JPanel p4 = new JPanel();
		JPanel p5 = new JPanel();
		
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
		
		p1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p5.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		p1.add(itemIsNameAuto);
		p1.add(fieldName);
		
		p2.add(itemIsDimensionAuto);
		p2.add(fieldDimensionX);
		p2.add(Box.createHorizontalStrut(5));
		p2.add(fieldDimensionY);
		
		itemIsBoundsAuto.setAlignmentX(Component.LEFT_ALIGNMENT);
		p3.add(itemIsBoundsAuto);
		p3.add(Box.createGlue());
		
		p4.add(itemIsImageAuto);
		p4.add(fieldImageURL);
		
		buttonChooseURL.setAlignmentX(Component.RIGHT_ALIGNMENT);
		buttonRefreshImage.setAlignmentX(Component.RIGHT_ALIGNMENT);
		p5.add(Box.createGlue());
		p5.add(buttonChooseURL);
		p5.add(buttonRefreshImage);
		
		panelLeftBottom.add(p1);
		panelLeftBottom.add(p2);
		panelLeftBottom.add(p3);
		panelLeftBottom.add(p4);
		panelLeftBottom.add(p5);
		
		pArea.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.gray));
		
		panelLeft.setLayout(new GridBagLayout());
		gbConstraints.gridx = 1;
		gbConstraints.gridy = 1;
		gbConstraints.weighty = 0;
		panelLeft.add(panelEdit, gbConstraints);
		gbConstraints.gridy = 2;
		gbConstraints.weighty = 1;
		panelLeft.add(pArea, gbConstraints);
		gbConstraints.gridy = 3;
		gbConstraints.weighty = 0;
		panelLeft.add(panelLeftBottom, gbConstraints);
		
		splitPane.add(panelLeft, JSplitPane.LEFT);
		splitPane.add(new JScrollPane(editor), JSplitPane.RIGHT);
		
		pArea.currPoly.add(new Point(0, 5));
		pArea.currPoly.add(new Point(100, 5));
		pArea.currPoly.add(new Point(100, 50));
		pArea.currPoly.add(new Point(80, 6));
		pArea.currPoly.add(new Point(0, 5));
		
		frame.add(splitPane);
		frame.setVisible(true);
	}
	
	public void show()
	{
		frame.setVisible(true);
	}
	
	public void hide()
	{
		frame.setVisible(false);
	}
}
