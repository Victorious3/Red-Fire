package vic.rpg.render;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

public class Drawable implements Cloneable
{
	private int width;
	private int height;
	
	private Object texture;
	
	public Drawable(int width, int height)
	{
		this.width = width;
		this.height = height;
		if(width <= 0 || height <= 0) throw new IllegalArgumentException("Width (" + width + ") and height (" + height + ") cannot be <= 0");
		
		try {
			this.texture = new Texture(0);
		} catch (NegativeArraySizeException e) {
			throw new IllegalArgumentException("Width (" + width + ") * height (" + height + ") cannot be bigger than " + Integer.MAX_VALUE);
		}
	}
	
	@Override
	public Drawable clone()
	{
		try {
			return (Drawable) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object getTexture()
	{
		return texture;
	}

	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	protected void setWidth(int width)
	{
		this.width = width;
	}
	
	protected void setHeight(int height)
	{
		this.height = height;
	}
	
	public void resetTexture()
	{
		this.texture = new Texture(0);	
	}
	
	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}
	
	public void setTexture(TextureFX fx)
	{
		this.texture = fx;
	}
	
	public void setTexture(BufferedImage img)
	{
		this.texture = TextureLoader.requestTexture(img);
	}

	public void render(GL2 gl2){}
}
