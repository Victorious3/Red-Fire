package vic.rpg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import vic.rpg.utils.Utils.Side;

/**
 * Only for use inside vic.rpg!
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Init 
{
	Side side();
}
