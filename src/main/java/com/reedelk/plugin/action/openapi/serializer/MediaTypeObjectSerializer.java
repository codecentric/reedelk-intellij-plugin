package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.runtime.api.commons.ImmutableMap;

import java.util.Map;

public class MediaTypeObjectSerializer implements Serializer<MediaTypeObject> {

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, MediaTypeObject mediaTypeObject) {
        // Replace all schemas with reference object to the Resource Text.
        // Example?
        return ImmutableMap.of("schema", "/assets/schemas/test.yaml");
    }
}
