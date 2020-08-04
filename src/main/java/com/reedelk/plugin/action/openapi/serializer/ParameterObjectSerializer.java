package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ParameterObject;

import java.util.Map;

public class ParameterObjectSerializer extends com.reedelk.openapi.v3.serializer.ParameterObjectSerializer {

    @Override
    public Map<String, Object> serialize(SerializerContext context, NavigationPath navigationPath, ParameterObject input) {
        Map<String, Object> serialized = super.serialize(context, navigationPath, input);
        if (input.getSchema() != null) {
            this.set(serialized, "schema", "/assets/schema.json");
        }
        return serialized;
    }
}
