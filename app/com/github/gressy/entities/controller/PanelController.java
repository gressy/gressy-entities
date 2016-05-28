package com.github.gressy.entities.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.gressy.entities.actions.Authenticated;
import com.github.gressy.entities.exceptions.GressyException;
import com.github.gressy.entities.exceptions.TargetNotFoundException;
import com.github.gressy.entities.model.Entity;
import com.github.gressy.entities.util.EntityResponse;
import com.github.gressy.entities.util.SchemaReader;
import org.hibernate.Hibernate;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.persistence.metamodel.EntityType;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for the Panel api calls.
 */
public class PanelController extends Controller {

    @Inject
    protected JPAApi jpaApi;

    @Inject
    protected SchemaReader schemaReader;

    private Map<String, Class<Entity>> entityClassesByName;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @SuppressWarnings("unchecked")
    private Class<Entity> getEntityClassByName(String entityType) {
        if (entityClassesByName == null) {
            Set<EntityType<?>> entityTypes = JPA.em().getMetamodel().getEntities();
            entityClassesByName = new HashMap<>();
            entityTypes.stream()
                    .filter(type -> Entity.class.isAssignableFrom(type.getJavaType()))
                    .forEach(type -> entityClassesByName.put(type.getName(), (Class<Entity>)type.getJavaType()));
        }
        if (!entityClassesByName.containsKey(entityType)) {
            throw new GressyException(404, "Unknown entity.");
        }
        return entityClassesByName.get(entityType);
    }


    /**
     * Returns a list of all the entities of the given type.
     * @param entityType The target entity type
     * @return A JSON object with all the entities of the given type.
     */
    @Transactional
    @Authenticated(role = {"admin"})
    @SuppressWarnings("unchecked")
    public Result list(String entityType) {
        Class<Entity> entityClass = getEntityClassByName(entityType);

        List<Entity> elements = (List<Entity>)jpaApi.em()
                .createQuery("SELECT t FROM " + entityClass.getSimpleName() + " t")
                .getResultList();

        return ok(new EntityResponse(elements).asJson());
    }


    /**
     * Creates a new entity from the POST body JSON, and returns it.
     * @param entityType The target entity type
     * @return A JSON object with the just-created entity.
     */
    @Transactional
    @Authenticated(role = {"admin"})
    public Result create(String entityType) {
        Class<Entity> entityClass = getEntityClassByName(entityType);
        JsonNode json = request().body().asJson();

        Entity entity;
        try {
            entity = Json.mapper().readerFor(entityClass).readValue(json.toString());
        } catch (Exception e) {
            throw new GressyException(400, "Error parsing entity", e);
        }

        Set<ConstraintViolation<Entity>> validationResult;
        validationResult = validator.validate(entity);
        if (!validationResult.isEmpty()) {
            throw new GressyException(400, validationResult.iterator().next().getMessage());
        }

        jpaApi.em().persist(entity);
        jpaApi.em().flush();

        return ok(new EntityResponse(entity).asJson());
    }


    /**
     * Returns a specific entity of the given type.
     * @param entityType The target entity type
     * @param id The target entity id
     * @return A JSON object with the requested entity.
     */
    @Transactional
    @Authenticated(role = {"admin"})
    public Result read(String entityType, long id) {
        Class<Entity> entityClass = getEntityClassByName(entityType);

        Entity entity = jpaApi.em().find(entityClass, id);
        if (entity == null) throw new TargetNotFoundException(entityClass, id);
        // TODO: Add back-references

        return ok(new EntityResponse(entity).asJson());
    }


    /**
     * Updates an existing entity with the data on the POST body JSON, and returns it.
     * @param entityType The target entity type
     * @param id The target entity id
     * @return A JSON object with the updated entity.
     */
    @Transactional
    @Authenticated(role = {"admin"})
    public Result update(String entityType, long id) {
        Class<Entity> entityClass = getEntityClassByName(entityType);
        JsonNode json = request().body().asJson();

        Entity entity = jpaApi.em().find(entityClass, id);
        if (entity == null) throw new TargetNotFoundException(entityClass, id);

        // Prevent "Failed to lazily initialize.." exception by initializing before detach.
        for (Method method: entity.getClass().getMethods()) {
            if (!method.getName().startsWith("get")) continue;
            if (method.getParameterCount() != 0) continue;
            try {
                Hibernate.initialize(method.invoke(entity));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jpaApi.em().detach(entity);

        try {
            Json.mapper().readerForUpdating(entity).readValue(json.toString());
        } catch (Exception e) {
            throw new GressyException(400, "Error parsing entity", e);
        }
        entity.setId(id);

        jpaApi.em().merge(entity);
        jpaApi.em().flush();

        return ok(new EntityResponse(entity).asJson());
    }


    /**
     * Removes a entity of the given type.
     * @param entityType The target entity type
     * @param id The target entity id
     * @return An empty JSON object.
     */
    @Transactional
    @Authenticated(role = {"admin"})
    public Result delete(String entityType, long id) {
        Class<Entity> entityClass = getEntityClassByName(entityType);

        Entity entity = jpaApi.em().find(entityClass, id);
        if (entity == null) throw new TargetNotFoundException(entityClass, id);

        jpaApi.em().remove(entity);
        jpaApi.em().flush();

        return ok(Json.newObject());
    }


    /**
     * Gets a schema with field details for all the available entities.
     * @return A JSON schema.
     */
    @Transactional
    @Authenticated(role = {"admin"})
    public Result schema() {
        return ok(schemaReader.getSchema());
    }


}
