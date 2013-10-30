package vic.rpg.level.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.FloatTag;
import org.jnbt.IntArrayTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.LongTag;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

import vic.rpg.level.INBTReadWrite;

//TODO Needs some testing will later be used
public class PlayerData implements INBTReadWrite
{
	private HashMap<String, HashMap<String, PlayerValue>> data = new HashMap<String, HashMap<String, PlayerValue>>();
	
	public PlayerData()
	{
		
	}
	
	public Object getData(String playerName, String dataName)
	{
		if(data.containsKey(playerName))
		{
			HashMap<String, PlayerValue> player = data.get(playerName);
			if(player.containsKey(dataName))
			{
				return player.get(dataName);
			}
		}
		return null;
	}
	
	private static class PlayerValue
	{
		private boolean presistent;
		private Object o;
		
		private PlayerValue(Object o, boolean presistent)
		{
			this.o = o;
			this.presistent = presistent;
		}
	}
	
	public void put(String player, String oName, Object o, boolean presistent)
	{
		if(!data.containsKey(player))
		{
			data.put(player, new HashMap<String, PlayerValue>());
		}
		data.get(player).put(oName, new PlayerValue(o, presistent));
	}
	
	public void put(String player, String oName, Object o)
	{
		put(player, oName, o, true);
	}
	
	public Object get(String player, String oName)
	{
		if(data.containsKey(player))
		{
			if(data.get(player).containsKey(oName))
			{
				return data.get(player).get(oName);
			}
		}
		return null;
	}

	@Override
	public void readFromNBT(CompoundTag tag, Object... args) 
	{
		List<Tag> playerListTag = ((ListTag)tag.getValue().get("playerData")).getValue();
		for(Tag t1 : playerListTag)
		{
			CompoundTag playerDataTag = (CompoundTag)t1;
			String playerName = (String)playerDataTag.getValue().get("playerName").getValue();
			for(Tag t2 : ((ListTag)playerDataTag.getValue().get("data")).getValue())
			{
				CompoundTag t3 = (CompoundTag)t2;
				Tag value = (Tag)t3.getValue().values().toArray()[0];
				put(playerName, value.getName(), value.getValue());
			}
		}
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag tag, Object... args) 
	{
		Map<String, Tag> oldTag = tag.getValue();
		
		List<Tag> dataListTag = new ArrayList<Tag>();
		for(String pName : data.keySet())
		{
			Map<String, Tag> playerDataTag = new HashMap<String, Tag>();
			ArrayList<Tag> playerDataListTag = new ArrayList<Tag>();
			for(String oName : data.get(pName).keySet())
			{				
				PlayerValue pv = data.get(pName).get(oName);
				if(pv.presistent)
				{
					HashMap<String, Tag> value = new HashMap<String, Tag>();
					Object o = pv.o;
					if(o instanceof Boolean) value.put(oName, new ShortTag(oName, ((Boolean)o).booleanValue() ? (short)1 : (short)0));
					else if(o instanceof Integer) value.put(oName, new IntTag(oName, ((Integer)o).intValue()));
					else if(o instanceof Byte) value.put(oName, new ByteTag(oName, ((Byte)o).byteValue()));
					else if(o instanceof Double) value.put(oName, new DoubleTag(oName, ((Double)o).doubleValue()));
					else if(o instanceof Float) value.put(oName, new FloatTag(oName, ((Float)o).floatValue()));
					else if(o instanceof Long) value.put(oName, new LongTag(oName, ((Long)o).longValue()));
					else if(o instanceof String) value.put(oName, new StringTag(oName, (String)o));
					else if(o instanceof Integer[]) value.put(oName, new IntArrayTag(oName, ArrayUtils.toPrimitive((Integer[])o)));
					else if(o instanceof Byte[]) value.put(oName, new ByteArrayTag(oName, ArrayUtils.toPrimitive((Byte[])o)));
					else throw new IllegalArgumentException("Object of type " + o.getClass().getSimpleName() + " can not be saved in PlayerData!");
					playerDataListTag.add(new CompoundTag("value", value));
				}
			}
			playerDataTag.put(pName, new StringTag("playerName", pName));
			playerDataTag.put("data", new ListTag("data", CompoundTag.class, playerDataListTag));
			dataListTag.add(new CompoundTag("playerData", playerDataTag));
		}
		ListTag tag2 = new ListTag("playerData", CompoundTag.class, dataListTag);
		Map<String, Tag> newTag = new HashMap<String, Tag>();
		newTag.putAll(oldTag);
		newTag.put("playerData", tag2);
		return new CompoundTag(tag.getName(), newTag);
	}
}


