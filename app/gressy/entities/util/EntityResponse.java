package gressy.entities.util;

import com.fasterxml.jackson.databind.JsonNode;
import gressy.entities.model.Entity;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to easily and fluidly create JSON responses from Entities.
 */
public class EntityResponse {

    private Map<String, List<Entity>> entities = new HashMap<>();

    public EntityResponse() {}

    public EntityResponse(Entity entity) {
        addEntity(entity);
    }

    public EntityResponse(List<Entity> list) {
        addEntities(list);
    }

    public EntityResponse addEntity(Entity entity) {
        String type = entity.getClass().getSimpleName();
        if (!entities.containsKey(type)) {
            entities.put(type, new ArrayList<>());
        }
        entities.get(type).add(entity);
        return this;
    }

    public EntityResponse addEntities(List<Entity> list) {
        if (list.size() > 0) {
            String type = list.get(0).getClass().getSimpleName();
            if (!entities.containsKey(type)) {
                entities.put(type, new ArrayList<>());
            }
            entities.get(type).addAll(list);
        }
        return this;
    }

    public JsonNode asJson() {
        return Json.toJson(entities);
    }

}
