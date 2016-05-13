package gressy.entities.actions;

import play.mvc.With;

import java.lang.annotation.*;

/**
 * Marks the annotated controller method as needing authentication.
 *
 * Any unauthenticated calls will result on an exception getting thrown.
 * You can optionally specify a list of allowed roles, and the method will
 * be restricted to users with any of those specific roles.
 *
 * @see gressy.entities.actions.AuthenticatedAction
 */
@With(AuthenticatedAction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Authenticated {
    String[] role() default {};
}
