package vic.rpg.gui.controls;


public class GSlider extends GControl {

	public float minS;
	public float maxS;
	
	public float returnValue;
	public int mode;
	
	public float xScroll = 0.0F;
	private boolean isScrolling = false;
	
	public static final int MODE_INT = 0;
	public static final int MODE_FLOAT = 1;
	public static final int MODE_PERCENT = 2;
	
	public GSlider(int xCoord, int yCoord, int width, int height, float minS, float maxS, float xScroll, int mode) 
	{
		super(xCoord, yCoord, width, height);
		
		this.minS = minS;
		this.maxS = maxS;
		this.mode = mode;
		
		this.xScroll = xScroll; 
	}

	//FIXME Update to OpenGl - Too lazy
	/*@Override
	public void render(GL2 gl2, int x, int y) 
	{
		gl2.setColor(new Color(97, 74, 119, 190));
		gl2.fillRect(xCoord, yCoord, width, height);
		
		if(this.mouseDown)gl2.setColor(new Color(158, 31, 74));
		else if(this.mouseHovered)gl2.setColor(new Color(130, 91, 213));
		else gl2.setColor(new Color(68, 21, 150));
		gl2.fillRect(xCoord + (int)((float)(width - 17) * this.xScroll), yCoord, 16, height);
		
		gl2.setColor(new Color(120, 31, 0));		
		Stroke oldStroke = gl2.getStroke();
		gl2.setStroke(new BasicStroke(3.0F));		
		gl2.drawRect(xCoord, yCoord, width, height);
		gl2.setStroke(oldStroke);
		
		String s = null;
		
		switch(mode)
		{
		case GSlider.MODE_INT : s = String.valueOf((int)returnValue); break; 
		case GSlider.MODE_FLOAT : s = String.valueOf(Math.round(returnValue * 100) / 100); break;
		case GSlider.MODE_PERCENT : s = String.valueOf((int)returnValue) + "%"; break;  
		}
		
		gl2.setColor(Color.WHITE);
		gl2.setFont(new Font("Monospaced", 2, 18));
		
		FontMetrics fm = gl2.getFontMetrics();		
		int sWidth = fm.stringWidth(String.valueOf((int)returnValue));
		int sHeight = fm.getHeight() / 2;		
		gl2.drawString(s, xCoord + (width - sWidth)/2, yCoord + (height + sHeight)/2);

		if(isScrolling)
		{
			xScroll = (float)(x - xCoord) / (float)(width);
			
			if(xScroll < 0.0F) xScroll = 0.0F;
			if(xScroll > 1.0F) xScroll = 1.0F;
		}
				
		returnValue = returnValue(minS, maxS, xScroll);
	}

	@Override
	public void onClickStart(int x, int y, int mouseButton) 
	{
		this.isScrolling = true;
	}

	@Override
	public void onClickReleased(int x, int y, int mouseButton) 
	{
		this.isScrolling = false;
	}
	
	public static float returnValue(float minS, float maxS, float xScroll)
	{
		return (maxS - xScroll)* xScroll + minS;
	}
	*/
}
