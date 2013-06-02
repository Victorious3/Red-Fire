package vic.rpg.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import vic.rpg.editor.gui.JBackgroundPanel;
import vic.rpg.editor.listener.ButtonListener;
import vic.rpg.editor.listener.Key;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.editor.listener.TableListener;
import vic.rpg.editor.listener.ZoomListener;
import vic.rpg.editor.render.LabelLevel;
import vic.rpg.editor.script.Script;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;
import vic.rpg.level.Tile;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.utils.Utils;

public class Editor 
{
	public static Editor editor;
	
	public JFrame frame;
	
	public JMenuBar menubar   = new JMenuBar();	
	
	public JMenu menuFile     = new JMenu("File");
	public JMenuItem newLevel = new JMenuItem("New...");
	public JMenuItem open     = new JMenuItem("Open...");
	public JMenuItem save     = new JMenuItem("Save", UIManager.getIcon("FileView.floppyDriveIcon"));
	public JMenuItem saveas   = new JMenuItem("Save As...");
	public JMenuItem exit     = new JMenuItem("Exit");
	
	public JMenu menuEdit     = new JMenu("Edit");
	public JMenuItem undo	  = new JMenuItem("Undo");
	public JMenuItem redo     = new JMenuItem("Redo");
	public JMenuItem newTile  = new JMenuItem("New...");	
	public JMenu run          = new JMenu("Run...");	
	public JMenuItem copy     = new JMenuItem("Copy");
	public JMenuItem paste    = new JMenuItem("Paste");
	public JMenuItem delete   = new JMenuItem("Delete");
	
	public JSplitPane panelMain = new JSplitPane();
	
	public JPanel panelEast   = new JPanel(); 
	public JPanel panelRender = new JBackgroundPanel(Utils.readImageFromJar("/vic/rpg/resources/editor/transparent_bg.png"));
	public JPanel panelEdit   = new JPanel();
	public LabelLevel labelLevel = new LabelLevel();
	
	public JComboBox<String> dropdownZoom = new JComboBox<String>(new String[]{"500%", "400%", "300%", "200%", "100%", "66%", "50%", "33%", "25%", "16%", "10%"});
	public JButton buttonZoomIn      = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/zoom-in.png")));
	public JButton buttonZoomOut     = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/zoom-out.png")));
	public JButton buttonRefresh     = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/repeat-2.png")));
	public JToggleButton buttonMove  = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/move.png")));
	public JToggleButton buttonEdit  = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/swap.png")));
	public JToggleButton buttonPaint = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/pencil.png")));
	public JComboBox<String> dropdownMode = new JComboBox<String>(new String[]{"Tiles", "Entities"});
	
	public JTabbedPane tabpanelEditor = new JTabbedPane();
	public JPanel panelTiles = new JPanel();
	public JPanel panelEntities = new JPanel();
	public JPanel panelLevel = new JPanel();
	
	public JComboBox<String> dropdownTiles = new JComboBox<String>();
	public JComboBox<String> dropdownEntities = new JComboBox<String>();
	
	public JTable tableLevel = new JTable(new DefaultTableModel(new String[][]{}, new String[]{"NBTTag", "Type", "value"}))
	{
		@Override
		public boolean isCellEditable(int row, int column) 
		{
			return column == 2;
		}
	};
	
	public JTable tableEntities = new JTable(new DefaultTableModel(new String[][]{}, new String[]{"NBTTag", "Type", "value"}))
	{
		@Override
		public boolean isCellEditable(int row, int column) 
		{
			return column == 2;
		}
	};
	
	public JTable tableTiles = new JTable(new DefaultTableModel(new String[][]{}, new String[]{"NBTTag", "Type", "value"}))
	{
		@Override
		public boolean isCellEditable(int row, int column) 
		{
			return column == 2;
		}
	};
	public JLabel labelTiles = new JLabel();
	
	public Level level;
	
	public Editor()
	{				
		frame = new JFrame();	
		if(!isInternal) frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(1024, 720);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Red Fire Level Editor");
		
		File[] files = Utils.getOrCreateFile(Utils.getAppdata() + "/scripts/").listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
		}});
		
		for(File file : files)
		{
			JMenuItem item = new JMenuItem(Utils.stripExtension(file.getName()));
			item.addActionListener(ButtonListener.listener);
			run.add(item);
		}
		
		open.addActionListener(ButtonListener.listener);
		newLevel.addActionListener(ButtonListener.listener);
		save.addActionListener(ButtonListener.listener);
		saveas.addActionListener(ButtonListener.listener);
		exit.addActionListener(ButtonListener.listener);
		undo.addActionListener(ButtonListener.listener);
		redo.addActionListener(ButtonListener.listener);
		newTile.addActionListener(ButtonListener.listener);
		run.addActionListener(ButtonListener.listener);
		copy.addActionListener(ButtonListener.listener);
		paste.addActionListener(ButtonListener.listener);
		delete.addActionListener(ButtonListener.listener);
		
		menuFile.add(open);
		menuFile.add(newLevel);
		menuFile.add(save);
		menuFile.add(saveas);
		menuFile.addSeparator();
		menuFile.add(exit);
		
		menuEdit.add(undo);
		menuEdit.add(redo);
		menuEdit.addSeparator();
		menuEdit.add(newTile);
		menuEdit.add(run);
		menuEdit.addSeparator();
		menuEdit.add(copy);
		menuEdit.add(paste);
		menuEdit.add(delete);
		
		menubar.add(menuFile);
		menubar.add(menuEdit);		
		
		labelLevel.setSize(10, 10);		
		
		Mouse mouse = new Mouse();
		
		dropdownZoom.setEditable(true);
		dropdownZoom.setSelectedItem("100%");
		dropdownZoom.addActionListener(new ZoomListener());
		
		buttonZoomIn.setPreferredSize(new Dimension(25, 25));
		buttonZoomIn.addActionListener(ButtonListener.listener);
		buttonZoomOut.setPreferredSize(new Dimension(25, 25));
		buttonZoomOut.addActionListener(ButtonListener.listener);
		buttonRefresh.setPreferredSize(new Dimension(25, 25));
		buttonRefresh.addActionListener(ButtonListener.listener);
		buttonEdit.setPreferredSize(new Dimension(25, 25));
		buttonEdit.addActionListener(ButtonListener.listener);
		buttonMove.setPreferredSize(new Dimension(25, 25));
		buttonMove.addActionListener(ButtonListener.listener);
		buttonPaint.setPreferredSize(new Dimension(25, 25));
		buttonPaint.addActionListener(ButtonListener.listener);
		
		buttonMove.setSelected(true);
		dropdownMode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Mouse.selectedEntities.clear();
				Mouse.selectedTiles.clear();
				labelLevel.update(true);
			}		
		});
		
		JSeparator sep1 = new JSeparator(JSeparator.VERTICAL);
		sep1.setPreferredSize(new Dimension(1, 25));
		JSeparator sep2 = new JSeparator(JSeparator.VERTICAL);
		sep2.setPreferredSize(new Dimension(1, 25));
		
		panelEdit.add(dropdownZoom);
		panelEdit.add(buttonZoomIn);
		panelEdit.add(buttonZoomOut);
		panelEdit.add(buttonRefresh);
		panelEdit.add(sep1);
		panelEdit.add(buttonMove);
		panelEdit.add(buttonEdit);		
		panelEdit.add(buttonPaint);
		panelEdit.add(sep2);
		panelEdit.add(dropdownMode);
		
		panelRender.setLayout(null);
		panelRender.setBackground(Color.white);
		panelRender.add(labelLevel);
		panelRender.addMouseMotionListener(mouse);
		panelRender.addMouseWheelListener(mouse);
		panelRender.addMouseListener(mouse);
		panelRender.setFocusable(true);
		panelRender.addKeyListener(Key.keyListener);
		
		GridBagConstraints panelEastConstraints = new GridBagConstraints();
		panelEastConstraints.gridx = 0;
		panelEastConstraints.gridy = 0;
		panelEastConstraints.weightx = 1;		
		panelEastConstraints.anchor = GridBagConstraints.WEST;
		
		panelEast.setLayout(new GridBagLayout());
		panelEast.add(panelEdit, panelEastConstraints);
		
		panelEastConstraints.gridy = 1;
		panelEastConstraints.weighty = 1;
		panelEastConstraints.fill = GridBagConstraints.BOTH;
		panelEast.add(panelRender, panelEastConstraints);
		
		for(Tile t : LevelRegistry.tileRegistry.values())
		{
			dropdownTiles.addItem(t.id + ": " + t.getClass().getSimpleName());
		}
		
		for(Entity e : TableListener.entities.values())
		{
			dropdownEntities.addItem(e.id + ": " + e.getClass().getSimpleName());
		}
		
		dropdownTiles.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				@SuppressWarnings("unchecked")
				int id = Integer.parseInt(((JComboBox<String>)arg0.getSource()).getSelectedItem().toString().split(":")[0]);
				
				TableListener.setTile(LevelRegistry.tileRegistry.get(id), TableListener.tiles.get(id));
			}		
		});
		
		dropdownEntities.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				@SuppressWarnings("unchecked")
				int id = Integer.parseInt(((JComboBox<String>)arg0.getSource()).getSelectedItem().toString().split(":")[0]);
				
				TableListener.setEntity(TableListener.entities.get(id));
			}		
		});
		
		panelLevel.setLayout(new GridBagLayout());
		panelTiles.setLayout(new GridBagLayout());
		panelEntities.setLayout(new GridBagLayout());
		
		panelLevel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelTiles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelEntities.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints panelLevelConstraints = new GridBagConstraints();
		GridBagConstraints panelTilesConstraints = new GridBagConstraints();
		GridBagConstraints panelEntitiesConstraints = new GridBagConstraints();
		
		//Panel Tiles
		
		panelTilesConstraints.gridx = 0;
		panelTilesConstraints.gridy = 0;
		panelTilesConstraints.weightx = 1;		
		panelTilesConstraints.anchor = GridBagConstraints.WEST;	
		panelTilesConstraints.fill = GridBagConstraints.BOTH;
		
		panelTiles.add(dropdownTiles, panelTilesConstraints);
		
		panelTilesConstraints.gridy = 1;
		labelTiles.setFont(labelTiles.getFont().deriveFont(Font.ITALIC));
		labelTiles.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
		panelTiles.add(labelTiles, panelTilesConstraints);
		
		panelTilesConstraints.gridy = 2;		
		JLabel lb1 = new JLabel("Tile Attributes:");
		lb1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
		panelTiles.add(lb1, panelTilesConstraints);
		
		panelTilesConstraints.gridy = 3;
		panelTilesConstraints.weighty = 1;	
		
		tableTiles.setRowHeight(20);
		tableTiles.getTableHeader().setReorderingAllowed(false);
		tableTiles.getModel().addTableModelListener(new TableListener());
		tableTiles.setColumnSelectionAllowed(true);
		
		JScrollPane sp1 = new JScrollPane(tableTiles);
		sp1.setPreferredSize(new Dimension(200, 0));
		panelTiles.add(sp1, panelTilesConstraints);
		
		//Panel Entities
		
		panelEntitiesConstraints.gridx = 0;
		panelEntitiesConstraints.gridy = 0;
		panelEntitiesConstraints.weightx = 1;		
		panelEntitiesConstraints.anchor = GridBagConstraints.WEST;	
		panelEntitiesConstraints.fill = GridBagConstraints.BOTH;
		
		panelEntities.add(dropdownEntities, panelEntitiesConstraints);
		
		panelEntitiesConstraints.gridy = 1;		
		JLabel lb2 = new JLabel("Entity Attributes:");
		lb2.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
		panelEntities.add(lb2, panelEntitiesConstraints);
		
		panelEntitiesConstraints.gridy = 2;
		panelEntitiesConstraints.weighty = 1;	
		
		tableEntities.setRowHeight(20);
		tableEntities.getTableHeader().setReorderingAllowed(false);
		tableEntities.getModel().addTableModelListener(new TableListener());
		tableEntities.setColumnSelectionAllowed(true);
		
		JScrollPane sp2 = new JScrollPane(tableEntities);
		sp2.setPreferredSize(new Dimension(200, 0));
		panelEntities.add(sp2, panelEntitiesConstraints);		
		
		//Panel Level
		
		panelLevelConstraints.gridx = 0;
		panelLevelConstraints.gridy = 0;
		panelLevelConstraints.weightx = 1;		
		panelLevelConstraints.anchor = GridBagConstraints.WEST;	
		
		JLabel lb3 = new JLabel("Level Attributes:");
		lb3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
		panelLevel.add(lb3, panelLevelConstraints);
		
		panelLevelConstraints.gridy = 1;
		panelLevelConstraints.weighty = 1;
		panelLevelConstraints.fill = GridBagConstraints.BOTH;
		
		tableLevel.setRowHeight(20);
		tableLevel.getTableHeader().setReorderingAllowed(false);
		tableLevel.getModel().addTableModelListener(new TableListener());
		tableLevel.setColumnSelectionAllowed(true);
		
		JScrollPane sp3 = new JScrollPane(tableLevel);
		sp3.setPreferredSize(new Dimension(200, 0));
		panelLevel.add(sp3, panelLevelConstraints);
		
		tabpanelEditor.addTab("Level", panelLevel);
		tabpanelEditor.addTab("Tiles", panelTiles);
		tabpanelEditor.addTab("Entities", panelEntities);	
		
		panelMain.add(tabpanelEditor, JSplitPane.LEFT);
		panelMain.add(panelEast, JSplitPane.RIGHT);		
		
		frame.add(menubar, BorderLayout.NORTH);
		frame.add(panelMain);
		frame.setVisible(true);
	}
	
	public static boolean isInternal = false;
	
	public static void main(String[] args)
	{
		if(args.length > 0)
		{
			if(args[0].equals("internal")) isInternal = true; 
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RenderRegistry.bufferImages();
		RenderRegistry.setup();
		
		editor = new Editor();
		
		TableListener.setTile(LevelRegistry.tileRegistry.values().iterator().next(), 0);
		TableListener.setEntity(LevelRegistry.entityRegistry.values().iterator().next());
		
		int choose = JOptionPane.showOptionDialog(null, "Welcome to the Red Fire Level Editor!\nBelow are some options you can choose from.\nPress F1 for help.", "Hello", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"New", "Open", "Help", "Cancel"}, 0);
		
		if(choose == 0)
		{
			Editor.createNewLevel();
		}
		if(choose == 1)
		{
			Editor.openLevel();
		}
		if(choose == 2)
		{
			Editor.displayHelp();
		}
	}
	
	public static void displayHelp()
	{
		
	}

	public static void runScript(String className, String filePath)
	{
		File f = new File(filePath);
		
		try {
			URLClassLoader cl = new URLClassLoader(new URL[]{f.toURI().toURL()});
			
			@SuppressWarnings("unchecked")
			Class<Script> cls = (Class<Script>) cl.loadClass("vic.rpg.editor.script." + className);
			Script script = cls.newInstance();
			
			script.run(editor.level);
			editor.labelLevel.update(false);
			
			cl.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, f.getPath() + " couldn't be run!\nThe File is not valid", "Run...", JOptionPane.ERROR_MESSAGE);	            
		}
	}

	public static void createNewLevel()
	{
		boolean quit = false;
		
		JTextField name = new JTextField("New Level");
		JTextField width = new JTextField("100");
		JTextField height = new JTextField("100");
		JTextField data = new JTextField("0"); 

		JComboBox<String> tiles = new JComboBox<String>();
		
		for(Tile t : LevelRegistry.tileRegistry.values())
		{
			tiles.addItem(t.id + ": " + t.getClass().getSimpleName());
		}
		
		JPanel multiPanel = new JPanel();
		multiPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints con = new GridBagConstraints();
		con.anchor = GridBagConstraints.WEST;
		con.weightx = 1;
		con.weighty = 1;
		con.gridwidth = 3;
		con.fill = GridBagConstraints.BOTH;
		
		JLabel title = new JLabel("Set Level Options:");
		title.setFont(title.getFont().deriveFont(Font.BOLD));
		
		multiPanel.add(title, con);
		
		con.gridy = 1;
		multiPanel.add(new JSeparator(), con);
		JLabel j1 = new JLabel("Name:");
		j1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		con.gridy = 2;
		multiPanel.add(j1, con);
		con.gridy = 3;
	  	multiPanel.add(name, con);
	  	
	  	JLabel j2 = new JLabel("Width:");
	  	j2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	  	con.gridy = 4;
	  	multiPanel.add(j2, con);
	  	con.gridy = 5;
	  	multiPanel.add(width, con);
	  	
	  	JLabel j3 = new JLabel("Height:"); 
	  	j3.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	  	con.gridy = 6;
	  	multiPanel.add(j3, con);
	  	con.gridy = 7;     	
	  	multiPanel.add(height, con);    	
	  	
	  	JLabel j4 = new JLabel("Fill material:");
	  	j4.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	  	con.gridy = 8;
	  	multiPanel.add(j4, con);
	  	con.gridy = 9;   	
	  	con.gridwidth = 1;
	  	multiPanel.add(tiles, con);
	  	 
	  	JLabel j5 = new JLabel("Data:");
	  	j5.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	  	con.weightx = 0;
	  	con.gridx = 1;
	  	multiPanel.add(j5, con);
		con.gridx = 2;
		multiPanel.add(data, con);
		
		while(!quit)
		{
			int result = JOptionPane.showConfirmDialog(null, multiPanel, "New... ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			
			int id = Integer.parseInt(((String)(tiles.getSelectedItem())).split(":")[0]);
			
			if(result != JOptionPane.OK_OPTION)
			{
				quit = true;
			}
			else
			{
				if(name.getText().length() < 1 || width.getText().length() < 1 || width.getText().length() < 1)
				{
					JOptionPane.showMessageDialog(null, "At least one value is not given", "New...", JOptionPane.ERROR_MESSAGE); 
				}
				else
				{
					try
					{							
						Level level = new Level(Integer.parseInt(width.getText()), Integer.parseInt(width.getText()), name.getText());
						level.fill(id, Integer.parseInt(data.getText()));
						editor.labelLevel.setLevel(level);
						editor.setLevelName(editor.level.name);
		            	JOptionPane.showMessageDialog(null, "Level \"" + editor.level.name + "\" was sucsessfully created", "New...", JOptionPane.INFORMATION_MESSAGE);  
		            	quit = true;
		            	
					} catch(NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "Width, Height or Data not numeric", "New...", JOptionPane.ERROR_MESSAGE); 
					} catch(IllegalArgumentException e2) {
						JOptionPane.showMessageDialog(null, e2.getMessage(), "New...", JOptionPane.ERROR_MESSAGE); 
					}
				}
			}
		}
	}

	public static void openLevel()
	{
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter plainFilter = new FileNameExtensionFilter( 
	            "Plaintext: lvl", "lvl"); 
	    chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter()); 
	    chooser.setFileFilter(plainFilter); 
	    chooser.setDialogTitle("Open...");
	    chooser.setVisible(true); 
	
	    int result = chooser.showOpenDialog(editor.frame);
	    
	    if (result == JFileChooser.APPROVE_OPTION) { 
	
	        String path = chooser.getSelectedFile().toString(); 
	        File file = new File(path); 
	        if (plainFilter.accept(file))
	        {
	            System.out.println(file + " selected for loading"); 
	            try{
	            	editor.labelLevel.setLevel(Level.readFromFile(file));       
	            	editor.setLevelName(editor.level.name);
	            	JOptionPane.showMessageDialog(null, "Level \"" + editor.level.name + "\" was sucsessfully loaded from " + path, "Open...", JOptionPane.INFORMATION_MESSAGE);
	        		ButtonListener.file = file;
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	JOptionPane.showMessageDialog(null, "Level could'nt be loaded from " + path + "\nReason: " + e.getClass().getSimpleName() + "\nat " + e.getStackTrace()[0], "Open...", JOptionPane.ERROR_MESSAGE);	
	            }
	        }
	        else
	        {
	        	System.out.println(path + " is not valid for loading");
	        	JOptionPane.showMessageDialog(null, "No level could be loaded from " + path + "\nMaybe the file is missing or currupted", "Open...", JOptionPane.ERROR_MESSAGE);
	        }                
	        chooser.setVisible(false);            
	    }
	}
	
	public static void saveLevel()
	{
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter plainFilter = new FileNameExtensionFilter( 
                "Plaintext: lvl", "lvl"); 
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter()); 
        chooser.setFileFilter(plainFilter); 
        chooser.setDialogTitle("Save as..."); 
        chooser.setVisible(true); 

        int result = chooser.showSaveDialog(Editor.editor.frame);
        
        if (result == JFileChooser.APPROVE_OPTION) { 

            String path = chooser.getSelectedFile().toString(); 
            File file = new File(path); 
            if (plainFilter.accept(file))
            {
                System.out.println(file + " selected for saving"); 
            	Editor.editor.level.writeToFile(file);
            	JOptionPane.showMessageDialog(null, "Level \"" + Editor.editor.level.name + "\" was saved to " + path, "Save as...", JOptionPane.INFORMATION_MESSAGE);
            	ButtonListener.file = file;
            }
            else
            {
            	System.out.println(path + " is not valid for saving");
            	JOptionPane.showMessageDialog(null, "Level \"" + Editor.editor.level.name + "\" couldn't be saved to " + path + "\nMaybe the file is missing or currupted", "Save as...", JOptionPane.ERROR_MESSAGE);
            }                
            chooser.setVisible(false);            
        } 
	}
	
	public void setLevelName(String name)
	{
		if(name.length() > 0)
		{
			frame.setTitle("Red Fire Level Editor (" + name + ")");
		}
		else frame.setTitle("Red Fire Level Editor");
	}
}
