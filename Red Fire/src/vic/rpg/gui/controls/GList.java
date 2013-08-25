package vic.rpg.gui.controls;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import vic.rpg.render.DrawUtils;

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
	public void render(GL2 gl2, int x, int y) 
	{
		DrawUtils.setGL(gl2);
		DrawUtils.fillRect(xCoord, yCoord, width, height, new Color(97, 74, 119, 190));
		DrawUtils.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		
		DrawUtils.startClip(xCoord, yCoord, width, height);
		int offset = (int) (scrollPos * maxOffset);
		
		for(int i = 0; i < data.size(); i++)
		{
			int i2 = offset + yCoord + 20 + i * (elementHeight + 5);
			
			Object o = data.get(i);
			if(this.selectedPos == i) DrawUtils.fillRect(xCoord + 20, i2, width - 80, elementHeight, new Color(158, 31, 74));
			else DrawUtils.fillRect(xCoord + 20, i2, width - 80, elementHeight, new Color(68, 21, 150));

			DrawUtils.drawRect(xCoord + 20, i2, width - 80, elementHeight, new Color(120, 31, 0));
			DrawUtils.drawString(xCoord + 25, i2 + (elementHeight + DrawUtils.getFont().getSize()) / 2, o.toString(), Color.white);
		}
		DrawUtils.endClip();
		
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

		if(!this.isScrollingEnabled) DrawUtils.fillRect(x2, y2, 31, 60, new Color(60, 60, 60));
		else if(this.isScrolling) DrawUtils.fillRect(x2, y2, 31, 60, new Color(158, 31, 74));
		else DrawUtils.fillRect(x2, y2, 31, 60, new Color(158, 31, 74));	

		float thickness = 3;
		float oldThickness = DrawUtils.getLineWidth();
		DrawUtils.setLineWidth(thickness);		
		DrawUtils.drawRect(xCoord, yCoord, width, height, new Color(120, 31, 0));
		DrawUtils.drawLine(xCoord + width - 40, yCoord, xCoord + width - 40, yCoord + height, new Color(120, 31, 0));
		DrawUtils.setLineWidth(oldThickness);		
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
			
			if(time - lastClickTime < 500 && this.selectedPos >= 0 && this.selectedPos < data.size())
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
