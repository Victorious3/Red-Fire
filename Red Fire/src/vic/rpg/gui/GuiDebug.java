package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.registry.GameRegistry;
import vic.rpg.render.DrawUtils;

public class GuiDebug extends Gui 
{

	public GuiDebug() 
	{
		super(false);
	}

	@Override
	public void render(GL2 gl2) 
	{
		DrawUtils.setGL(gl2);
		DrawUtils.setFont(new Font("Veranda", 0, 20));
		DrawUtils.drawString(5, 20, (int)Game.game.GL_ANIMATOR.getLastFPS() + " FPS", Color.white);
		DrawUtils.drawString(5, 560, GameRegistry.mouse.xCoord + " " + GameRegistry.mouse.yCoord, Color.white);
		DrawUtils.drawString(550, 560, "M1:" + GameRegistry.mouse.isLeftDown + " M2:" + GameRegistry.mouse.isRightDown + " M3:" + GameRegistry.mouse.isMiddleDown, Color.white);
		DrawUtils.drawString(650, 20, "char:" + GameRegistry.key.currChar + " key:" + GameRegistry.key.currKey, Color.white);
	}

}
