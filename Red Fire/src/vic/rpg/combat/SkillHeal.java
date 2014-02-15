package vic.rpg.combat;

import vic.rpg.level.entity.living.EntityLiving;

public class SkillHeal extends Skill
{
	public SkillHeal() 
	{
		super("/vic/rpg/resources/skill/heal.png");
	}

	@Override
	public void onSkillCast(EntityLiving entity) 
	{
		super.onSkillCast(entity);
	}	
}
