package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.gui.controls.GButton;
import vic.rpg.gui.controls.GButton.IGButton;
import vic.rpg.gui.controls.GControl;
import vic.rpg.registry.RenderRegistry;
import vic.rpg.render.DrawUtils;
import vic.rpg.render.TextureLoader;
import vic.rpg.utils.Utils;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.texture.Texture;

public class GuiError extends Gui implements IGButton
{
	private static Texture tex = TextureLoader.requestTexture(Utils.readImageFromJar("/vic/rpg/resources/error.png"));
	
	public GuiError() 
	{
		super(true, true);
	}

	@Override
	public void render(GL2 gl2) 
	{		
		DrawUtils.setGL(gl2);
		DrawUtils.drawTexture(0, 0, tex);
		DrawUtils.fillRect(0, 0, Game.WIDTH, Game.HEIGHT, new Color(80, 80, 80, 180));
		super.render(gl2);
		DrawUtils.setFont(RenderRegistry.RPGFont.deriveFont(50.0F));
		DrawUtils.drawString(Game.WIDTH / 2 - 320, Game.HEIGHT / 2 - 200, "Sorry a wild Exception appeared", Color.white);
		String lines[] = Game.netHandler.lastError.split("\n");
		DrawUtils.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
		int i = 0;
		for(String line : lines)
		{
			DrawUtils.drawUnformattedString(85, 170 + i * 16, line, Color.white);
			i++;
		}
	}

	@Override
	public void initGui() 
	{
		controlsList.add(new GButton(Game.WIDTH / 2 - 60, Game.HEIGHT / 2 + 200, 120, 30, this, "OK", "OK"));
		super.initGui();
	}

	@Override
	public void onButtonPressed(GControl button) 
	{
		Gui.setGui(new GuiMain());;
	}

	@Override
	public void onKeyTyped(char k, int keyCode) 
	{
		if(keyCode == KeyEvent.VK_ESCAPE) Gui.setGui(new GuiMain());
	}	
}
