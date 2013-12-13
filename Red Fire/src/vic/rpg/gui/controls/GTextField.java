package vic.rpg.gui.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.render.DrawUtils;

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
	public void render(GL2 gl2, int x, int y) 
	{
		DrawUtils.setGL(gl2);
		DrawUtils.fillRect(xCoord, yCoord, width, height, Color.black);
		
		float thickness = 3;
		float oldThickness = DrawUtils.getLineWidth();
		DrawUtils.setLineWidth(thickness);		
		
		if(this.activated) DrawUtils.drawRect(xCoord, yCoord, width, height, Color.white);
		else DrawUtils.drawRect(xCoord, yCoord, width, height, Color.LIGHT_GRAY); 
		DrawUtils.setLineWidth(oldThickness);
		
		DrawUtils.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		
		wdt = (int) DrawUtils.getTextRenderer().getBounds(text).getWidth();
		wdt1 = (int) DrawUtils.getTextRenderer().getBounds(" ").getWidth();
		
		DrawUtils.drawUnformattedString(xCoord + 5, yCoord + height - 6, text, Color.white);
	}

	@Override
	public void onClickStart(int x, int y, int mouseButton) 
	{		
		activate(true);		
		if(Game.getPlayer() != null)
		{
			Game.getPlayer().isWalkingBlocked = true;
			Game.getPlayer().setWalking(false);
		}
	}

	@Override
	public void onClickEnd(int x, int y) 
	{		
		if(loosesFocus)
		{
			activate(false);		
		}			
		if(Game.getPlayer() != null) Game.getPlayer().isWalkingBlocked = false;		
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
