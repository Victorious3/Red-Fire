package vic.rpg.render;

import java.awt.Image;
import java.util.ArrayList;

public class TextureFX 
{
	private Image[] data;
	private int tickRate;
	public boolean hasUpdated = true;
	public Image currImage;
	
	private static ArrayList<TextureFX> images = new ArrayList<TextureFX>();
	
	public TextureFX(Image[] data, int tickRate)
	{
		this.data = data;
		this.tickRate = tickRate;
		
		currImage = data[0];
		images.add(this);
	}
	
	int tickPointer = 0;
	int imgPointer = 0;
	
	public void tick()
	{
		hasUpdated = false;
		
		tickPointer++;
		
		if(tickPointer == tickRate)
		{
			imgPointer++;
			if(imgPointer >= data.length) imgPointer = 0;
			
			currImage = data[imgPointer];
			hasUpdated = true;
			
			tickPointer = 0;
		}
	}
	
	public static void tickAll()
	{
		for(TextureFX tex : images)
		{
			tex.tick();
		}
	}
}
