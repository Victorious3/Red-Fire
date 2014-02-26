package vic.rpg.level;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.Annotation;

import vic.rpg.editor.Editor;

/**
 * An {@link Annotation} that marks variables as editable by the {@link Editor}.
 * @see Editor#updateTilesAndEntites()
 * @author Victorious3
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Editable{}
