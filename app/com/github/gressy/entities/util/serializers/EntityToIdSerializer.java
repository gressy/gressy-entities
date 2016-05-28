package com.github.gressy.entities.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.gressy.entities.model.Entity;

import java.io.IOException;

public class EntityToIdSerializer extends JsonSerializer<Entity> {

    @Override
    public void serialize(Entity entity,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {
        if (entity != null) {
            jsonGenerator.writeNumber(entity.getId());
        }
    }
}
