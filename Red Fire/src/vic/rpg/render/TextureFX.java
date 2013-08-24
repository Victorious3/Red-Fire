package vic.rpg.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.media.opengl.GL2;

import vic.rpg.utils.Utils;

import com.jogamp.opengl.util.texture.Texture;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

public class TextureFX 
{
	private Texture[] data;
	private float framerate;
	private boolean isPlaying = true;
	private boolean hasFinished = false;
	private boolean isRepeating = true;
	private int imgPointer = 0;
	
	public TextureFX(String gifURL, float framerate)
	{
		this.framerate = framerate;
		
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
	    ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
	    try {
			ir.setInput(ImageIO.createImageInputStream(Utils.getStreamFromJar(gifURL)));
			for(int i = 0; i < ir.getNumImages(true); i++)
			{
				frames.add(ir.read(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    ir.dispose();
	    
	    ArrayList<Texture> texFrames = new ArrayList<Texture>();
	    for(BufferedImage img : frames)
	    {
	    	texFrames.add(TextureLoader.requestTexture(img));
	    }
	    data = texFrames.toArray(new Texture[texFrames.size()]);
	}
	
	private long lastTime = System.currentTimeMillis();
	
	public void draw(GL2 gl2, int x, int y)
	{
		DrawUtils.setGL(gl2);
		DrawUtils.drawTexture(x, y, data[getNextFrame()]);	
	}
	
	public Texture getCurrentTexture()
	{
		return(data[getNextFrame()]);
	}
	
	public boolean hasFinished()
	{
		return hasFinished;
	}
	
	public void start()
	{
		this.isPlaying = true;
	}
	
	public void stop()
	{
		this.isPlaying = false;
	}
	
	private int getNextFrame()
	{
		if(isPlaying)
		{
			float frametime = 100 / framerate;
			long currTime = System.currentTimeMillis();
			if(currTime - lastTime >= frametime)
			{
				int offset = (int) ((currTime - lastTime) / frametime);
				imgPointer += offset;
				lastTime = System.currentTimeMillis();
				if(imgPointer >= data.length) 
				{
					if(isRepeating) imgPointer = 0;
					else imgPointer = data.length - 1;
				}
			}
		}
		return imgPointer;
	}
}
