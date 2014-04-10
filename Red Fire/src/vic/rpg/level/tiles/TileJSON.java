package vic.rpg.level.tiles;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vic.rpg.render.LightSource;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;
import bsh.EvalError;
import bsh.Interpreter;

import com.jogamp.opengl.util.texture.Texture;

/**
 * This is a special tile that's instanced and provided with attributes that can be
 * changed in the Editor.
 * @author Victorious3
 */
public class TileJSON extends Tile
{
	private Interpreter interpreter = new Interpreter();
	private boolean isTicking = false;
	private String source;
	
	private Texture texture;
	private HashMap<Integer, Point> textureCoords = new HashMap<Integer, Point>();
	private HashMap<Integer, Integer> heights = new HashMap<Integer, Integer>();
	private HashMap<Integer, Boolean> emitsLight = new HashMap<Integer, Boolean>();
	private HashMap<Integer, LightSource> lightSources = new HashMap<Integer, LightSource>();
	private HashMap<Integer, Point> lightPositions = new HashMap<Integer, Point>();
	private String description;
	private String name;
	private HashMap<Integer, Double> movementCosts = new HashMap<Integer, Double>();
	private HashMap<Integer, Boolean> isWalkingPermitted = new HashMap<Integer, Boolean>();
	
	private int suggestedID = 0;
	private String filePath;
	private String texturePath;
	
	@Override
	public Texture getTexture(int x, int y, int data) 
	{
		return texture;
	}

	@Override
	public Point getTextureCoord(int x, int y, int data) 
	{
		if(textureCoords.containsKey(data)) return textureCoords.get(data);
		else return textureCoords.get(0);
	}

	@Override
	public int getHeight(int x, int y, int data) 
	{
		if(heights.containsKey(data)) return heights.get(data);
		else return heights.get(0);
	}

	@Override
	public boolean emitsLight(int x, int y, int data) 
	{
		if(emitsLight.containsKey(data)) return emitsLight.get(data);
		else return emitsLight.get(0);
	}

	@Override
	public LightSource getLightSource(int x, int y, int data) 
	{
		if(lightSources.containsKey(data)) return lightSources.get(data);
		else return lightSources.get(0);
	}

	@Override
	public Point getLightPosition(int x, int y, int data) 
	{
		if(lightPositions.containsKey(data)) return lightPositions.get(data);
		else return lightPositions.get(0);
	}

	@Override
	public void tick(int x, int y, int data) 
	{
		if(!isTicking) return;
		try {
			interpreter.eval(source);
		} catch (EvalError e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() 
	{
		return description;
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public double getMovementCost() 
	{
		if(movementCosts.containsKey(data)) return movementCosts.get(data);
		else return movementCosts.get(0);
	}

	@Override
	public boolean isWalkingPermitted() 
	{
		if(isWalkingPermitted.containsKey(data)) return isWalkingPermitted.get(data);
		else return isWalkingPermitted.get(0);
	}
	
	public int getSuggestedID()
	{
		return suggestedID;
	}
	
	/**
	 * Returns the absolute path of this TileJSON.
	 * @return String
	 */
	public String getFilePath()
	{
		return filePath;
	}
	
	/**
	 * Returns the relative path of the referenced Texure.
	 * @return String
	 */
	public String getTexturePath()
	{
		return texturePath;
	}
	
	/**
	 * Parses a new TileJSON from a given {@link File}.
	 * @param file
	 * @return TileJSON
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static TileJSON parse(File file) throws FileNotFoundException, IOException, ParseException
	{
		TileJSON tile = new TileJSON();
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(new FileReader(file));

		String name = (String)obj.get("name");
		Integer id = (int)(long)obj.get("id");
		String description = (String)(obj.containsKey("description") ? obj.get("description") : "");
		String source = (String)(obj.containsKey("source") ? obj.get("source") : "");
		Texture texture = TextureLoader.requestTexture(Utils.readImage(Utils.getAppdata() + "/resources/tiles/" + (String)obj.get("texture")));
		
		tile.filePath = file.getAbsolutePath();
		tile.texturePath = (String)obj.get("texture");
		tile.suggestedID = id;
		tile.name = name;
		tile.description = description;
		tile.source = source;
		tile.isTicking = obj.containsKey("source");
		tile.texture = texture;
		
		JSONArray tileArray = (JSONArray)obj.get("tiles");
		
		for(Object o : tileArray)
		{
			JSONObject o2 = (JSONObject)o;
			Integer data = (int)(long)(o2.containsKey("data") ? o2.get("data") : 0L);
			Point texPoint = new Point((int)(long)o2.get("texX"), (int)(long)o2.get("texY"));
			Integer height = (int)(long)(o2.containsKey("height") ? o2.get("height") : 1L);
			Boolean emitsLight = (Boolean)(o2.containsKey("emitsLight") ? o2.get("emitsLight") : false);
			LightSource ls = emitsLight ? new LightSource((int)(long)o2.get("lightWidth"), (float)(long)o2.get("lightBrightness"), new Color((int)(long)o2.get("lightColor")), (boolean)o2.get("lightFlickering")) : null;
			Point lightPosition = emitsLight ? new Point((int)(long)o2.get("lightX"), (int)(long)o2.get("lightY")) : null;
			Boolean isWalkingPermitted = (Boolean)o2.get("isWalkingPermitted");
			Double movementCost = isWalkingPermitted ? (long)o2.get("movementCost") : 0D;
			
			tile.textureCoords.put(data, texPoint);
			tile.heights.put(data, height);
			tile.emitsLight.put(data, emitsLight);
			tile.lightSources.put(data, ls);
			tile.lightPositions.put(data, lightPosition);
			tile.isWalkingPermitted.put(data, isWalkingPermitted);
			tile.movementCosts.put(data, movementCost);
		}
		
		return tile;
	}
}
