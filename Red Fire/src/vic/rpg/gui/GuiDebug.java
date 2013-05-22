package vic.rpg.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import vic.rpg.Game;
import vic.rpg.registry.GameRegistry;

public class GuiDebug extends Gui 
{

	public GuiDebug() 
	{
		super(false);
	}

	@Override
	public void render(Graphics2D g2d) 
	{
		g2d.setFont(new Font("Veranda", 0, 20));
		g2d.setColor(Color.white);
		g2d.drawString(Game.fps + " FPS", 5, 20);
		g2d.drawString(GameRegistry.mouse.xCoord + " " + GameRegistry.mouse.yCoord, 5, 560);
		g2d.drawString("M1:" + GameRegistry.mouse.isLeftDown + " M2:" + GameRegistry.mouse.isRightDown + " M3:" + GameRegistry.mouse.isMiddleDown, 550, 560);
		g2d.drawString("char:" + GameRegistry.key.currChar + " key:" + GameRegistry.key.currKey, 650, 20);
	}

}
