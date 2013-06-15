package vic.rpg.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import vic.rpg.utils.Utils;

public class LightSource 
{
	public static Image baseImage = Utils.readImageFromJar("/vic/rpg/resources/light.png");
	
	private Image img;
	public int width;
	
	public LightSource(int width, float brightness, Color color)
	{
		if(Utils.getSide().equals(Utils.SIDE_CLIENT))
		{
			BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = (Graphics2D) img.getGraphics();
			g2d.scale(width / baseImage.getWidth(null), width / baseImage.getHeight(null));
			g2d.setColor(color);
			g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
			g2d.setComposite(AlphaComposite.DstIn);
			g2d.drawImage(baseImage, 0, 0, null);
			
			new RescaleOp(brightness, 0, null).filter(img, img);
			
			this.img = img;
			this.width = width;
		}
	}
	
	public Image getImage()
	{
		return img;
	}
}
