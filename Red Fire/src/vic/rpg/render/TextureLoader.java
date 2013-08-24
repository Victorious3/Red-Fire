package vic.rpg.render;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class TextureLoader {

	private static HashMap<String, Texture> bufferedTextures = new HashMap<String, Texture>();
	private static HashMap<Texture, BufferedImage> requestedTextures = new HashMap<Texture, BufferedImage>();
	
	public static void storeTexture(String name, String filePath)
	{
		BufferedImage img = Utils.readImageFromJar(filePath);
		bufferedTextures.put(name, requestTexture(img));
	}
	
	private static synchronized HashMap<Texture, BufferedImage> getRequestedTextures()
	{
		return requestedTextures;
	}
	
	public static Texture loadTexture(String name)
	{
		return bufferedTextures.get(name);
	}
	
	public static Texture requestTexture(BufferedImage i)
	{
		Texture tex = new Texture(0);
		getRequestedTextures().put(tex, i);
		return tex;
	}
	
	/**
	 * <b>Only for use inside the render(GL2 gl2) method!</b>
	 * @param i
	 * @return Texture
	 */
	public static Texture createTexture(BufferedImage i)
	{
		return AWTTextureIO.newTexture(Game.GL_PROFILE, i, true);
	}
	
	public static void setupTextures(GL2 gl2)
	{
		for(Texture t : getRequestedTextures().keySet())
		{
			BufferedImage img = getRequestedTextures().get(t);
			t.updateImage(gl2, AWTTextureIO.newTextureData(Game.GL_PROFILE, img, true));
		}
		getRequestedTextures().clear();
	}
	
	public static BufferedImage[] getAnimatedImageData(String filePath)
	{
		try {
			ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();  
			ImageInputStream in = ImageIO.createImageInputStream(Utils.getStreamFromJar(filePath));  
			reader.setInput(in);  
			
			BufferedImage[] gifData = new BufferedImage[reader.getNumImages(true)];
			
			for (int i = 0, count = reader.getNumImages(true); i < count; i++)  
			{  
			    BufferedImage img = reader.read(i);  
			    gifData[i] = img;
			}		
			
			return gifData;
			
		} catch (Exception e) {
			throw new RuntimeException("Animated Image " + filePath + " could not be loaded. Maybe the file is missing or obstructed.");
		}
	}
}
