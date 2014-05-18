package vic.rpg.world.entity;

import org.jnbt.CompoundTag;

import vic.rpg.world.INBTReadWrite;
import vic.rpg.world.Map;
import vic.rpg.world.tiles.Tile;

//FIXME This has no actual purpose yet.
public class EntityTile implements INBTReadWrite
{
	public int xCoord;
	public int yCoord;
	public int layerID;
	public Map mapObj;
	
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
		return mapObj.getTileAt(xCoord, yCoord, layerID);
	}
	
	/**
	 * Returns the Integer data associated with this EntityTile. Might be {@code null}.
	 * @return Integer
	 */
	public Integer getParentData()
	{
		return mapObj.getTileDataAt(xCoord, yCoord, layerID);
	}
}
