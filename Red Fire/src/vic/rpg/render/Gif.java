package vic.rpg.render;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import vic.rpg.utils.Utils;

public class Gif
{
	private ImageReader reader;
	private BufferedImage img;
	private int size;
	private int counter = 0;
	private int tickRate = 4;
	
	private static ArrayList<Gif> images = new ArrayList<Gif>();
	
	public Gif(String string, int tickRate) 
	{
		this.tickRate = tickRate;
		
		try
		{
			reader = ImageIO.getImageReadersBySuffix("gif").next();  
			ImageInputStream in = ImageIO.createImageInputStream(Utils.getStreamFromString(string));  
			reader.setInput(in);

			size = reader.getNumImages(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		images.add(this);
	}
	
	int tickCounter = 0;
	public void tick()
	{	
		try {
			tickCounter++;
			if(tickCounter == tickRate)
			{
				counter++;
				if(counter >= size) counter = 0;
				img = reader.read(counter);				
				tickCounter = 0;
				
			}
		} catch (Exception e) {			
			try {
				reader.dispose();
			} catch (Exception e2) {}
		}
	}
	
	public BufferedImage getImage()
	{
		return img;
	}
	
	public static void tickAll()
	{
		for(Gif img : images)
		{
			try {
				img.tick();
			} catch (Exception e) {}			
		}
	}
}
