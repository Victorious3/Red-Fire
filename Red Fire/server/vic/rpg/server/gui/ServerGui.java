package vic.rpg.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import vic.rpg.registry.GameRegistry;
import vic.rpg.server.Server;
import vic.rpg.server.io.Connection;

public class ServerGui 
{
	public static JFrame frame;
	public static JTextPane textArea; 
	public static JScrollPane scrollPane;
	public static JTextField textField;
	public static JPanel players;
	public static JTable tablePlayers;
	
	public static boolean returnPressed = false;
	
	private static final LinkedBlockingQueue<Character> sb = new LinkedBlockingQueue<Character>();
	
	public static void setup()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		frame = new JFrame();
		textArea = new JTextPane();
		scrollPane = new JScrollPane(textArea);
		textField = new JTextField();		
		textArea.setEditable(false);
		
		frame.setLayout(new GridBagLayout());
		GridBagConstraints grid = new GridBagConstraints();
		grid.gridx = 0;
		grid.gridy = 0;
		grid.weightx = 1;
		grid.weighty = 1;
		grid.gridheight = 2;
		grid.fill = GridBagConstraints.BOTH;
		frame.add(scrollPane, grid);
		grid.gridheight = 1;
		grid.gridy = 2;
		grid.weighty = 0;
		frame.add(textField, grid);
		grid.gridx = 1;
		grid.gridy = 0;
		grid.weightx = 0;
		grid.weighty = 0;
		JPanel p1 = new JPanel();
		p1.setBorder(BorderFactory.createTitledBorder("Statistics"));
		StatisticPanel.instance.setPreferredSize(new Dimension(300, 110));
		StatisticPanel.instance.setMinimumSize(new Dimension(300, 110));
		p1.add(StatisticPanel.instance);
		frame.add(p1, grid);
		grid.gridx = 1;
		grid.gridy = 1;
		grid.weightx = 0;
		grid.weighty = 1;
		grid.gridheight = 2;
		
		players = new JPanel(new BorderLayout());
		players.setBorder(BorderFactory.createTitledBorder("Players (" + Server.actConnections + "/" + Server.MAX_CONNECTIONS + ")"));

		tablePlayers = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"username", "ip", "in", "out"}))
		{
			@Override
			public Dimension getPreferredScrollableViewportSize() 
			{
				Dimension dim = super.getPreferredSize();
				dim.width = 300;
				return dim;
			}

			@Override
			public boolean isCellEditable(int row, int column) 
			{
				return false;
			}				
		};
		updatePlayers();
		
		class TablePopupMenu extends JPopupMenu
		{
			public int row;
		}
		
		final TablePopupMenu popup = new TablePopupMenu();
		
		final JMenuItem itemKick = new JMenuItem("kick");
		final JMenuItem itemBan = new JMenuItem("ban");
		final JMenuItem itemPermissions = new JMenuItem("permissions");

		ActionListener popupMenuListener = new ActionListener() 
		{		
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				JMenuItem it = (JMenuItem)arg0.getSource();
				
				if(it == itemKick)
				{
					String username = (String)tablePlayers.getValueAt(popup.row, 0);
					Server.server.inputHandler.handleCommand("kick", Arrays.asList(new String[]{username}), Server.server);
				}
				else if(it == itemBan)
				{
					String username = (String)tablePlayers.getValueAt(popup.row, 0);
					Server.server.inputHandler.handleCommand("ban", Arrays.asList(new String[]{username}), Server.server);
				}
				else if(it == itemPermissions)
				{
					String username = (String)tablePlayers.getValueAt(popup.row, 0);
					changePermissions(username);
				}
			}
		};
		
		itemKick.addActionListener(popupMenuListener);
		itemBan.addActionListener(popupMenuListener);
		itemPermissions.addActionListener(popupMenuListener);
		
		popup.add(itemKick);
		popup.add(itemBan);
		popup.add(itemPermissions);
		
		tablePlayers.addMouseListener(new MouseAdapter() 
		{		
			@Override
			public void mouseReleased(MouseEvent e) 
			{
				int r = tablePlayers.rowAtPoint(e.getPoint());
				if(r >= 0 && r < tablePlayers.getRowCount()) 
				{
					tablePlayers.setRowSelectionInterval(r, r);
				} 
				else 
				{
					tablePlayers.clearSelection();
				}

				int rowindex = tablePlayers.getSelectedRow();
				if (rowindex < 0) return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) 
				{
					popup.row = rowindex;
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		tablePlayers.setRowHeight(20);
		tablePlayers.getTableHeader().setReorderingAllowed(false);
		tablePlayers.setColumnSelectionAllowed(false);
		tablePlayers.setRowSelectionAllowed(false);
		
		JScrollPane sp1 = new JScrollPane(tablePlayers);
		players.add(sp1, BorderLayout.CENTER);
		frame.add(players, grid);
		
		frame.setSize(800, 600);
		frame.addWindowListener(new WindowListener() 
		{
			@Override public void windowActivated(WindowEvent e) {}
			@Override public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) 
			{
				if(Server.server.inputHandler != null) Server.server.inputHandler.handleCommand("stop", null, Server.server);
				else System.exit(0);
			}

			@Override public void windowDeactivated(WindowEvent e) {}
			@Override public void windowDeiconified(WindowEvent e) {}
			@Override public void windowIconified(WindowEvent e) {}
			@Override public void windowOpened(WindowEvent e) {}	      
			
		});
		
		frame.setTitle("-~/RedFire\\~- Server " + GameRegistry.VERSION);

		Server.server.console = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[0])))
		{
			class TheAttributeSet extends SimpleAttributeSet
			{
				TheAttributeSet()
				{
					StyleConstants.setForeground(this, Color.green);
			        StyleConstants.setFontFamily(this, "Helvetica");
			        StyleConstants.setFontSize(this, 16);
				}
			};
			
			AttributeSet set = new TheAttributeSet();
			
			@Override
			public String readLine() throws IOException 
			{
				if(returnPressed)
				{	
					String s = textField.getText();
					try {
						textArea.getDocument().insertString(textArea.getDocument().getLength(), s + "\n", set);
						textArea.setCaretPosition(textArea.getDocument().getLength());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					
					returnPressed = false;
					textField.setText("");
					return s;
				}
				else return "";
			}		
		};
		
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) 
			{
				sb.offer(e.getKeyChar());
			}

			@Override
			public void keyPressed(KeyEvent arg0) {}

			@Override
			public void keyReleased(KeyEvent arg0) 
			{
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{
					returnPressed = true;
				}
			}	  
		});

		System.setOut(new PrintStream(System.out) 
		{
			class TheAttributeSet extends SimpleAttributeSet
			{
				TheAttributeSet()
				{
					StyleConstants.setForeground(this, Color.black);
			        StyleConstants.setFontFamily(this, "Helvetica");
			        StyleConstants.setFontSize(this, 16);
				}
			};
			
			public AttributeSet set = new TheAttributeSet();
			
			@Override
			public void print(boolean b) 
			{
				print(String.valueOf(b));
			}

			@Override
			public void print(char c) 
			{
				print(String.valueOf(c));
			}

			@Override
			public void print(char[] s) 
			{
				print(String.valueOf(s));
			}

			@Override
			public void print(double d) 
			{
				print(String.valueOf(d));
			}

			@Override
			public void print(float f) 
			{
				print(String.valueOf(f));
			}

			@Override
			public void print(int i) 
			{
				print(String.valueOf(i));
			}

			@Override
			public void print(long l) 
			{
				print(String.valueOf(l));
			}

			@Override
			public void print(Object obj) 
			{
				print(obj.toString());
			}

			public void print(final String s) 
		    {
				try {
					textArea.getDocument().insertString(textArea.getDocument().getLength(), s + "\n", set);
					textArea.setCaretPosition(textArea.getDocument().getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		        super.print(s);
		    }			
		});
		
		System.setErr(new PrintStream(System.err) 
		{
			class TheAttributeSet extends SimpleAttributeSet
			{
				TheAttributeSet()
				{
					StyleConstants.setForeground(this, Color.red);
			        StyleConstants.setFontFamily(this, "Helvetica");
			        StyleConstants.setFontSize(this, 16);
				}
			};
			
			public AttributeSet set = new TheAttributeSet();
			
			@Override
			public void print(boolean b) 
			{
				print(String.valueOf(b));
			}

			@Override
			public void print(char c) 
			{
				print(String.valueOf(c));
			}

			@Override
			public void print(char[] s) 
			{
				print(String.valueOf(s));
			}

			@Override
			public void print(double d) 
			{
				print(String.valueOf(d));
			}

			@Override
			public void print(float f) 
			{
				print(String.valueOf(f));
			}

			@Override
			public void print(int i) 
			{
				print(String.valueOf(i));
			}

			@Override
			public void print(long l) 
			{
				print(String.valueOf(l));
			}

			@Override
			public void print(Object obj) 
			{
				print(obj.toString());
			}

			
			public void print(final String s) 
		    {
				try {
					textArea.getDocument().insertString(textArea.getDocument().getLength(), s + "\n", set);
					textArea.setCaretPosition(textArea.getDocument().getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
		        super.print(s);
		    }	       
		});

		textField.addKeyListener(new KeyListener() 
		{			
			public String text = "";
			
			@Override
			public void keyTyped(KeyEvent e) 
			{
			    if(e.getKeyCode() == KeyEvent.VK_ENTER)
			    {
			    	Server.server.inputHandler.handleInput(text);
			    	text = "";
			    }
			    else text += e.getKeyChar();
			}
	
			@Override
			public void keyPressed(KeyEvent e) {}
	
			@Override
			public void keyReleased(KeyEvent e) {}
		});

		frame.setVisible(true);
	}

	private static void changePermissions(String username) 
	{
		
	}
	
	public static void updatePlayers()
	{
		synchronized(tablePlayers)
		{
			DefaultTableModel model = (DefaultTableModel) tablePlayers.getModel();
		    model.setRowCount(0);
		    for(Connection con : Server.connections.values())
		    {
		    	String username = con.username;
		    	String ip = con.ip.toString();
		    	model.addRow(new Object[]{username, ip, 0, 0});
		    }
		    model.fireTableDataChanged();
		    
		    
		}
	}
	
	public static void tick() 
	{
		if(tablePlayers != null)
		{
			synchronized(tablePlayers)
			{
				StatisticPanel.instance.updateUI();
				DefaultTableModel model = (DefaultTableModel) tablePlayers.getModel();
				
				int i = 0;
				for(Connection con : Server.connections.values())
			    {
			    	model.setValueAt(con.packetHandler.getQueueLenght(), i, 2);
			    	model.setValueAt(con.packetHandler.getSendingQueueLenght(), i, 3);
					i++;
			    }
				model.fireTableDataChanged();
				players.setBorder(BorderFactory.createTitledBorder("Players (" + Server.actConnections + "/" + Server.MAX_CONNECTIONS + ")"));
			}
		}
	}
}
