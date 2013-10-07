package vic.rpg.editor.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JLabel;

import vic.rpg.editor.listener.BrushFrameListener;
import vic.rpg.editor.tiles.TileMaterial;
import vic.rpg.level.Level;
import vic.rpg.utils.Direction;

public class LabelEditMaterials extends JLabel implements MouseListener
{
	private boolean[][] sites;
	private TileMaterial material;
	private TileMaterial subMaterial;
	
	public LabelEditMaterials()
	{
		this.setPreferredSize(new Dimension(BrushFrameListener.terrainImg.getWidth(), BrushFrameListener.terrainImg.getHeight()));
		sites = new boolean[BrushFrameListener.terrainImg.getWidth() / Level.CELL_SIZE * 2][BrushFrameListener.terrainImg.getHeight() / Level.CELL_SIZE * 2];
		this.setFocusable(true);
		this.setRequestFocusEnabled(true);
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
	
	public void clear()
	{
		for(int i = 0; i < sites.length; i++)
		{
			boolean[] bool = sites[i];
			for(int j = 0; j < bool.length; j++)
			{
				sites[i][j] = false;		
			}		
		}
		updateUI();
	}
	
	public void setMaterials(TileMaterial material, TileMaterial subMaterial)
	{
		this.material = material;
		this.subMaterial = subMaterial; 
	}
	
	public TileMaterial getMaterial()
	{
		return material;
	}
	
	public TileMaterial getSubMaterial()
	{
		return subMaterial;
	}
	
	public boolean saveMaterial()
	{
		if(material == null) return true;		
		for(int i = 0; i < sites.length; i+= 2)
		{
			for(int j = 0; j < sites[i].length; j+= 2)
			{
				boolean[] b = new boolean[4];
				b[0] = sites[i][j];
				b[1] = sites[i + 1][j];
				b[2] = sites[i][j + 1];
				b[3] = sites[i + 1][j + 1];
				
				Direction d = null;
				if(Arrays.equals(b, new boolean[]{false, false, true, true})) d = Direction.NORTH;
				else if(Arrays.equals(b, new boolean[]{true, false, true, false})) d = Direction.EAST;
				else if(Arrays.equals(b, new boolean[]{true, true, false, false})) d = Direction.SOUTH;
				else if(Arrays.equals(b, new boolean[]{false, true, false, true})) d = Direction.WEST;
				else if(Arrays.equals(b, new boolean[]{false, false, true, false})) d = Direction.NORTH_EAST;
				else if(Arrays.equals(b, new boolean[]{true, false, false, false})) d = Direction.SOUTH_EAST;
				else if(Arrays.equals(b, new boolean[]{false, true, false, false})) d = Direction.SOUTH_WEST;
				else if(Arrays.equals(b, new boolean[]{false, false, false, true})) d = Direction.NORTH_WEST;
				else if(Arrays.equals(b, new boolean[]{true, true, true, true})) d = Direction.CENTER;
				else if(!Arrays.equals(b, new boolean[]{false, false, false, false}))
				{
					return false;
				}
				
				if(d != null) material.setTextureCoord(subMaterial, new Point(i / 2, j / 2), d);
			}
		}		
		return true;
	}
	
	public void setDirection(Direction d, int x, int y)
	{
		boolean[] b = new boolean[4];
		
		switch(d)
		{
		case NORTH:			b = new boolean[]{false, false, true, true}; 	break;
		case EAST: 			b = new boolean[]{true, false, true, false}; 	break;
		case SOUTH: 		b = new boolean[]{true, true, false, false}; 	break;		
		case WEST: 			b = new boolean[]{false, true, false, true}; 	break;
		case NORTH_EAST: 	b = new boolean[]{false, false, true, false}; 	break;
		case SOUTH_EAST: 	b = new boolean[]{true, false, false, false}; 	break;
		case SOUTH_WEST: 	b = new boolean[]{false, true, false, false}; 	break;
		case NORTH_WEST:	b = new boolean[]{false, false, false, true}; 	break;
		case CENTER:		b = new boolean[]{true, true, true, true}; 		break;
		}
		
		sites[x * 2][y * 2] = b[0];
		sites[x * 2 + 1][y * 2] = b[1];
		sites[x * 2][y * 2 + 1] = b[2];
		sites[x * 2 + 1][y * 2 + 1] = b[3];
		
		updateUI();
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}
	
	@Override 
	public void mousePressed(MouseEvent e) 
	{
		int x = e.getX() / (Level.CELL_SIZE / 2);
		int y = e.getY() / (Level.CELL_SIZE / 2);
		
		if(e.getButton() == MouseEvent.BUTTON3) sites[x][y] = false;
		else if(e.getButton() == MouseEvent.BUTTON1) sites[x][y] = true;
		
		this.updateUI();
	}
	
	@Override public void mouseReleased(MouseEvent e) {}	
}
