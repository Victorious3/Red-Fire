package vic.rpg.world.entity.tile;

import org.jnbt.CompoundTag;

import vic.rpg.utils.Logger;

public class EntityTileChest extends EntityTile
{
	@Override
	public void readFromNBT(CompoundTag tag, Object... args) 
	{
		Logger.log(tag.getString("Test", "Test failed D:"));
		super.readFromNBT(tag, args);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		tag.putString("Test", "Just a test.");
		return super.writeToNBT(tag, args);
	}	
}
