package vic.rpg.server.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintStream;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import vic.rpg.registry.GameRegistry;
import vic.rpg.server.Server;

public class ServerGui 
{
	public static JFrame frame;
	public static JTextPane textArea; 
	public static JScrollPane scrollPane;
	public static JTextField textField;

	@SuppressWarnings("unused")
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
		
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.add(textField, BorderLayout.SOUTH);
		frame.setSize(800, 600);
		frame.addWindowListener(new WindowListener() 
		{
			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) 
			{
				if(Server.server.inputHandler != null) Server.server.inputHandler.handleCommand("stop", null);
				else System.exit(0);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowOpened(WindowEvent e) {}	      
			
		});
		
		frame.setTitle("-~/RedFire\\~- Server " + GameRegistry.VERSION);

		/*textField.addKeyListener(new KeyListener() {
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
					
				}
			}
		  
		});

		System.setIn(new BufferedInputStream(new InputStream(){
			@Override
			public int read() throws IOException
			{
				int c = -1;
				try {
					c = sb.take();            
				} catch(InterruptedException ie) {} 
				return c;           
			}
		}));*/
		
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
			
			public void print(final String s) 
		    {
				try {
					textArea.getDocument().insertString(textArea.getDocument().getLength(), s + "\n", set);
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
			
			public void print(final String s) 
		    {
				try {
					textArea.getDocument().insertString(textArea.getDocument().getLength(), s + "\n", set);
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
}
