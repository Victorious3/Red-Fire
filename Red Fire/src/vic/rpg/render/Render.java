package vic.rpg.render;

import static vic.rpg.registry.RenderRegistry.CL_CONTEXT;
import static vic.rpg.registry.RenderRegistry.CL_ENABLED;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import vic.rpg.utils.Utils;

@Deprecated
public class Render {
	
	@Override
	protected Render clone()
	{
		try {
			return (Render) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage img;
	public Graphics2D g2d;
	
	public static void setup()
	{
		if(CL_ENABLED)
		{
			try {
				CL_CONTEXT.createProgram(Utils.getStreamFromString("/vic/rpg/resources/jocl/render.cl")).build().createCLKernel("render");;
//				DRAW_RECT = CL_CONTEXT.createProgram(Utils.getStreamFromString("/vic/rpg/resources/jocl/render_rect.cl")).build().createCLKernel("render");
//				DRAW_LINE = CL_CONTEXT.createProgram(Utils.getStreamFromString("/vic/rpg/resources/jocl/render_line.cl")).build().createCLKernel("render");
//				DRAW_FILLEDCIRCLE = CL_CONTEXT.createProgram(Utils.getStreamFromString("/vic/rpg/resources/jocl/render_filledcircle.cl")).build().createCLKernel("render");
//				DRAW_CIRCLE = CL_CONTEXT.createProgram(Utils.getStreamFromString("/vic/rpg/resources/jocl/render_circle.cl")).build().createCLKernel("render");
							
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Render(int width, int height)
	{
		if(width <= 0 || height <= 0) throw new IllegalArgumentException("Width (" + width + ") and height (" + height + ") cannot be <= 0");
		
		try{
		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		} catch (NegativeArraySizeException e) {
			throw new IllegalArgumentException("Width (" + width + ") * height (" + height + ") cannot be bigger than " + Integer.MAX_VALUE);
		}
		this.g2d = img.createGraphics();
	}
	
	public Graphics2D g2d()
	{
		return img.createGraphics();
	}
	
	public int getWidth()
	{
		return img.getWidth();
	}
	
	public int getHeight()
	{
		return img.getHeight();
	}

	public void draw(Render render, int xOffset, int yOffset) 
	{	
		g2d.drawImage(render.img, null, xOffset, yOffset);
	}
	
	public void drawImage(String bufferedImage, int xOffset, int yOffset, int width, int height)
	{
		g2d.drawImage(ImageBuffer.getImage(bufferedImage), xOffset, yOffset, width, height, null);
	}
	
	public void resetImage()
	{
		this.img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		this.g2d = g2d();
	}
	
	public void setImage(BufferedImage img)
	{
		this.img = img;
		this.g2d = g2d();
	}
	
	public void setImage(Image img)
	{
		this.img = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		this.g2d = g2d();
		this.g2d.drawImage(img, 0, 0, null);
	}
	
	public void rotate(int rotation)
	{		
	    AffineTransform transform = new AffineTransform();
	    
	    transform.translate(img.getWidth() / 2, img.getHeight() / 2);
	    transform.rotate(Math.toRadians(rotation));
	    transform.translate(-img.getWidth() / 2, -img.getHeight() / 2);
	    
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	    img = op.filter(img, null);
	}

	public void render(Graphics2D g2d)
	{
		
	}
}
