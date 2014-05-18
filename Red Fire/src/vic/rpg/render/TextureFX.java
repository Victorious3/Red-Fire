package vic.rpg.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.media.opengl.GL2;

import vic.rpg.utils.Utils;
import vic.rpg.world.entity.living.EntityLiving;

import com.jogamp.opengl.util.texture.Texture;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

/**
 * A TextureFX is basically an array of {@link Texture Textures} that updates its current {@link Texture}
 * at the specified framerate.
 * @author Victorious3
 */
public class TextureFX implements Cloneable
{
	private Texture[] data;
	private float framerate;
	private boolean isPlaying = true;
	private boolean isFinished = false;
	private boolean isRepeating = true;
	private int imgPointer = 0;
	
	/**
	 * Globally controls weather the textures should be animated.
	 */
	public static boolean IS_PLAYING = true;
	
	public TextureFX(String gifURL, int framerate)
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
	
	public TextureFX(BufferedImage img, int width, int height, int repeatX, int xOff, int yOff, int framerate)
	{
		this.framerate = framerate;
		if(width * repeatX + xOff > img.getWidth() || height + yOff > img.getHeight())
		{
			throw new IllegalArgumentException("impossible parameters");
		}
		
		ArrayList<Texture> texFrames = new ArrayList<Texture>();
		for(int x = 0; x < repeatX; x++)
		{
			texFrames.add(TextureLoader.requestTexture(img.getSubimage(x * width + xOff, yOff, width, height)));
		}
		data = texFrames.toArray(new Texture[texFrames.size()]);
	}
	
	public TextureFX(String pngURL, int width, int height, int repeatX, int xOff, int yOff, int framerate)
	{
		this(Utils.readImage(pngURL), width, height, repeatX, xOff, yOff, framerate);
	}
	
	/**
	 * Parses a sprite sheet that is used by {@link EntityLiving}.
	 * @param pngURL
	 * @param width
	 * @param height
	 * @param repeatX
	 * @param repeatY
	 * @param xOff
	 * @param yOff
	 * @param framerate
	 * @return extureFX[]
	 */
	public static TextureFX[] createTextureFXArray(String pngURL, int width, int height, int repeatX, int repeatY, int xOff, int yOff, int framerate)
	{
		ArrayList<TextureFX> texFrames = new ArrayList<TextureFX>();
		BufferedImage img = Utils.readImage(pngURL);
		if(height * repeatY + yOff > img.getHeight()) throw new IllegalArgumentException("impossible parameters");
		
		for(int y = 0; y < repeatY; y++)
		{
			texFrames.add(new TextureFX(img, width, height, repeatX, xOff, y * height + yOff, framerate));
		}
		return texFrames.toArray(new TextureFX[texFrames.size()]);
	}
	
	private TextureFX(float framerate)
	{
		this.framerate = framerate;
	}
	
	@Override
	public TextureFX clone()
	{
		TextureFX clone = new TextureFX(this.framerate);
		clone.data = this.data;
		return clone;
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
		return isFinished;
	}
	
	/**
	 * Starts animationg the TextureFX
	 */
	public void start()
	{
		this.isPlaying = true;
	}
	
	/**
	 * Stops animation the TextureFX
	 */
	public void stop()
	{
		this.isPlaying = false;
	}
	
	public void setRepeating(boolean repeat)
	{
		this.isRepeating = repeat;
	}
	
	private int getNextFrame()
	{
		if(isPlaying && IS_PLAYING)
		{
			float frametime = 1000 / framerate;
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
