package gressy.entities.exceptions;

public class ModelException extends GressyException {

    private long id;
    private Class entityClass;

    public Class getEntityClass() {
        return entityClass;
    }

    public long getId() {
        return id;
    }

    public ModelException(String message) {
        super(400, message);
    }

    public ModelException(int statusCode, String message) {
        super(statusCode, message);
    }

    public ModelException(String message, Class entityClass, long id) {
        super(400, message);
        this.entityClass = entityClass;
        this.id = id;
    }

    public ModelException(int statusCode, String message, Class entityClass, long id) {
        super(statusCode, message);
        this.entityClass = entityClass;
        this.id = id;
    }

}
