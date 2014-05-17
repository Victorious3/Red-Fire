package vic.rpg.editor;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import vic.rpg.utils.Utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

/**
 * GuiState saves the state of a given {@link Component} and can restore it on restart.
 * Currently supported {@link Component Components} are: {@link JFrame}, {@link JDialog}, {@link JSplitPane}
 * @author Victorious3
 */
public class GuiState 
{
	private static HashMap<String, HashMap<String, JsonPrimitive>> state = new HashMap<String, HashMap<String, JsonPrimitive>>();
	
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
			state.get(name).put("width", new JsonPrimitive(frame.getWidth()));
			state.get(name).put("height", new JsonPrimitive(frame.getHeight()));
			state.get(name).put("x", new JsonPrimitive(frame.getX()));
			state.get(name).put("y", new JsonPrimitive(frame.getY()));
			state.get(name).put("isMaximized", new JsonPrimitive((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH));
		}
		else if(comp instanceof JDialog)
		{
			JDialog dialog = (JDialog) comp;
			newState(name);
			state.get(name).put("width", new JsonPrimitive(dialog.getWidth()));
			state.get(name).put("height", new JsonPrimitive(dialog.getHeight()));
			state.get(name).put("x", new JsonPrimitive(dialog.getX()));
			state.get(name).put("y", new JsonPrimitive(dialog.getY()));
		}
		else if(comp instanceof JSplitPane)
		{
			JSplitPane splitPane = (JSplitPane) comp;
			newState(name);
			state.get(name).put("divider", new JsonPrimitive(splitPane.getDividerLocation()));
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
			if(state.get(name).get("isMaximized").getAsBoolean()) frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			else
			{
				frame.setSize(state.get(name).get("width").getAsInt(), state.get(name).get("height").getAsInt());
				frame.setLocation(state.get(name).get("x").getAsInt(), state.get(name).get("y").getAsInt());
			}
		}
		else if(comp instanceof JDialog)
		{
			JDialog dialog = (JDialog) comp;
			dialog.setSize(state.get(name).get("width").getAsInt(), state.get(name).get("height").getAsInt());
			dialog.setLocation(state.get(name).get("x").getAsInt(), state.get(name).get("y").getAsInt());
		}
		else if(comp instanceof JSplitPane)
		{
			JSplitPane splitPane = (JSplitPane) comp;
			splitPane.setDividerLocation(state.get(name).get("divider").getAsInt());
		}
		else
		{
			throw new IllegalArgumentException("No state save supported for component \"" + comp.getClass().getSimpleName() + "\"!");
		}
	}
	
	/**
	 * Saves the current state to the file {@code %APPDATA%/.RedFire/tmp/guistate.dat}.
	 */
	public static void saveToFile()
	{
		JsonObject mainObj = new JsonObject();
		for(String name : state.keySet())
		{
			HashMap<String, JsonPrimitive> data = state.get(name);
			JsonObject subObj = new JsonObject();
			for(String name2 : data.keySet())
			{
				subObj.add(name2, data.get(name2));
			}
			mainObj.add(name, subObj);
		}
		File out = Utils.getOrCreateFile(Utils.getAppdata() + "/tmp/guistate.dat");
		try {
			FileWriter writer = new FileWriter(out);
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(mainObj));
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
		
		JsonParser parser = new JsonParser();
		JsonObject mainObj;
		try {
			mainObj = (JsonObject)parser.parse(new FileReader(f));
			
			for(Entry<String, JsonElement> obj1 : mainObj.entrySet())
			{
				state.put(obj1.getKey(), new HashMap<String, JsonPrimitive>());
				JsonObject subObj = obj1.getValue().getAsJsonObject();
				for(Entry<String, JsonElement> obj2 : subObj.entrySet())
				{
					state.get(obj1.getKey()).put(obj2.getKey(), obj2.getValue().getAsJsonPrimitive());
				}		
			}			
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	private static void newState(String name)
	{
		if(state.containsKey(name)) return;
		state.put(name, new HashMap<String, JsonPrimitive>());
	}
}
