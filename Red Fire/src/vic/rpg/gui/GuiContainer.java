package vic.rpg.gui;

import java.awt.Cursor;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.item.Slot;
import vic.rpg.level.entity.living.Inventory;
import vic.rpg.registry.GameRegistry;
import vic.rpg.render.DrawUtils;

public class GuiContainer extends Gui 
{
	public boolean isSlotHovered = false;
	public Inventory inventory;
	
	@Override
	public void render(GL2 gl2) 
	{		
		if(currentSlot != null) Game.frame.setCursor(GameRegistry.CURSOR_DRAG);
		else if(isSlotHovered) Game.frame.setCursor(GameRegistry.CURSOR_DROP);	
		else Game.frame.setCursor(Cursor.getDefaultCursor());
		isSlotHovered = false;
		
		DrawUtils.setGL(gl2);
		super.render(gl2);
		if(currentSlot != null) DrawUtils.drawTexture(GameRegistry.mouse.xCoord - 15, GameRegistry.mouse.yCoord - 15, currentSlot.item.getTexture());
	}
	
	public GuiContainer(boolean pauseGame, boolean overridesEsc) 
	{
		super(pauseGame, overridesEsc);
	}
	
	public GuiContainer(boolean pauseGame) 
	{
		super(pauseGame);
	}
	
	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
	}
	
	public Slot currentSlot;

}
