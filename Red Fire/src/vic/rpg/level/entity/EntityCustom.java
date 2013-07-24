package vic.rpg.level.entity;

import vic.rpg.level.Entity;

public abstract class EntityCustom extends Entity 
{
	public EntityCustom(int width, int height) 
	{
		super(width, height);
	}
	
	public abstract int getSuggestedID();
}
