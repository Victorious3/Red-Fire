package vic.rpg.editor;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vic.rpg.utils.Utils;

/**
 * GuiState saves the state of a given {@link Component} and can restore it on restart.
 * Currently supported {@link Component Components} are: {@link JFrame}, {@link JDialog}, {@link JSplitPane}
 * @author Victorious3
 */
public class GuiState 
{
	private static HashMap<String, HashMap<String, Object>> state = new HashMap<String, HashMap<String, Object>>();
	
	/**
	 * Saves the state of a given component.
	 * @param comp
	 * @param name
	 */
	public static void save(Component comp, String name)
	{
		if(comp instanceof JFrame)
		{
			JFrame frame = (JFrame) comp;
			newState(name);
			state.get(name).put("width", Long.valueOf(frame.getWidth()));
			state.get(name).put("height", Long.valueOf(frame.getHeight()));
			state.get(name).put("x", Long.valueOf(frame.getX()));
			state.get(name).put("y", Long.valueOf(frame.getY()));
			state.get(name).put("isMaximized", (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH);
		}
		else if(comp instanceof JDialog)
		{
			JDialog dialog = (JDialog) comp;
			newState(name);
			state.get(name).put("width", Long.valueOf(dialog.getWidth()));
			state.get(name).put("height", Long.valueOf(dialog.getHeight()));
			state.get(name).put("x", Long.valueOf(dialog.getX()));
			state.get(name).put("y", Long.valueOf(dialog.getY()));
		}
		else if(comp instanceof JSplitPane)
		{
			JSplitPane splitPane = (JSplitPane) comp;
			newState(name);
			state.get(name).put("divider", Long.valueOf(splitPane.getDividerLocation()));
		}
		else
		{
			throw new IllegalArgumentException("No state save supported for component \"" + comp.getClass().getSimpleName() + "\"!");
		}
	}
	
	/**
	 * Restores the state of a given component.
	 * @param comp
	 * @param name
	 */
	public static void restore(Component comp, String name)
	{
		if(!state.containsKey(name)) return;
		if(comp instanceof JFrame)
		{
			JFrame frame = (JFrame) comp;
			if((Boolean)state.get(name).get("isMaximized")) frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			else
			{
				frame.setSize((int)(long)state.get(name).get("width"), (int)(long)state.get(name).get("height"));
				frame.setLocation((int)(long)state.get(name).get("x"), (int)(long)state.get(name).get("y"));
			}
		}
		else if(comp instanceof JDialog)
		{
			JDialog dialog = (JDialog) comp;
			dialog.setSize((int)(long)state.get(name).get("width"), (int)(long)state.get(name).get("height"));
			dialog.setLocation((int)(long)state.get(name).get("x"), (int)(long)state.get(name).get("y"));
		}
		else if(comp instanceof JSplitPane)
		{
			JSplitPane splitPane = (JSplitPane) comp;
			splitPane.setDividerLocation((int)(long)state.get(name).get("divider"));
		}
		else
		{
			throw new IllegalArgumentException("No state save supported for component \"" + comp.getClass().getSimpleName() + "\"!");
		}
	}
	
	/**
	 * Saves the current state to the file {@code %APPDATA%/.RedFire/tmp/guistate.dat}.
	 */
	@SuppressWarnings("unchecked")
	public static void saveToFile()
	{
		JSONObject mainObj = new JSONObject();
		for(String name : state.keySet())
		{
			HashMap<String, Object> data = state.get(name);
			JSONObject subObj = new JSONObject();
			for(String name2 : data.keySet())
			{
				Object obj = data.get(name2);
				subObj.put(name2, obj);
			}
			mainObj.put(name, subObj);
		}
		File out = Utils.getOrCreateFile(Utils.getAppdata() + "/tmp/guistate.dat");
		try {
			FileWriter writer = new FileWriter(out);
			writer.write(mainObj.toJSONString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readFromFile()
	{
		File f = new File(Utils.getAppdata() + "/tmp/guistate.dat");
		if(!f.exists()) return;
		JSONParser parser = new JSONParser();
		try {
			JSONObject mainObj = (JSONObject)parser.parse(new FileReader(f));
			for(Object name : mainObj.keySet())
			{
				state.put((String) name, new HashMap<String, Object>());
				JSONObject subObj = (JSONObject) mainObj.get(name);
				for(Object name2 : subObj.keySet())
				{
					Object obj = subObj.get(name2);
					state.get(name).put((String)name2, obj);
				}
			}		
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static void newState(String name)
	{
		if(state.containsKey(name)) return;
		state.put(name, new HashMap<String, Object>());
	}
}
