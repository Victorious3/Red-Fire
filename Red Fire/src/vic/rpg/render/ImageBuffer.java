package vic.rpg.render;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import vic.rpg.utils.Utils;

public class ImageBuffer {

	private static HashMap<String, Image> bufferedImage = new HashMap<String, Image>();
	
	@Deprecated
	public static void bufferImage(String name, String filePath)
	{
		Image img = Utils.readImageFromJar(filePath);	
		bufferedImage.put(name, img);
	}
	
	@Deprecated
	public static Image getImage(String name)
	{
		return bufferedImage.get(name);
	}
	
	public static Image[] getAnimatedImageData(String filePath)
	{
		try {
			ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();  
			ImageInputStream in = ImageIO.createImageInputStream(Utils.getStreamFromString(filePath));  
			reader.setInput(in);  
			
			Image[] gifData = new Image[reader.getNumImages(true)];
			
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
