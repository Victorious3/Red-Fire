package vic.rpg.editor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import vic.rpg.level.Level;
import vic.rpg.utils.Utils;

public class TileTextureSelector extends JLabel implements MouseListener
{
	private BufferedImage img;
	private int selectedTexture = 0;
	private int texHeight = 1;
	private int texWidth = 1;
	
	public TileTextureSelector(String path)
	{
		setImagePath(path);
		addMouseListener(this);
	}
	
	public void setImagePath(String path)
	{
		if(path == null) img = new BufferedImage(5 * 64, 10 * 64, BufferedImage.TYPE_INT_ARGB);
		else img = Utils.readImage(path);
		updateUI();
	}

	@Override
	public Dimension getPreferredSize() 
	{
		int width = getXAmount() * Level.CELL_SIZE;
		int height = getYAmount() * Level.CELL_SIZE;
		
		return new Dimension(width + 1, height + 1);
	}
	
	public void setTextureDimension(int width, int height)
	{
		this.texHeight = height;
		this.texWidth = width;
	}
	
	private int getXAmount()
	{
		return (int) (getParent().getSize().getWidth() / (float)Level.CELL_SIZE);
	}
	
	private int getYAmount()
	{
		float total = (img.getWidth() / Level.CELL_SIZE) * (img.getHeight() / Level.CELL_SIZE);
		return (int) Math.ceil(total / (float)getXAmount());
	}
	
	public int getSelectedTexture() 
	{
		return selectedTexture;
	}
	
	public void setSelectedTexture(int texture)
	{
		selectedTexture = texture;
	}
	
	public Point getSelectedTexturePoint()
	{
		return Utils.conv1Dto2DPoint(selectedTexture, img.getWidth() / Level.CELL_SIZE);
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		int i = 0;
		for(int y = 0; y < getYAmount(); y++)
		{
			for(int x = 0; x < getXAmount(); x++)
			{
				Point p = Utils.conv1Dto2DPoint(i, img.getWidth() / Level.CELL_SIZE);
				g2d.setColor(Color.black);
				if(p.x * Level.CELL_SIZE + Level.CELL_SIZE <= img.getWidth() && p.y * Level.CELL_SIZE + Level.CELL_SIZE <= img.getHeight()) 
				{
					g2d.drawImage(img.getSubimage(p.x * Level.CELL_SIZE, p.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE), null, x * Level.CELL_SIZE, y * Level.CELL_SIZE);
					g2d.drawRect(x * Level.CELL_SIZE, y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
				}
				i++;
			}
		}
		
		g2d.setColor(new Color(0, 0, 255, 100));
		int selectedTexture = this.selectedTexture;
		Point p2 = Utils.conv1Dto2DPoint(selectedTexture, getXAmount());
		g2d.fillRect(p2.x * Level.CELL_SIZE, p2.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
		
		Point p3 = Utils.conv1Dto2DPoint(selectedTexture, (img.getWidth() / Level.CELL_SIZE));
		if((texWidth > 1 || texHeight > 1) && (p3.x + texWidth) * Level.CELL_SIZE <= img.getWidth() && (p3.y - texHeight) * Level.CELL_SIZE >= -Level.CELL_SIZE)
		{
			for(int x = 0; x < texWidth; x++)
			{
				for(int y = 0; y < texHeight; y++)
				{
					selectedTexture = this.selectedTexture + x;
					selectedTexture -= y * (img.getWidth() / Level.CELL_SIZE);
					Point p4 = Utils.conv1Dto2DPoint(selectedTexture, getXAmount());
					
					if(x > 0 || y > 0)
					{				
						g2d.setColor(new Color(0, 155, 255, 100));
						g2d.fillRect(p4.x * Level.CELL_SIZE, p4.y * Level.CELL_SIZE, Level.CELL_SIZE, Level.CELL_SIZE);
					}
					
					g2d.setColor(Color.black);
					if(x == 0 && y == 0)
					{
						g2d.fillPolygon(new int[]{p4.x * Level.CELL_SIZE, p4.x * Level.CELL_SIZE, p4.x * Level.CELL_SIZE + 10}, new int[]{p4.y * Level.CELL_SIZE + Level.CELL_SIZE - 10, p4.y * Level.CELL_SIZE + Level.CELL_SIZE, p4.y * Level.CELL_SIZE + Level.CELL_SIZE}, 3);
					}
					if(x == 0 && y == texHeight - 1)
					{
						g2d.fillPolygon(new int[]{p4.x * Level.CELL_SIZE, p4.x * Level.CELL_SIZE, p4.x * Level.CELL_SIZE + 10}, new int[]{p4.y * Level.CELL_SIZE + 10, p4.y * Level.CELL_SIZE, p4.y * Level.CELL_SIZE}, 3);
					}
					if(x == texWidth - 1 && y == texHeight - 1)
					{
						g2d.fillPolygon(new int[]{p4.x * Level.CELL_SIZE + Level.CELL_SIZE, p4.x * Level.CELL_SIZE + Level.CELL_SIZE, p4.x * Level.CELL_SIZE + Level.CELL_SIZE- 10}, new int[]{p4.y * Level.CELL_SIZE + 10, p4.y * Level.CELL_SIZE, p4.y * Level.CELL_SIZE}, 3);
					}
					if(x == texWidth - 1 && y == 0)
					{
						g2d.fillPolygon(new int[]{p4.x * Level.CELL_SIZE + Level.CELL_SIZE, p4.x * Level.CELL_SIZE + Level.CELL_SIZE, p4.x * Level.CELL_SIZE + Level.CELL_SIZE- 10}, new int[]{p4.y * Level.CELL_SIZE + Level.CELL_SIZE - 10, p4.y * Level.CELL_SIZE + Level.CELL_SIZE, p4.y * Level.CELL_SIZE + Level.CELL_SIZE}, 3);
					}
				}
			}
		}
		
		super.paintComponent(g);
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		int x = e.getX() / Level.CELL_SIZE;
		int y = e.getY() / Level.CELL_SIZE;
		
		if(x < getXAmount())
		{
			int sNew = Utils.conv2Dto1Dint(x, y, getXAmount());	
			if(sNew < (img.getWidth() / Level.CELL_SIZE) * (img.getHeight() / Level.CELL_SIZE)) selectedTexture = sNew;
			updateUI();
		}
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
}
