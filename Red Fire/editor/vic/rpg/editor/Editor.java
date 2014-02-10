package vic.rpg.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import vic.rpg.Game;
import vic.rpg.editor.gui.DockableDesktopManager;
import vic.rpg.editor.gui.JDockableFrame;
import vic.rpg.editor.gui.PanelLevel;
import vic.rpg.editor.gui.TileTextureSelector;
import vic.rpg.editor.listener.ButtonListener;
import vic.rpg.editor.listener.Key;
import vic.rpg.editor.listener.LayerFrameListener;
import vic.rpg.editor.listener.Mouse;
import vic.rpg.editor.listener.TableListener;
import vic.rpg.editor.listener.ZoomListener;
import vic.rpg.editor.script.Script;
import vic.rpg.editor.tiles.TileMaterial;
import vic.rpg.level.Entity;
import vic.rpg.level.Level;
import vic.rpg.level.TexturePath;
import vic.rpg.level.Tile;
import vic.rpg.level.entity.EntityCustom;
import vic.rpg.registry.LevelRegistry;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.utils.Utils;

public class Editor 
{
	public static Editor instance;
	public static int layerID = 0;
	public static BufferedImage NO_TEXTURE = Utils.readImageFromJar("/vic/rpg/resources/editor/no_texture.png");
	
	public EntityEditor entityEditor = new EntityEditor();
	
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
	public JMenu run          = new JMenu("Run...");	
	public JMenuItem copy     = new JMenuItem("Copy");
	public JMenuItem paste    = new JMenuItem("Paste");
	public JMenuItem delete   = new JMenuItem("Delete");
	
	public JSplitPane panelMain = new JSplitPane();
	
	public JPanel panelEast   = new JPanel(); 
	public JPanel panelEdit   = new JPanel();
	
	public PanelLevel labelLevel;
	
	public JComboBox<String> dropdownZoom = new JComboBox<String>(new String[]{"500%", "400%", "300%", "200%", "100%", "66%", "50%", "33%", "25%", "16%", "10%"});
	public JButton buttonZoomIn      = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/zoom-in.png")));
	public JButton buttonZoomOut     = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/zoom-out.png")));
	public JButton buttonRefresh     = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/repeat-2.png")));
	public JToggleButton buttonMove  = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/move.png")));
	public JToggleButton buttonEdit  = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/swap.png")));
	public JToggleButton buttonPaint = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/pencil.png")));
	public JToggleButton buttonErase = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/rubber.png")));
	public JToggleButton buttonPath  = new JToggleButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/pathfinding.png")));
	
	public JTabbedPane tabpanelEditor = new JTabbedPane();
	public JPanel panelTiles = new JPanel();
	public JPanel panelEntities = new JPanel();
	public JPanel panelLevel = new JPanel();
	
	public JComboBox<String> dropdownTiles = new JComboBox<String>();
	public JComboBox<String> dropdownEntities = new JComboBox<String>();
	
	public JButton buttonNewEntity = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/add.png")));
	public JButton buttonEditEntity = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/swap.png")));
	public JButton buttonDeleteEntity = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/remove.png")));
	
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
	public TileTextureSelector selectTileTexture = new TileTextureSelector("/vic/rpg/resources/editor/no_texture.png");
	public JScrollPane tableTilesScrollPane = new JScrollPane(tableTiles);
	public JScrollPane selectTileTextureScrollPane = new JScrollPane(selectTileTexture);
	
	public JDesktopPane desktop;
	
	//Layer Frame
	public JDockableFrame frameLayers;
	public JTable tableLayers = new JTable();
	public JButton buttonNewLayer = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/add.png")));
	public JButton buttonRemoveLayer = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/remove.png")));
	
	/*
	//Brush Frame
	public JDockableFrame frameBrush;
	public JSlider sliderBrushSize = new JSlider(SwingConstants.VERTICAL, 0, 20, 1);
	public PanelTexture panelTexture1 = new PanelTexture(NO_TEXTURE);
	public PanelTexture panelTexture2 = new PanelTexture(NO_TEXTURE);
	public JButton buttonEditBrush = new JButton(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/swap.png")));
	*/
	public Level level;
	
	public Editor()
	{				
		Game.GL_PROFILE = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(Game.GL_PROFILE);
        labelLevel = new PanelLevel(glcapabilities);
		
		frame = new JFrame();	
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			
			@Override public void windowOpened(WindowEvent e) {}		
			@Override public void windowIconified(WindowEvent e) {}		
			@Override public void windowDeiconified(WindowEvent e) {}		
			@Override public void windowDeactivated(WindowEvent e) {}
			
			@Override 
			public void windowClosing(WindowEvent e) 
			{
				TileMaterial.saveMaterials();
				System.exit(0);
			}
			
			@Override public void windowClosed(WindowEvent e) {}	
			@Override public void windowActivated(WindowEvent e) {}
		});

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
		run.addActionListener(ButtonListener.listener);
		copy.addActionListener(ButtonListener.listener);
		paste.addActionListener(ButtonListener.listener);
		delete.addActionListener(ButtonListener.listener);
		
		buttonNewEntity.addActionListener(ButtonListener.listener);
		buttonNewEntity.setPreferredSize(new Dimension(25, 25));
		buttonEditEntity.addActionListener(ButtonListener.listener);
		buttonEditEntity.setPreferredSize(new Dimension(25, 25));
		buttonEditEntity.setEnabled(false);
		buttonDeleteEntity.addActionListener(ButtonListener.listener);
		buttonDeleteEntity.setPreferredSize(new Dimension(25, 25));
		buttonDeleteEntity.setEnabled(false);
		
		menuFile.add(open);
		menuFile.add(newLevel);
		menuFile.add(save);
		menuFile.add(saveas);
		menuFile.addSeparator();
		menuFile.add(exit);
		
		menuEdit.add(undo);
		menuEdit.add(redo);
		menuEdit.addSeparator();
		menuEdit.add(run);
		menuEdit.addSeparator();
		menuEdit.add(copy);
		menuEdit.add(paste);
		menuEdit.add(delete);
		
		menubar.add(menuFile);
		menubar.add(menuEdit);		
		
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
		buttonErase.setPreferredSize(new Dimension(25, 25));
		buttonErase.addActionListener(ButtonListener.listener);
		buttonMove.setPreferredSize(new Dimension(25, 25));
		buttonMove.addActionListener(ButtonListener.listener);
		buttonPaint.setPreferredSize(new Dimension(25, 25));
		buttonPaint.addActionListener(ButtonListener.listener);
		buttonPath.setPreferredSize(new Dimension(25, 25));
		buttonPath.addActionListener(ButtonListener.listener);
		
		buttonMove.setSelected(true);
		tabpanelEditor.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				Mouse.selectedEntities.clear();
				Mouse.selectedTiles.clear();
				labelLevel.updateUI();
			}		
		});
		
		JSeparator sep1 = new JSeparator(JSeparator.VERTICAL);
		sep1.setPreferredSize(new Dimension(1, 25));
		
		panelEdit.add(dropdownZoom);
		panelEdit.add(buttonZoomIn);
		panelEdit.add(buttonZoomOut);
		panelEdit.add(buttonRefresh);
		panelEdit.add(sep1);
		panelEdit.add(buttonMove);
		panelEdit.add(buttonEdit);
		panelEdit.add(buttonErase);
		panelEdit.add(buttonPaint);
		panelEdit.add(buttonPath);
		
		labelLevel.setBackground(Color.white);
		labelLevel.addMouseMotionListener(mouse);
		labelLevel.addMouseWheelListener(mouse);
		labelLevel.addMouseListener(mouse);
		labelLevel.setFocusable(true);
		labelLevel.addKeyListener(Key.keyListener);
		labelLevel.setLayout(new BorderLayout());
		
		desktop = new JDesktopPane();
		desktop.setOpaque(false);
		DockableDesktopManager desktopManager = new DockableDesktopManager();
		desktop.setDesktopManager(desktopManager);
		desktop.addComponentListener(desktopManager);
		
		//Layer Frame
		frameLayers = new JDockableFrame("Layers", true, false);
		frameLayers.setFrameIcon(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/windows.png")));
		frameLayers.getContentPane().setLayout(new BorderLayout());
		tableLayers.setModel(new DefaultTableModel(0, 2)
		{
			@Override
			public boolean isCellEditable(int row, int column) 
			{
				return column != 0;
			}

			@Override
			public Class<?> getColumnClass(int arg0) 
			{
				return arg0 == 1 ? Boolean.class : super.getColumnClass(arg0);
			}	
		});
		tableLayers.setTableHeader(null);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tableLayers.getModel().addTableModelListener(LayerFrameListener.instance);
		tableLayers.setDefaultRenderer(Object.class, centerRenderer);	
		tableLayers.setRowHeight(30);
		tableLayers.setFont(tableLayers.getFont().deriveFont(16f));
		tableLayers.getSelectionModel().addListSelectionListener(LayerFrameListener.instance);
		JScrollPane scroll1 = new JScrollPane(tableLayers);
		scroll1.setPreferredSize(new Dimension(150, 150));
		frameLayers.getContentPane().add(scroll1, BorderLayout.CENTER);
		
		buttonNewLayer.setPreferredSize(new Dimension(25, 25));
		buttonRemoveLayer.setPreferredSize(new Dimension(25, 25));
		buttonNewLayer.addActionListener(LayerFrameListener.instance);
		buttonRemoveLayer.addActionListener(LayerFrameListener.instance);
		
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout());
		p1.add(buttonNewLayer);
		p1.add(buttonRemoveLayer);
		frameLayers.add(p1, BorderLayout.SOUTH);
		
		desktop.add(frameLayers);
		
		/*
		//Brush Frame
		frameBrush = new JDockableFrame("Brush Settings", true, false);
		frameBrush.setFrameIcon(new ImageIcon(Utils.readImageFromJar("/vic/rpg/resources/editor/windows.png")));
		frameBrush.setSize(150, 200);
		frameBrush.setLocation(0, desktop.getHeight() - frameLayers.getHeight());
		frameBrush.addDock(JDockableFrame.WEST);
		frameBrush.addDock(JDockableFrame.SOUTH);
		frameBrush.setLayout(new GridBagLayout());
		GridBagConstraints frameBrushConstraints = new GridBagConstraints();
		
		JPanel p2 = new JPanel(new GridBagLayout());
		p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints p2Constraints = new GridBagConstraints();
		p2Constraints.fill = GridBagConstraints.BOTH;
		p2Constraints.weightx = 1;
		p2Constraints.weighty = 1;
		panelTexture1.setFocusable(true);
		panelTexture1.addMouseListener(BrushFrameListener.instance);
		p2.add(panelTexture1, p2Constraints);
		p2Constraints.gridy = 1;
		panelTexture2.setFocusable(true);
		panelTexture2.addMouseListener(BrushFrameListener.instance);
		p2.add(panelTexture2, p2Constraints);
		
		sliderBrushSize.setPaintTicks(true);
		sliderBrushSize.setMinorTickSpacing(1);
		sliderBrushSize.setMajorTickSpacing(5);
		sliderBrushSize.setSnapToTicks(true);
		sliderBrushSize.setPaintLabels(true);
		
		JPanel p3 = new JPanel(new GridBagLayout());
		p3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
		GridBagConstraints p3Constraints = new GridBagConstraints();
		p3Constraints.fill = GridBagConstraints.VERTICAL;
		p3Constraints.weightx = 1;
		p3Constraints.weighty = 1;
		p3.add(sliderBrushSize, p3Constraints);
		p3Constraints.weighty = 0;
		p3Constraints.gridy = 1;
		buttonEditBrush.setPreferredSize(new Dimension(25, 25));
		buttonEditBrush.addActionListener(BrushFrameListener.instance);
		p3.add(buttonEditBrush, p3Constraints);
		
		frameBrushConstraints.fill = GridBagConstraints.BOTH;
		frameBrushConstraints.weightx = 0;
		frameBrushConstraints.weighty = 1;
		frameBrush.add(p3, frameBrushConstraints);
		frameBrushConstraints.weightx = 1;
		frameBrushConstraints.gridx = 1;
		frameBrush.add(p2, frameBrushConstraints);
		
		desktop.add(frameBrush);
		*/
		labelLevel.add(desktop, BorderLayout.CENTER);
		
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
		panelEast.add(labelLevel, panelEastConstraints);
		
		updateTilesAndEntites();
		
		dropdownTiles.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED)
				{
					@SuppressWarnings("unchecked")
					int id = Integer.parseInt(((JComboBox<String>)arg0.getSource()).getSelectedItem().toString().split(":")[0]);
					
					TableListener.setTile(LevelRegistry.tileRegistry.get(id), TableListener.tiles.get(id));
				}
			}		
		});
		
		dropdownEntities.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED)
				{
					@SuppressWarnings("unchecked")
					int id = Integer.parseInt(((JComboBox<String>)arg0.getSource()).getSelectedItem().toString().split(":")[0]);
					if(TableListener.entities.get(id) instanceof EntityCustom)
					{
						buttonEditEntity.setEnabled(true);
						buttonDeleteEntity.setEnabled(true);
					}
					else
					{
						buttonEditEntity.setEnabled(false);
						buttonDeleteEntity.setEnabled(false);
					}
					
					TableListener.setEntity(TableListener.entities.get(id));
				}
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
		
		tableTilesScrollPane.setPreferredSize(new Dimension(200, 0));
		panelTiles.add(tableTilesScrollPane, panelTilesConstraints);
		
		panelTilesConstraints.gridy = 4;
		
		selectTileTexture.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) 
			{
				tableTiles.setValueAt(selectTileTexture.getSelectedTexture(), 0, 2);
			}
			
			@Override public void mousePressed(MouseEvent e) {}	
			@Override public void mouseExited(MouseEvent e) {}	
			@Override public void mouseEntered(MouseEvent e) {}	
			@Override public void mouseClicked(MouseEvent e) {}
		});
		
		selectTileTextureScrollPane.setBorder(null);
		selectTileTextureScrollPane.setVisible(false);
		panelTiles.add(selectTileTextureScrollPane, panelTilesConstraints);
		
		//Panel Entities
		
		panelEntitiesConstraints.gridx = 0;
		panelEntitiesConstraints.gridy = 0;
		panelEntitiesConstraints.weightx = 1;		
		panelEntitiesConstraints.anchor = GridBagConstraints.WEST;	
		panelEntitiesConstraints.fill = GridBagConstraints.BOTH;	
		panelEntities.add(dropdownEntities, panelEntitiesConstraints);
		
		panelEntitiesConstraints.gridx = 1;
		panelEntitiesConstraints.weightx = 0;	
		panelEntities.add(buttonEditEntity, panelEntitiesConstraints);
		
		panelEntitiesConstraints.gridx = 2;
		panelEntities.add(buttonDeleteEntity, panelEntitiesConstraints);
		
		panelEntitiesConstraints.gridx = 3;
		panelEntities.add(buttonNewEntity, panelEntitiesConstraints);
		
		panelEntitiesConstraints.gridwidth = 4;
		panelEntitiesConstraints.gridx = 0;
		panelEntitiesConstraints.gridy = 1;
		panelEntitiesConstraints.weightx = 1;
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
	
	public void updateTileTextureSelector(Tile t)
	{
		if(t.getClass().getAnnotation(TexturePath.class) != null)
		{
			TexturePath texPath = t.getClass().getAnnotation(TexturePath.class);
			tableTilesScrollPane.setVisible(false);
			selectTileTextureScrollPane.setVisible(true);
			selectTileTexture.setImagePath(texPath.path());
		}
		else
		{
			selectTileTextureScrollPane.setVisible(false);
			tableTilesScrollPane.setVisible(true);
		}
	}
	
	public void updateTilesAndEntites() 
	{
		dropdownTiles.removeAllItems();
		dropdownEntities.removeAllItems();
		
		for(Tile t : LevelRegistry.tileRegistry.values())
		{
			dropdownTiles.addItem(t.id + ": " + t.getClass().getSimpleName());
		}
		
		for(Entity e : TableListener.entities.values())
		{
			dropdownEntities.addItem(e.id + ": " + e.getClass().getSimpleName());
		}
	}
	
	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RenderRegistry.bufferImages();
		RenderRegistry.setup();
		Game.init();
		
		for(Integer i : LevelRegistry.entityRegistry.keySet())
		{
			TableListener.entities.put(i, LevelRegistry.entityRegistry.get(i).clone());
		}
		for(Integer i : LevelRegistry.tileRegistry.keySet())
		{
			TableListener.tiles.put(i, LevelRegistry.tileRegistry.get(i).data);
		}
		
		instance = new Editor();
		
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
		TileMaterial.loadMaterials();
	}
	
	private static boolean firstTimeLayer = true;
	public static void updateLayerFrame()
	{
		instance.frameLayers.setVisible(false);
		LayerFrameListener.updateLayers();
		if(firstTimeLayer)
		{
			instance.frameLayers.pack();
			instance.frameLayers.setLocation(instance.desktop.getWidth() - instance.frameLayers.getWidth(), instance.desktop.getHeight() - instance.frameLayers.getHeight());
			instance.frameLayers.addDock(JDockableFrame.EAST);
			instance.frameLayers.addDock(JDockableFrame.SOUTH);
		}
		instance.tableLayers.setRowSelectionInterval(0, 0);
		instance.frameLayers.setVisible(true);
		firstTimeLayer = false;
	}

	/**
	 * Loads a given jar file and runs the method "run" located in the class named the same as
	 * the jar file and which is located in vic.rpg.script.
	 * @param className
	 * @param filePath
	 */
	public static void runScript(String className, String filePath)
	{
		File f = new File(filePath);
		
		try {
			URLClassLoader cl = new URLClassLoader(new URL[]{f.toURI().toURL()});
			
			@SuppressWarnings("unchecked")
			Class<Script> cls = (Class<Script>) cl.loadClass("vic.rpg.editor.script." + className);
			Script script = cls.newInstance();
			
			script.run(instance.level);
			instance.labelLevel.updateUI();
			
			cl.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, f.getPath() + " couldn't be run!\nThe File is not valid", "Run...", JOptionPane.ERROR_MESSAGE);	            
		}
	}

	/**
	 * Opens the "New Level" - dialogue.
	 */
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
		data.setPreferredSize(new Dimension(25, 0));
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
						level.fill(id, Integer.parseInt(data.getText()), 0);
						instance.labelLevel.setLevel(level);
						updateLayerFrame();
						instance.setLevelName(instance.level.name);
		            	JOptionPane.showMessageDialog(null, "Level \"" + instance.level.name + "\" was sucsessfully created", "New...", JOptionPane.INFORMATION_MESSAGE);  
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

	/**
	 * Opens the "Open Level" - dialogue.
	 */
	public static void openLevel()
	{
		JFileChooser chooser = new JFileChooser(Utils.getAppdata() + "/saves");
		FileNameExtensionFilter plainFilter = new FileNameExtensionFilter("Plaintext: lvl", "lvl"); 
		chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter()); 
		chooser.setFileFilter(plainFilter); 
		chooser.setDialogTitle("Open...");
		chooser.setVisible(true); 

		int result = chooser.showOpenDialog(instance.frame);

		if(result == JFileChooser.APPROVE_OPTION) 
		{
			String path = chooser.getSelectedFile().toString(); 
	        File file = new File(path); 
	        if (plainFilter.accept(file))
	        {
	        	System.out.println(file + " selected for loading"); 
	            try{
	            	instance.labelLevel.setLevel(Level.readFromFile(file));       
	            	instance.setLevelName(instance.level.name);
	            	updateLayerFrame();
	            	JOptionPane.showMessageDialog(null, "Level \"" + instance.level.name + "\" was sucsessfully loaded from " + path, "Open...", JOptionPane.INFORMATION_MESSAGE);
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
	
	/**
	 * Opens the "Save Level" - dialogue.
	 */
	public static void saveLevel()
	{
		JFileChooser chooser = new JFileChooser(Utils.getAppdata() + "/saves");
		FileNameExtensionFilter plainFilter = new FileNameExtensionFilter( 
                "Plaintext: lvl", "lvl"); 
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter()); 
        chooser.setFileFilter(plainFilter); 
        chooser.setDialogTitle("Save as..."); 
        chooser.setVisible(true); 

        int result = chooser.showSaveDialog(Editor.instance.frame);
        
        if (result == JFileChooser.APPROVE_OPTION) { 

            String path = chooser.getSelectedFile().toString(); 
            File file = new File(path); 
            if (plainFilter.accept(file))
            {
                System.out.println(file + " selected for saving"); 
            	Editor.instance.level.writeToFile(file);
            	JOptionPane.showMessageDialog(null, "Level \"" + Editor.instance.level.name + "\" was saved to " + path, "Save as...", JOptionPane.INFORMATION_MESSAGE);
            	ButtonListener.file = file;
            }
            else
            {
            	System.out.println(path + " is not valid for saving");
            	JOptionPane.showMessageDialog(null, "Level \"" + Editor.instance.level.name + "\" couldn't be saved to " + path + "\nMaybe the file is missing or currupted", "Save as...", JOptionPane.ERROR_MESSAGE);
            }                
            chooser.setVisible(false);            
        } 
	}
	
	/**
	 * Used to format the title of the editor window given the name of a level.
	 * @param name
	 */
	public void setLevelName(String name)
	{
		if(name.length() > 0)
		{
			frame.setTitle("Red Fire Level Editor (" + name + ")");
		}
		else frame.setTitle("Red Fire Level Editor");
	}
}
