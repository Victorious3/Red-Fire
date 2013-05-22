package vic.rpg.gui;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.RescaleOp;

import vic.rpg.Game;
import vic.rpg.item.Slot;
import vic.rpg.registry.GameRegistry;

public class IGuiContainer extends Gui 
{
	public boolean isSlotHovered = false;
	
	@Override
	public void render(Graphics2D g2d) 
	{
		super.render(g2d);
		
		if(currentSlot != null) Game.frame.setCursor(GameRegistry.CURSOR_DRAG);
		else if(isSlotHovered) Game.frame.setCursor(GameRegistry.CURSOR_DROP);	
		else Game.frame.setCursor(Cursor.getDefaultCursor());
		isSlotHovered = false;
		
		if(currentSlot != null) g2d.drawImage(currentSlot.item.img, new RescaleOp(new float[]{1.0f, 1.0f, 1.0f, 0.8f}, new float[]{0f, 0f, 0f, -20f}, null), GameRegistry.mouse.xCoord - 15, GameRegistry.mouse.yCoord - 15);
	}

	public IGuiContainer(boolean pauseGame, boolean overridesEsc) 
	{
		super(pauseGame, overridesEsc);
	}
	
	public IGuiContainer(boolean pauseGame) 
	{
		super(pauseGame);
	}
	
	public Slot currentSlot;

}
