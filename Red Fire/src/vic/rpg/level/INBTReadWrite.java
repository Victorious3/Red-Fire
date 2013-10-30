package vic.rpg.level;

import org.jnbt.CompoundTag;

public interface INBTReadWrite
{
	public void readFromNBT(CompoundTag tag, Object... args);
	
	public CompoundTag writeToNBT(CompoundTag tag, Object... args);
}
