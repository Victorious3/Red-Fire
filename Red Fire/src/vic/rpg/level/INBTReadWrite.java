package vic.rpg.level;

import org.jnbt.CompoundTag;

/**
 * INBTReadWrite does provide for read and write a {@link CompoundTag}.
 * @author Victorious3
 */
public interface INBTReadWrite
{
	/**
	 * Read from a {@link CompoundTag}.
	 * @param tag
	 * @param args
	 */
	public void readFromNBT(CompoundTag tag, Object... args);
	
	/**
	 * Write to a {@link CompoundTag}.
	 * @param tag
	 * @param args
	 */
	public CompoundTag writeToNBT(CompoundTag tag, Object... args);
}
