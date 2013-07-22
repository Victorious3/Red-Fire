package vic.rpg.gui.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

public class GList extends GControl 
{
	private int elementHeight;
	private ArrayList<? extends Object> data;
	private boolean isScrolling = false;
	private float scrollPos = 0.0F;
	private boolean isScrollingEnabled;
	private int selectedPos;
	private IGList handler;
	private int maxOffset = 0;
	
	public GList(int xCoord, int yCoord, int width, int height, int elementHeight, ArrayList<? extends Object> data, IGList handler) 
	{
		super(xCoord, yCoord, width, height);
		this.elementHeight = elementHeight;
		this.data = data;
		this.handler = handler;
		if(((data.size() + 1) * (elementHeight + 5) - 40) >= height)
		{
			isScrollingEnabled = true;
			maxOffset = height - data.size() * (elementHeight + 5) - 40; 
		}
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) 
	{
		g2d.setColor(new Color(97, 74, 119, 190));
		g2d.fillRect(xCoord, yCoord, width, height);
		
		g2d.setClip(xCoord, yCoord, width, height);	
		
		int offset = (int) (scrollPos * maxOffset);
		
		for(int i = 0; i < data.size(); i++)
		{
			int i2 = offset + yCoord + 20 + i * (elementHeight + 5);
			
			Object o = data.get(i);
			g2d.setColor(new Color(68, 21, 150));
			if(this.selectedPos == i) g2d.setColor(new Color(158, 31, 74));
			g2d.fillRect(xCoord + 20, i2, width - 80, elementHeight);
			g2d.setColor(new Color(120, 31, 0));
			g2d.drawRect(xCoord + 20, i2, width - 80, elementHeight);
			g2d.setColor(Color.white);
			g2d.drawString(o.toString(), xCoord + 25, i2 + (elementHeight + g2d.getFont().getSize()) / 2);
		}
		g2d.setClip(null);
		
		int x2 = xCoord + width - 35;
		int y2 = (int) (yCoord + 5 + scrollPos * (height - 69));
		
		if(this.isScrollingEnabled)
		{
			if((x >= x2 && x <= x2 + 31 && this.mouseDown) || this.isScrolling)
			{
				scrollPos = (float)(y - yCoord - 5) / (float)(height - 10);
				scrollPos = scrollPos > 1.0F ? 1.0F : scrollPos < 0.0F ? 0.0F : scrollPos;
				if(y >= y2 && y <= y2 + 60) this.isScrolling = true;
			}
		}

		if(!this.isScrollingEnabled) g2d.setColor(new Color(60, 60, 60));
		else if(this.isScrolling) g2d.setColor(new Color(158, 31, 74));
		else g2d.setColor(new Color(68, 21, 150));
			
		g2d.fillRect(x2, y2, 31, 60);

		g2d.setColor(new Color(120, 31, 0));		
		float thickness = 3;
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(thickness));		
		g2d.drawRect(xCoord, yCoord, width, height);
		g2d.drawLine(xCoord + width - 40, yCoord, xCoord + width - 40, yCoord + height);
		g2d.setStroke(oldStroke);
	}

	long lastClickTime = System.currentTimeMillis();
	
	@Override
	public void onClickReleased(int x, int y) 
	{
		long time = System.currentTimeMillis();
		
		if(x >= xCoord + 20 && x <= xCoord + 20 + width - 80)
		{
			int offset = (int) (scrollPos * maxOffset);
			this.selectedPos = (y - 20 - yCoord - offset) / (this.elementHeight + 5);
			
			if(time - lastClickTime < 500)
			{				
				handler.onElementDoubleClick(this, data.get(this.selectedPos));
			}
			
			lastClickTime = time;
		}
		
		this.isScrolling = false;
	}

	@Override
	public void onMouseWheelMoved(int amount) 
	{
		if(isScrollingEnabled)
		{
			this.scrollPos += amount * ((float)(height - 40) / (float)((data.size() + 1) * (elementHeight + 5)) / 33F);
			scrollPos = scrollPos > 1.0F ? 1.0F : scrollPos < 0.0F ? 0.0F : scrollPos;
		}
	}
	
	public static interface IGList
	{
		public void onElementDoubleClick(GList glist, Object element);
	}
}
