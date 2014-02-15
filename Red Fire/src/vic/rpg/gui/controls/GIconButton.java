package vic.rpg.gui.controls;

import javax.media.opengl.GL2;

import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.render.DrawUtils;

import com.jogamp.opengl.util.texture.Texture;

public class GIconButton extends GControl
{
	private final Texture tNormal;
	private final Texture tHover;
	private final Texture tClick;
	
	private IGButton handler;
	public String name;
	
	public GIconButton(int xCoord, int yCoord, int width, int height, String name, Texture tNormal, Texture tHover, Texture tClick, IGButton handler) 
	{
		super(xCoord, yCoord, width, height);
		this.tNormal = tNormal;
		this.tHover = tHover;
		this.tClick = tClick;
		
		this.handler = handler;
		this.name = name;
	}

	@Override
	public void render(GL2 gl2, int x, int y) 
	{
		DrawUtils.setGL(gl2);
		super.render(gl2, x, y);
		
		if(this.mouseDown) DrawUtils.drawTexture(xCoord, yCoord, tClick);
		else if(this.mouseHovered) DrawUtils.drawTexture(xCoord, yCoord, tHover);
		else DrawUtils.drawTexture(xCoord, yCoord, tNormal);
	}

	@Override
	public void onClickReleased(int x, int y, int mouseButton) 
	{
		if(this.mouseHovered) handler.onButtonPressed(this);
	}
}
