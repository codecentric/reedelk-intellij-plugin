package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.HeaderObject;

import java.util.Map;

public class HeaderObjectSerializer extends com.reedelk.openapi.v3.serializer.HeaderObjectSerializer {

    @Override
    public Map<String, Object> serialize(SerializerContext context, NavigationPath navigationPath, HeaderObject input) {
        Map<String, Object> serialize = super.serialize(context, navigationPath, input);
        if (input.getSchema() != null) {
            this.set(serialize, "schema", "/assets/schema.json");
        }
        return serialize;
    }
}
