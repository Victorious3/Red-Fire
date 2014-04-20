package vic.rpg.level.entity;

import org.jnbt.CompoundTag;

import vic.rpg.level.INBTReadWrite;
import vic.rpg.level.Level;
import vic.rpg.level.tiles.Tile;

//FIXME This has no actual purpose yet.
public class EntityTile implements INBTReadWrite
{
	public int xCoord;
	public int yCoord;
	public int layerID;
	public Level levelObj;
	
	@Override public void readFromNBT(CompoundTag tag, Object... args){}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		return tag;
	}
	
	/**
	 * Returns the {@link Tile} associated with this EntityTile.
	 * @return Tile
	 */
	public Tile getParent()
	{
		return levelObj.getTileAt(xCoord, yCoord, layerID);
	}
	
	/**
	 * Returns the Integer data associated with this EntityTile. Might be {@code null}.
	 * @return Integer
	 */
	public Integer getParentData()
	{
		return levelObj.getTileDataAt(xCoord, yCoord, layerID);
	}
}
