package vic.rpg.world.entity;

/**
 * Was used for Entites created by the Editor.
 * @author Victorious3
 *
 */
@Deprecated
public abstract class EntityCustom extends Entity 
{
	public EntityCustom(int width, int height) 
	{
		super(width, height);
	}
	
	public abstract int getSuggestedID();
}
