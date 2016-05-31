package com.github.gressy.entities.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.db.jpa.JPAApi;
import play.libs.Json;

import javax.inject.Inject;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class SchemaReader {

    @Inject
    protected JPAApi jpaApi;


    /**
     * Returns a JSON Object representing the attribute meta-model
     * @param attribute The target attribute
     * @return The schema as a JSON Object
     */
    public ObjectNode getAttributeSchema(Attribute attribute) {
        ObjectNode ret = Json.newObject();
        ret.put("type", attribute.getJavaType().getName());

        // Check for supported annotations
        Field field = (Field) attribute.getJavaMember();
        if (field.getAnnotation(NotNull.class) != null) ret.put("notNull", true);

        return ret;
    }


    /**
     * Returns a JSON Object representing the entity type's meta-model
     * @param entityType The target entity type
     * @return The schema as a JSON Object
     */
    public ObjectNode getEntitySchema(EntityType<?> entityType) {
        ObjectNode ret = Json.newObject();

        // Add list of attributes
        ObjectNode attributes = Json.newObject();
        entityType.getAttributes().forEach(
                attribute -> attributes.set(attribute.getName(), getAttributeSchema(attribute)));
        ret.set("attributes", attributes);

        return ret;
    }


    /**
     * Returns a JSON Object representing the complete Entity Manager meta-model
     * @return The schema as a JSON Object
     */
    public ObjectNode getSchema() {
        ObjectNode ret = Json.newObject();

        // Add all entities
        ObjectNode entities = Json.newObject();
        Metamodel metamodel = jpaApi.em().getMetamodel();
        metamodel.getEntities().forEach(entityType -> {
            if (!Modifier.isAbstract(entityType.getJavaType().getModifiers())) {
                entities.set(entityType.getName(), getEntitySchema(entityType));
            }
        });
        ret.set("entities", entities);

        return ret;
    }

}
