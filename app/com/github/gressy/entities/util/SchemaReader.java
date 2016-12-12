package com.github.gressy.entities.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.gressy.entities.annotations.PanelTitle;
import play.db.jpa.JPAApi;
import play.libs.Json;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        Class type = attribute.getJavaType();
        if (type == String.class) {
            ret.put("type", "string");
        } else if (type == Long.class || type == Integer.class || type == long.class || type == int.class) {
            ret.put("type", "number");
            ret.put("isInteger", true);
        } else if (type == Float.class || type == Double.class || type == float.class || type == double.class) {
            ret.put("type", "number");
            ret.put("isDecimal", true);
        } else if (type == Boolean.class || type == boolean.class) {
            ret.put("type", "boolean");
        } else if (type.isEnum()) {
            ret.put("type", "enum");
            List<String> options = new ArrayList<>();
            for (Object o : type.getEnumConstants()) {
                options.add(o.toString());
            }
            ret.set("options", Json.toJson(options));
        } else if (type.getAnnotation(Entity.class) != null) {
            ret.put("type", "relationship");
            ret.put("related", type.getSimpleName());
        } else {
            ret.put("type", type.getName());
        }

        // Check for supported annotations
        Field field = (Field) attribute.getJavaMember();
        if (field.getAnnotation(NotNull.class) != null) ret.put("notNull", true);
        if (field.getAnnotation(JsonProperty.class) != null) {
            ret.put("jsonName", field.getAnnotation(JsonProperty.class).value());
        }

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

        // Other metadata
        String titleField = getEntityTitle(entityType);
        if (titleField != null) ret.put("title", titleField);

        return ret;
    }

    /**
     * Returns the name of the title field for a entity type
     * @param entityType The target entity type
     * @return The name of the title field, or null if not available
     */
    private String getEntityTitle(EntityType<?> entityType) {
        List<String> fields = new ArrayList<>();
        for (Attribute a: entityType.getAttributes()) {
            if (a.getJavaType().getAnnotation(PanelTitle.class) != null) return a.getName();
            fields.add(a.getName());
        }
        if (fields.contains("title")) return "title";
        if (fields.contains("name")) return "name";
        return null;
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
