package vic.rpg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import vic.rpg.utils.Utils.Side;

/**
 * Mark any static method with this annotation to let it been loaded on startup. Directly loads after all {@link Init} calls.<br>
 * A {@link Side} is used to specify weather it should be called from Server or Client/Editor.
 * <br>
 * Supported Sides are: {@link Side#CLIENT CLIENT}, {@link Side#CLIENT SERVER}, {@link Side#CLIENT BOTH} <br>
 * <br>
 * <b>Only for use inside vic.rpg!</b>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostInit 
{
	Side side();
}
