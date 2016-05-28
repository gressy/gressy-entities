package com.github.gressy.entities.model;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import play.db.jpa.JPA;

/**
 * Base class for all the DAOs using Entity descendants.
 * Do not subclass more than one level, or with generic types!
 */
public class EntityDao<Entity extends com.github.gressy.entities.model.Entity> {

    // Hack to know Entity.class real value after type erasure.
    protected Class<Entity> entityClass;

    @SuppressWarnings("unchecked")
    public EntityDao() {
        ParameterizedType superclass = (ParameterizedType) ((Class) this.getClass()).getGenericSuperclass();
        entityClass = (Class<Entity>) superclass.getActualTypeArguments()[0];
    }

    /**
     * Adds a newly created Entity to the persistence context.
     * @param entity The newly created Entity.
     */
    public void create(Entity entity) {
        JPA.em().persist(entity);
        JPA.em().flush();
    }


    /**
     * Replaces an existing entity with a new version of it.
     * @param entity The new version of the entity, with the same ID as the old one.
     */
    public void merge(Entity entity) {
        JPA.em().merge(entity);
        JPA.em().flush();
    }


    /**
     * Finds the entity with the given id.
     * @param id The id of the entity.
     * @return The entity if it exists, or null otherwise.
     */
    public Entity findById(long id) {
        return JPA.em().find(entityClass, id);
    }


    /**
     * Deletes the specified entity.
     * @param entity The Entity to delete.
     */
    public void delete(Entity entity) {
        JPA.em().remove(entity);
        JPA.em().flush();
    }


    /**
     * Finds all existing entities.
     * @return A list with all the entities.
     */
    @SuppressWarnings("unchecked")
    public List<Entity> findAll() {
        return JPA.em().createQuery("SELECT t FROM " + entityClass.getSimpleName() + " t").getResultList();
    }


    /**
     * Clones the specified entity.
     * @param entity The entity to clone. It will be detached and modified in place.
     * @return The persisted entity, re-attached.
     */
    public Entity clone(Entity entity) {
        JPA.em().detach(entity);
        entity.setId(0);
        JPA.em().persist(entity);
        JPA.em().flush();
        return entity;
    }

}
