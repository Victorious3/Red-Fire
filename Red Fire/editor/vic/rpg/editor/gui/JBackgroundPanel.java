package vic.rpg.editor.gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class JBackgroundPanel extends JPanel
{
	private Image image;
	
	public JBackgroundPanel(Image image)
	{
		this.image = image;
	}
	
	@Override
	public void paintComponent(Graphics g) 
	{
        super.paintComponent(g);

        int iw = image.getWidth(this);
        int ih = image.getHeight(this);
        if (iw > 0 && ih > 0) 
        {
            for (int x = 0; x < getWidth(); x += iw) 
            {
                for (int y = 0; y < getHeight(); y += ih) 
                {
                    g.drawImage(image, x, y, iw, ih, this);
                }
            }
        }
    } 
}

