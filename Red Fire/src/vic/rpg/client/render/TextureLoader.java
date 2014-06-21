package vic.rpg.client.render;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * The TextureLoader does bind all the {@link BufferedImage BufferedImages} to the OpenGL context
 * and creates {@link Texture Textures}. The nice part is that you can create {@link Texture Textures}
 * everywhere using {@link #requestTexture(BufferedImage)} and the Textures are automatically created
 * inside the next frame.
 * @author Victorious3
 */
public class TextureLoader {

	private static ConcurrentHashMap<String, Texture> bufferedTextures = new ConcurrentHashMap<String, Texture>();
	private static ConcurrentHashMap<Texture, BufferedImage> requestedTextures = new ConcurrentHashMap<Texture, BufferedImage>();
	
	@Deprecated
	public static void storeTexture(String name, String filePath)
	{
		BufferedImage img = Utils.readImage(filePath);
		bufferedTextures.put(name, requestTexture(img));
	}
	
	private static synchronized ConcurrentHashMap<Texture, BufferedImage> getRequestedTextures()
	{
		return requestedTextures;
	}
	
	@Deprecated
	public static Texture loadTexture(String name)
	{
		return bufferedTextures.get(name);
	}
	
	/**
	 * Creates a new empty {@link Texture} for later processing.
	 * @param i
	 * @return Textures
	 */
	public static Texture requestTexture(BufferedImage i)
	{
		Texture tex = new Texture(0);
		getRequestedTextures().put(tex, i);
		return tex;
	}
	
	/**
	 * Creates a new {@link Texture}.
	 * <b>Only for use inside the render(GL2 gl2) method!</b>
	 * @param i
	 * @return Texture
	 */
	public static Texture createTexture(BufferedImage i)
	{
		return AWTTextureIO.newTexture(Game.GL_PROFILE, i, true);
	}
	
	/**
	 * Cycles through all {@link BufferedImage BufferedImages} and creates {@link Texture Textures} of it.
	 * @param gl2
	 */
	public static void setupTextures(GL2 gl2)
	{
		for(Texture t : getRequestedTextures().keySet())
		{
			BufferedImage img = getRequestedTextures().get(t);
			t.updateImage(gl2, AWTTextureIO.newTextureData(Game.GL_PROFILE, img, true));
		}
		getRequestedTextures().clear();
	}
	
	/**
	 * Returns the contents of an animated gif file.
	 * @param filePath
	 * @return BufferedImage[]
	 */
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
