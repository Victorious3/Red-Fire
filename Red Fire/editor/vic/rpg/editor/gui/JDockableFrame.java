package vic.rpg.editor.gui;

import java.util.ArrayList;

import javax.swing.JInternalFrame;

public class JDockableFrame extends JInternalFrame 
{
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	private ArrayList<Integer> docks = new ArrayList<Integer>();
	
	public JDockableFrame(String string) 
	{
		this(string, false, false, false, false);
	}
	public JDockableFrame(String string, boolean b) 
	{
		this(string, b, false, false, false);
	}
	public JDockableFrame(String string, boolean b, boolean c) 
	{
		this(string, b, c, false, false);
	}
	public JDockableFrame(String string, boolean b, boolean c, boolean d) 
	{
		this(string, b, c, d, false);
	}
	public JDockableFrame(String string, boolean b, boolean c, boolean d, boolean e) 
	{
		super(string, b, c, d, e);
	}

	public void addDock(int dockType)
	{
		docks.add(dockType);
	}
	
	public void removeDock(int dockType)
	{
		docks.remove(dockType);
	}
	
	public ArrayList<Integer> getDocks()
	{
		return docks;
	}
	
	public void clearDocks()
	{
		docks.clear();
	}
}
