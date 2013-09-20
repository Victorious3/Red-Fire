package vic.rpg.editor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import vic.rpg.editor.listener.BrushFrameListener;
import vic.rpg.level.Level;

public class LabelEditMaterials extends JLabel implements MouseListener
{
	private boolean[][] sites;
	
	public LabelEditMaterials()
	{
		this.setPreferredSize(new Dimension(BrushFrameListener.terrainImg.getWidth(), BrushFrameListener.terrainImg.getHeight()));
		sites = new boolean[BrushFrameListener.terrainImg.getWidth() / Level.CELL_SIZE * 2][BrushFrameListener.terrainImg.getHeight() / Level.CELL_SIZE * 2];
		this.setFocusable(true);
		this.addMouseListener(this);		
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.drawImage(BrushFrameListener.terrainImg, null, 0, 0);
		
		g2d.setColor(Color.black);
		for(int x = 0; x < BrushFrameListener.terrainImg.getWidth() / Level.CELL_SIZE; x++)
		{
			g2d.drawLine(x * Level.CELL_SIZE, 0, x * Level.CELL_SIZE, BrushFrameListener.terrainImg.getHeight());
			for(int y = 0; y < BrushFrameListener.terrainImg.getHeight() / Level.CELL_SIZE; y++)
			{
				g2d.drawLine(0, y * Level.CELL_SIZE, BrushFrameListener.terrainImg.getWidth(), y * Level.CELL_SIZE);
			}
		}
		
		g2d.setColor(new Color(0, 155, 255, 100));
		for(int x = 0; x < sites.length; x++)
		{
			for(int y = 0; y < sites[0].length; y++)
			{
				if(sites[x][y]) g2d.fillRect(x * Level.CELL_SIZE / 2, y * Level.CELL_SIZE / 2, Level.CELL_SIZE / 2, Level.CELL_SIZE / 2);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int x = e.getX() / (Level.CELL_SIZE / 2);
		int y = e.getY() / (Level.CELL_SIZE / 2);
		
		if(sites[x][y]) sites[x][y] = false;
		else sites[x][y] = true;
		
		this.updateUI();
	}

	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}	
}
