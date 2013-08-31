package vic.rpg.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;
import javax.swing.text.PlainDocument;

import vic.rpg.editor.gui.PanelEntity;
import vic.rpg.editor.listener.TableListener;
import vic.rpg.level.Entity;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.utils.Utils;

public class EntityEditor implements WindowListener
{
	public JFrame frame;
	public JTextArea editor;
	public PanelEntity pArea;
	public JSplitPane splitPane;
	public JPanel panelEdit;
	
	public JButton buttonAdd;
	public JButton buttonRemove;
	public JButton buttonLeft;
	public JButton buttonRight;
	public JButton buttonSave;
	
	public JTextField fieldDimensionX;
	public JTextField fieldDimensionY;
	public JCheckBox boxIsDimensionAuto;
	
	public JTextField fieldName;
	public JCheckBox boxIsNameAuto;
	
	public JTextField fieldImageURL;
	public JButton buttonChooseURL;
	public JCheckBox boxIsImageAuto;
	
	public JCheckBox boxIsBoundsAuto; 
	
	public JTextField fieldID;
	
	private GridBagConstraints gbConstraints = new GridBagConstraints();
	private String n = System.getProperty("line.separator");

	public void show(Entity e)
	{		
		editor = new JTextArea();
		splitPane = new JSplitPane();
		panelEdit = new JPanel();
		
		buttonAdd = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/add.png")));
		buttonRemove = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/remove.png")));
		buttonLeft = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/arrow-left.png")));
		buttonRight = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/arrow-right.png")));
		buttonSave = new JButton("Save");	
		fieldDimensionX = new JTextField("0");
		fieldDimensionY = new JTextField("0");
		boxIsDimensionAuto = new JCheckBox("Dimension set automatically?");	
		fieldName = new JTextField("");
		boxIsNameAuto = new JCheckBox("Name set automatically?");	
		fieldImageURL = new JTextField("");
		buttonChooseURL = new JButton("Change...");
		boxIsImageAuto = new JCheckBox("Image set automatically?");	
		boxIsBoundsAuto = new JCheckBox("Bounds set automatically?"); 	
		fieldID = new JTextField("1");
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.weightx = 1;
		gbConstraints.weighty = 1;
			
		frame = new JFrame();	
		
		Editor.instance.frame.setEnabled(false);
		
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(Editor.instance.frame);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Entity Editor");
		frame.addWindowListener(this);
		
		editor.getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
		pArea = new PanelEntity(null, this);
		
		if(e != null) loadEntity(e);
		
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
		buttonSave.addActionListener(new ActionListener() 
		{			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				try {
					saveEntity();
				} catch (Exception e) {}
			}
		});
		
		buttonSave.setAlignmentX(Component.RIGHT_ALIGNMENT);
		buttonAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonRemove.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonLeft.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonRight.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		panelEdit.setLayout(new BoxLayout(panelEdit, BoxLayout.X_AXIS));
		panelEdit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		panelEdit.add(buttonAdd);
		panelEdit.add(buttonRemove);
		panelEdit.add(buttonLeft);
		panelEdit.add(buttonRight);
		panelEdit.add(Box.createHorizontalGlue());
		panelEdit.add(buttonSave);
		
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
		
		boxIsNameAuto.setSelected(true);
		boxIsDimensionAuto.setSelected(true);
		boxIsBoundsAuto.setSelected(true);
		boxIsImageAuto.setSelected(true);		
		
		boxIsNameAuto.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.DESELECTED)
				{
					fieldName.setEnabled(false);
				}
				if(arg0.getStateChange() == ItemEvent.SELECTED)
				{
					fieldName.setEnabled(true);
				}
			}		
		});	
		boxIsDimensionAuto.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.DESELECTED)
				{
					fieldDimensionX.setEnabled(false);
					fieldDimensionY.setEnabled(false);
					pArea.updateUI();
				}
				if(arg0.getStateChange() == ItemEvent.SELECTED)
				{
					fieldDimensionX.setEnabled(true);
					fieldDimensionY.setEnabled(true);
					pArea.updateUI();
				}
			}		
		});
		boxIsBoundsAuto.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.DESELECTED)
				{
					buttonAdd.setEnabled(false);
					buttonRemove.setEnabled(false);
					buttonLeft.setEnabled(false);
					buttonRight.setEnabled(false);
					pArea.updateUI();
				}
				if(arg0.getStateChange() == ItemEvent.SELECTED)
				{
					buttonAdd.setEnabled(true);
					buttonRemove.setEnabled(true);
					buttonLeft.setEnabled(true);
					buttonRight.setEnabled(true);
					pArea.updateUI();
				}
			}		
		});
		boxIsImageAuto.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.DESELECTED)
				{
					buttonChooseURL.setEnabled(false);
					pArea.updateUI();
				}
				if(arg0.getStateChange() == ItemEvent.SELECTED)
				{				
					buttonChooseURL.setEnabled(true);
					pArea.updateUI();
				}
			}		
		});		
		buttonChooseURL.addActionListener(new ActionListener() 
		{		
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				updateImage();
			}
		});
		
		fieldImageURL.setEnabled(false);
		fieldName.getDocument().addDocumentListener(new DocumentListener() 
		{			
			@Override public void removeUpdate(DocumentEvent arg0) {changed(arg0);}
			@Override public void insertUpdate(DocumentEvent arg0) {changed(arg0);}
			@Override public void changedUpdate(DocumentEvent arg0) {}
			
			private void changed(DocumentEvent arg0)
			{
				String text = fieldName.getText();
				if(text.contains(" "))
				{
					Toolkit.getDefaultToolkit().beep();
				}
				else
				{
					editor.setText(editor.getText().replace("class " + name + " extends", "class " + text + " extends"));
					editor.setText(editor.getText().replace("public " + name + "()", "public " + text + "()"));
					editor.setText(editor.getText().replace(name + " instance = new " + name + "();", text + " instance = new " + text + "();"));
					
					name = text;
				}
			}
		});
		fieldID.getDocument().addDocumentListener(new DocumentListener() 
		{			
			@Override public void removeUpdate(DocumentEvent arg0) {changed(arg0);}
			@Override public void insertUpdate(DocumentEvent arg0) {changed(arg0);}
			@Override public void changedUpdate(DocumentEvent arg0) {}
			
			private void changed(DocumentEvent arg0)
			{
				String text = fieldID.getText();
				if(!text.matches("^[1-9]\\d*$"))
				{
					Toolkit.getDefaultToolkit().beep();
				}
				else
				{
					editor.setText(editor.getText().replace
					(
						"\tpublic int getSuggestedID()" + n +
						"\t{" + n +
						"\t\treturn " + id + ";" + n +
						"\t}" + n,
						
						"\tpublic int getSuggestedID()" + n +
						"\t{" + n +
						"\t\treturn " + text + ";" + n +
						"\t}" + n
					));
					id = text;
				}
			}
		});
		DocumentListener docListener = new DocumentListener() 
		{
			@Override public void removeUpdate(DocumentEvent arg0) {changed(arg0);}
			@Override public void insertUpdate(DocumentEvent arg0) {changed(arg0);}
			@Override public void changedUpdate(DocumentEvent arg0) {}
			
			private void changed(DocumentEvent arg0)
			{
				String text1 = fieldDimensionX.getText();
				String text2 = fieldDimensionY.getText();
				
				if(!text1.matches("^[1-9]\\d*$"))
				{
					Toolkit.getDefaultToolkit().beep();
				}
				else
				{
					editor.setText(editor.getText().replace("super(" + dimX + ", " + dimY + ")", "super(" + text1 + ", " + text2 + ")"));
					pArea.updateUI();
					
					dimX = text1;
					dimY = text2;
				}
			}
		};
		fieldDimensionX.getDocument().addDocumentListener(docListener);
		fieldDimensionY.getDocument().addDocumentListener(docListener);
		
		p1.add(boxIsNameAuto);
		p1.add(fieldName);
		
		p2.add(boxIsDimensionAuto);
		p2.add(fieldDimensionX);
		p2.add(Box.createHorizontalStrut(5));
		p2.add(fieldDimensionY);
		
		boxIsBoundsAuto.setAlignmentX(Component.LEFT_ALIGNMENT);
		p3.add(boxIsBoundsAuto);
		p3.add(Box.createGlue());
		
		p4.add(boxIsImageAuto);
		p4.add(fieldImageURL);
		
		buttonChooseURL.setAlignmentX(Component.RIGHT_ALIGNMENT);
		JLabel l1 = new JLabel("ID:");
		l1.setAlignmentX(Component.LEFT_ALIGNMENT);
		fieldID.setAlignmentX(Component.LEFT_ALIGNMENT);
		p5.add(Box.createHorizontalStrut(5));
		p5.add(l1);
		p5.add(Box.createHorizontalStrut(5));
		p5.add(fieldID);
		p5.add(Box.createHorizontalStrut(5));
		p5.add(buttonChooseURL);
		
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
		
		if(e == null) generateNewDocument();
		
		frame.add(splitPane);
		frame.setVisible(true);
	}
	
	public void show()
	{
		show(null);
	}
	
	public void hide()
	{
		Editor.instance.frame.setEnabled(true);
		frame.setVisible(false);
	}
	
	private void loadEntity(Entity e)
	{
		byte[] b;
		try {
			b = Files.readAllBytes(Paths.get(Utils.getAppdata() + "/resources/entities/" + e.getClass().getSimpleName() + ".bsh"));
			editor.setText(new String(b));
			
			ArrayList<String> xCoords = new ArrayList<String>();	
			ArrayList<String> yCoords = new ArrayList<String>();
			
			int start = editor.getText().indexOf("public Area getCollisionBoxes(Area area)");
			int i = editor.getText().indexOf("Polygon()", start);
			while(i != -1)
			{		
				int j = editor.getText().indexOf("xpoints = new int[]{", i);
				j += "xpoints = new int[]{".length();
				int k = editor.getText().indexOf("};", j);
				String sub = editor.getText().substring(j, k);
				xCoords.add(sub);
				i = editor.getText().indexOf("Polygon()", i + "Polygon()".length());
			}
			
			i = editor.getText().indexOf("Polygon()", start);
			while(i != -1)
			{		
				int j = editor.getText().indexOf("ypoints = new int[]{", i);
				j += "ypoints = new int[]{".length();
				int k = editor.getText().indexOf("};", j);
				String sub = editor.getText().substring(j, k);
				yCoords.add(sub);
				i = editor.getText().indexOf("Polygon()", i + "Polygon()".length());
			}
			
			ArrayList<ArrayList<Point>> allPolys = new ArrayList<ArrayList<Point>>();
			for(int j = 0; j < xCoords.size(); j++)
			{
				String sub = xCoords.get(j);
				sub = sub.replaceAll("[^0-9,]", "");
				sub = sub.replace(" ", "");
				String[] numbersX = sub.split(",");
				
				sub = yCoords.get(j);
				sub = sub.replaceAll("[^0-9,]", "");
				sub = sub.replace(" ", "");
				String[] numbersY = sub.split(",");
				
				ArrayList<Point> pList = new ArrayList<Point>();			
				for(int k = 0; k < numbersX.length; k++)
				{
					int pX = Integer.parseInt(numbersX[k]);
					int pY = Integer.parseInt(numbersY[k]);
					
					Point p = new Point(pX, pY);
					pList.add(p);
				}
				allPolys.add(pList);
			}
			
			pArea.polys = allPolys;
			if(allPolys.size() > 0)
			{
				pArea.currPoly = allPolys.get(0);
			}
			else pArea.createNewPoly();
			
			fieldName.setText(e.getClass().getSimpleName());
			fieldDimensionX.setText(e.getWidth() + "");
			fieldDimensionY.setText(e.getHeight() + "");
			fieldID.setText(e.id + "");
			
			int l = editor.getText().indexOf("this.setTexture(TextureLoader.requestTexture(ImageIO.read(new File(Utils.getAppdata() + \"") + "this.setTexture(TextureLoader.requestTexture(ImageIO.read(new File(Utils.getAppdata() + \"".length();
			int m = editor.getText().indexOf("\"))))", l);
			String url = editor.getText().substring(l, m);
			fieldImageURL.setText(url);
			pArea.setImage(ImageIO.read(new File(Utils.getAppdata() + url)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}
	
	private String name = "";
	public String dimX = "0";
	public String dimY = "0";
	private String url = "";
	private String id = "0";
	
	private void generateNewDocument()
	{
		String text = 
		"//IMPORTANT! Don't change any of the existing methods if you want the auto edit to be sufficent!" + n +
		"//IMPORTS" + n +
		"import vic.rpg.level.entity.*;" + n +
		"import vic.rpg.render.TextureLoader;" + n +
		"import vic.rpg.utils.Utils;" + n +
		"import java.awt.geom.Area;" + n +
		"import java.awt.Polygon;" + n +
		"import java.io.File;" + n +
		"import javax.imageio.ImageIO;" + n +
		n +
		"class " + fieldName.getText() + " extends EntityCustom" + n +	
		"{" + n +
		"\tpublic " + fieldName.getText() + "()" + n +
		"\t{" + n +
		"\t\tsuper(" + fieldDimensionX.getText() + ", " + fieldDimensionY.getText() +");" + n +
		"\t\tif(Utils.getSide().equals(Utils.SIDE_CLIENT)) this.setTexture(TextureLoader.requestTexture(ImageIO.read(new File(Utils.getAppdata() + \"" + fieldImageURL.getText() + "\"))));" + n +
		"\t}" + n +
		n +
		"\tpublic int getSuggestedID()" + n +
		"\t{" + n +
		"\t\treturn " + fieldID.getText() + ";" + n +
		"\t}" + n +
		n +
		"\tpublic Area getCollisionBoxes(Area area)" + n +
		"\t{" + n +
		"\t\treturn area;" + n +
		"\t}" + n +
		"}" + n +
		n +
		"//INSTANCE (Do not change!)" + n +
		fieldName.getText() + " instance = new " + fieldName.getText() + "();"
		;
		name = fieldName.getText();
		dimX = fieldDimensionX.getText();
		dimY = fieldDimensionY.getText();
		url = fieldImageURL.getText();
		id = fieldID.getText();
		
		editor.setText(text);
	}
	
	private void updateImage()
	{
		final File dir = Utils.getOrCreateFile((Utils.getAppdata() + "/resources/entities/"));
		JFileChooser chooser = new JFileChooser(dir);
		chooser.setFileView(new FileView() 
		{
			@Override
		    public Boolean isTraversable(File f) {
		         return dir.equals(f);
		    }
		});
		FileNameExtensionFilter plainFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());	
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.addChoosableFileFilter(plainFilter); 
        chooser.setDialogTitle("Save as..."); 
        chooser.setVisible(true); 

        int result = chooser.showOpenDialog(frame);
        
        if(result == JFileChooser.APPROVE_OPTION)
        {
        	String file = chooser.getSelectedFile().toString();
        	file = Utils.replaceBackslashes(file);
        	
        	BufferedImage img = null;		
        	try {
				img = ImageIO.read(chooser.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
			}		
        	pArea.setImage(img);
        	
        	fieldImageURL.setText(file.replace(Utils.getAppdata(), ""));
			fieldDimensionX.setText(String.valueOf(img.getWidth()));
        	fieldDimensionY.setText(String.valueOf(img.getHeight()));
			
        	editor.setText(editor.getText().replace("this.setTexture(TextureLoader.requestTexture(ImageIO.read(new File(Utils.getAppdata() + \"" + url + "\"))))", "this.setTexture(TextureLoader.requestTexture(ImageIO.read(new File(Utils.getAppdata() + \"" + fieldImageURL.getText() + "\"))))"));
			url = fieldImageURL.getText();
        }
	}
	
	public void saveEntity() throws Exception
	{
		File f = Utils.getOrCreateFile(Utils.getAppdata() + "/resources/entities/" + fieldName.getText() + ".bsh");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(editor.getText());
			fw.close();
			
			Entity ent = LevelRegistry.addNewEntity(f);
			TableListener.entities.put(ent.id, LevelRegistry.entityRegistry.get(ent.id).clone());
			Editor.instance.updateTilesAndEntites();
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this.frame, e1.toString() + "\nat " + e1.getStackTrace()[0].toString() + "\nPlease correct your input.", "Entity could'nt be saved!", JOptionPane.ERROR_MESSAGE);
			f.delete();
			throw new Exception("Entity could'nt be saved!");
		}
	}

	@Override public void windowActivated(WindowEvent e) {}
	@Override public void windowClosed(WindowEvent e) {}
	
	@Override public void windowClosing(WindowEvent e) 
	{
		int i = JOptionPane.showConfirmDialog(this.frame, "Do you want to save this entity as " + fieldName.getText() + ".bsh ?", "Entity Editor", JOptionPane.YES_NO_CANCEL_OPTION);	
		if(i == JOptionPane.YES_OPTION)
		{
			try {
				saveEntity();
				hide();
			} catch (Exception e1) {}		
		}
		else if(i == JOptionPane.NO_OPTION) hide();
	}
	
	@Override public void windowDeactivated(WindowEvent e) {}
	@Override public void windowDeiconified(WindowEvent e) {}
	@Override public void windowIconified(WindowEvent e) {}
	@Override public void windowOpened(WindowEvent e) {}
}
