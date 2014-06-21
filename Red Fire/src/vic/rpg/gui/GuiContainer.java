package vic.rpg.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.media.opengl.GL2;

import vic.rpg.Game;
import vic.rpg.client.render.DrawUtils;
import vic.rpg.combat.SlotSkill;
import vic.rpg.item.ItemStack;
import vic.rpg.item.Slot;
import vic.rpg.item.SlotGrid;
import vic.rpg.registry.GameRegistry;
import vic.rpg.world.entity.living.Inventory;

/**
 * GuiContainer offers an extra layer of complexity to Gui. It is dependent if a {@link Slot}, {@link SlotGrid} or {@link SlotSkill} is used.
 * It contains an {@link Inventory} and a {@link Slot} which is used to transfer {@link ItemStack ItemStaks} between the slots of the given {@link Inventory}.
 * @author Victorious3
 */
public abstract class GuiContainer extends Gui 
{
	public boolean isSlotHovered = false;
	public Inventory inventory;
	
	public Slot currentSlot;
	
	@Override
	public void render(GL2 gl2) 
	{		
		if(inventory != null && currentSlot != null && !inventory.getItemStack(currentSlot.id).isEmpty()) Game.frame.setCursor(GameRegistry.CURSOR_DRAG);
		else if(isSlotHovered) Game.frame.setCursor(GameRegistry.CURSOR_DROP);	
		else Game.frame.setCursor(Cursor.getDefaultCursor());
		isSlotHovered = false;
		super.render(gl2);
		if(inventory != null && currentSlot != null && !inventory.getItemStack(currentSlot.id).isEmpty()) 
		{
			ItemStack stack = inventory.getItemStack(currentSlot.id);
			DrawUtils.drawTexture(GameRegistry.mouse.xCoord, GameRegistry.mouse.yCoord, stack.getItem().getTexture());
			DrawUtils.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
			if(stack.getStackSize() > 1) DrawUtils.drawString(GameRegistry.mouse.xCoord + stack.getItem().gridWidth * 30 - DrawUtils.getFormattedStringLenght(String.valueOf(stack.getStackSize())) - 2, GameRegistry.mouse.yCoord + stack.getItem().gridHeight * 30 - 2, String.valueOf(stack.getStackSize()), Color.black);
		}
	}

	public GuiContainer(boolean pauseGame, boolean overridesEsc) 
	{
		super(pauseGame, overridesEsc);
	}
	
	public GuiContainer(boolean pauseGame) 
	{
		super(pauseGame);
	}
	
	/**
	 * Sets the current {@link Inventory}. Should be called on {@link #initGui()}.
	 * @param inventory
	 */
	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
		currentSlot = new Slot(0, 0, -1, this);
		if(currentSlot.getItemStack() == null) currentSlot.setItemStack(new ItemStack());	
	}	
}
