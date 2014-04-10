package vic.rpg.combat;

import vic.rpg.level.entity.living.EntityLiving;
import vic.rpg.render.Drawable;
import vic.rpg.utils.Utils;
import vic.rpg.utils.Utils.Side;

/**
 * Under Construction
 * @author Victorious3
 */
public class Skill extends Drawable implements Cloneable
{
	public int id;

	public Skill(String image) 
	{
		super(60, 60);
		
		if(Utils.getSide() == Side.CLIENT)
		{
			setTexture(Utils.readImage(image));
		}
	}
	
	public void onSkillCast(EntityLiving entity)
	{
		
	}
	
	@Override
	public Skill clone() 
	{
		return (Skill) super.clone();
	}
}
