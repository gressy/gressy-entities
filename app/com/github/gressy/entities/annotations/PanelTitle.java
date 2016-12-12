package com.github.gressy.entities.annotations;

import java.lang.annotation.*;

/**
 * Marks the annotated field as the title of the entity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface PanelTitle {

}
