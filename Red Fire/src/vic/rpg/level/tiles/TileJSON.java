package vic.rpg.level.tiles;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import vic.rpg.render.LightSource;
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
	
	private HashMap<Integer, Texture> textures;
	private HashMap<Integer, Point> textureCoords;
	private HashMap<Integer, Integer> heights;
	private HashMap<Integer, Boolean> emitsLight;
	private HashMap<Integer, LightSource> lightSources;
	private HashMap<Integer, Point> lightPositions;
	private String description;
	private String name;
	private HashMap<Integer, Double> movementCosts;
	private HashMap<Integer, Boolean> isWalkingPermitted;
	
	private int suggestedID = 10;
	
	@Override
	public Texture getTexture(int x, int y, int data) 
	{
		if(textures.containsKey(data)) return textures.get(data);
		else return textures.get(0);
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
	
	public static TileJSON parse(File file) throws FileNotFoundException, IOException, ParseException
	{
		TileJSON tile = new TileJSON();
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(new FileReader(file));

		String name = (String)obj.get("name");
		String source = (String)(obj.containsKey("source") ? obj.get("source") : "");
		
		tile.name = name;
		tile.source = source;
		tile.isTicking = obj.containsKey("source");
		
		return tile;
	}
}
