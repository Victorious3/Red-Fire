package vic.rpg.world.tile;

import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import vic.rpg.client.render.LightSource;
import vic.rpg.client.render.TextureLoader;
import vic.rpg.utils.Utils;
import vic.rpg.world.Map;
import bsh.EvalError;
import bsh.Interpreter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
	public Texture getTexture(int x, int y, int data, int layerID, Map map) 
	{
		return texture;
	}

	@Override
	public Point getTextureCoord(int x, int y, int data, int layerID, Map map) 
	{
		if(textureCoords.containsKey(data)) return textureCoords.get(data);
		else return textureCoords.get(0);
	}

	@Override
	public int getHeight(int x, int y, int data, int layerID, Map map) 
	{
		if(heights.containsKey(data)) return heights.get(data);
		else return heights.get(0);
	}

	@Override
	public boolean emitsLight(int x, int y, int data, int layerID, Map map) 
	{
		if(emitsLight.containsKey(data)) return emitsLight.get(data);
		else return emitsLight.get(0);
	}

	@Override
	public LightSource getLightSource(int x, int y, int data, int layerID, Map map) 
	{
		if(lightSources.containsKey(data)) return lightSources.get(data);
		else return lightSources.get(0);
	}

	@Override
	public Point getLightPosition(int x, int y, int data, int layerID, Map map) 
	{
		if(lightPositions.containsKey(data)) return lightPositions.get(data);
		else return lightPositions.get(0);
	}

	@Override
	public void tick(int x, int y, int data, int layerID, Map map) 
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
	public double getMovementCost(int x, int y, int layerID, Map map) 
	{
		if(movementCosts.containsKey(data)) return movementCosts.get(data);
		else return movementCosts.get(0);
	}

	@Override
	public boolean isWalkingPermitted(int x, int y, int layerID, Map map) 
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
	public static TileJSON parse(File file) throws FileNotFoundException, IOException
	{
		TileJSON tile = new TileJSON();
		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject)parser.parse(new FileReader(file));

		String name = obj.get("name").getAsString();
		Integer id = obj.get("id").getAsInt();
		String description = obj.has("description") ? obj.get("description").getAsString() : "";
		String source = obj.has("source") ? obj.get("source").getAsString() : "";
		Texture texture = TextureLoader.requestTexture(Utils.readImage(Utils.getAppdata() + "/resources/tiles/" + obj.get("texture").getAsString()));
		
		tile.filePath = file.getAbsolutePath();
		tile.texturePath = obj.get("texture").getAsString();
		tile.suggestedID = id;
		tile.name = name;
		tile.description = description;
		tile.source = source;
		tile.isTicking = obj.has("source");
		tile.texture = texture;
		
		JsonArray tileArray = (JsonArray)obj.get("tiles");
		
		for(Object o : tileArray)
		{
			JsonObject o2 = (JsonObject)o;
			int data = o2.has("data") ? o2.get("data").getAsInt() : 0;
			Point texPoint = new Point(o2.get("texX").getAsInt(), o2.get("texY").getAsInt());
			int height = o2.has("height") ? o2.get("height").getAsInt() : 1;
			boolean emitsLight = o2.has("emitsLight") ? o2.get("emitsLight").getAsBoolean() : false;
			LightSource ls = emitsLight ? new LightSource(o2.get("lightWidth").getAsInt(), o2.get("lightBrightness").getAsFloat(), new Color(o2.get("lightColor").getAsInt()), o2.get("lightFlickering").getAsBoolean()) : null;
			Point lightPosition = emitsLight ? new Point(o2.get("lightX").getAsInt(), o2.get("lightY").getAsInt()) : null;
			boolean isWalkingPermitted = o2.get("isWalkingPermitted").getAsBoolean();
			double movementCost = isWalkingPermitted ? o2.get("movementCost").getAsDouble() : 0;
			
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
