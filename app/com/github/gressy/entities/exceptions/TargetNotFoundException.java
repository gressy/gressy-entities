package com.github.gressy.entities.exceptions;

public class TargetNotFoundException extends ModelException {

    public TargetNotFoundException(Class entityClass, long id) {
        super(404, "Target not found: " + entityClass.getSimpleName() + " #" + id, entityClass, id);
    }

}
