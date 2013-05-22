package vic.rpg.gui.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;

import vic.rpg.Game;

public class GTextField extends GControl
{
	public boolean activated = false;
	public String text = "";
	public String plaintext = "";
	public int maxLenght = 0;
	public boolean returnPressed = false;
	public boolean loosesFocus = true;
	
	private int wdt = 0;
	private int wdt1 = 0;
	
	public GTextField(int xCoord, int yCoord, int width, int height, int maxLenght, boolean loosesFocus) 
	{
		super(xCoord, yCoord, width, height);
		this.maxLenght = maxLenght;
		this.loosesFocus = loosesFocus; 
		if(!loosesFocus)
		{
			activate(true);
		}
	}

	@Override
	public void render(Graphics2D g2d, int x, int y) 
	{
		g2d.setColor(Color.black);
		g2d.fillRect(xCoord, yCoord, width, height);
		
		g2d.setColor(Color.lightGray);
		if(this.activated) g2d.setColor(Color.white);
		float thickness = 3;
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(thickness));		
		g2d.drawRect(xCoord, yCoord, width, height);
		g2d.setStroke(oldStroke);
		
		g2d.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		FontMetrics metrics = g2d.getFontMetrics();
		
		wdt = metrics.stringWidth(text);
		wdt1 = metrics.stringWidth(" ");
		
		g2d.setColor(Color.white);
		g2d.drawString(text, xCoord + 5, yCoord + height - 6);
	}

	@Override
	public void onClickStart(int x, int y) 
	{		
		activate(true);		
		if(Game.thePlayer != null)
		{
			Game.thePlayer.isWalkingBlocked = true;
			Game.thePlayer.isWalking = false;
		}
	}

	@Override
	public void onClickEnd(int x, int y) 
	{		
		if(loosesFocus)
		{
			activate(false);		
		}			
		if(Game.thePlayer != null) Game.thePlayer.isWalkingBlocked = false;		
	}
	
	public GTextField activate(boolean b) 
	{
		if(!activated && b) text += "_";
		if(activated && !b) text = text.substring(0, text.length() - 1).intern();
		activated = b;
		return this;
	}

	@Override
	public void onKeyPressed(char k, int keyCode) 
	{
		if(activated)
		{					
			text = text.substring(0, text.length() - 1).intern();
			
			if(keyCode == KeyEvent.VK_ENTER)
			{
				returnPressed = true;
				text += "_";
				return;
			}
			if(keyCode == KeyEvent.VK_ESCAPE)
			{
				text += "_";
				return;
			}		
			if(keyCode == KeyEvent.VK_BACK_SPACE)
			{
				if(text.length() >= 1)
				{									
					plaintext = plaintext.substring(0, plaintext.length() - 1).intern(); 
					
					if(plaintext.length() + 1 > text.length())
					{
						text = text.substring(0, text.length() - 1).intern();
						text = plaintext.substring(plaintext.length() - text.length() - 1, plaintext.length() - text.length()).intern() + text;
					}
					else text = text.substring(0, text.length() - 1).intern();
				}						
				text += "_";
				return;
			}
			
			if(Character.isDefined(k) && plaintext.length() < maxLenght)
			{
				text += k;
				plaintext += k;
				
				if(wdt + wdt1 * 2 >= width)
				{
					text = text.substring(1, text.length()).intern(); 
				}
			}
			
			if(keyCode == KeyEvent.VK_DELETE)
			{
				text = "";
				plaintext = "";
			}
			
			text += "_";
		}
	}
	
	public void clear()
	{
		this.plaintext = "";
		this.text = "_";
	}
	
	public GTextField setText(String text)
	{
		this.text = text;
		this.plaintext = text;
		
		return this;
	}
}
