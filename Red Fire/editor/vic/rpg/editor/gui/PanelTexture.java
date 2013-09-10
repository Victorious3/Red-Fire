package vic.rpg.editor.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;

public class PanelTexture extends JPanel
{
	protected Image texture;
	protected int textureID;
	
	public PanelTexture(Image texture)
	{
		this.texture = texture;
	}
	
	public void setTexture(Image texture)
	{
		this.texture = texture;
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		float scale1 = (float)getSize().width / (float)texture.getWidth(null);
		float scale2 = (float)getSize().height / (float)texture.getHeight(null);
		
		float scale = scale1 > scale2 ? scale2 : scale1;
	
		int width = (int)((float)texture.getWidth(null) * scale);
		int height = (int)((float)texture.getHeight(null) * scale);
		
		int x = (int) (width < getSize().width ? getSize().width / 2F - width / 2F : 0); 
		int y = (int) (height < getSize().height ? getSize().height / 2F - height / 2F : 0); 
				
		g2d.drawImage(texture, x, y, width, height, null);
	}	
}
