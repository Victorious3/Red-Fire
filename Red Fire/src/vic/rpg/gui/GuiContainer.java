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
		if(inventory != null && currentSlot != null && inventory.getItem(currentSlot.id) != null) Game.frame.setCursor(GameRegistry.CURSOR_DRAG);
		else if(isSlotHovered) Game.frame.setCursor(GameRegistry.CURSOR_DROP);	
		else Game.frame.setCursor(Cursor.getDefaultCursor());
		isSlotHovered = false;
		
		DrawUtils.setGL(gl2);
		super.render(gl2);
		if(inventory != null && currentSlot != null && inventory.getItem(currentSlot.id) != null) DrawUtils.drawTexture(GameRegistry.mouse.xCoord - 15, GameRegistry.mouse.yCoord - 15, inventory.getItem(currentSlot.id).getTexture());
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
		currentSlot = new Slot(0, 0, -1, this);
	}
	
	public Slot currentSlot;
}
