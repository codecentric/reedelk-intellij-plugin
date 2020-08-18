package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ServerVariableObject;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiStringConstants;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerVariableObjectSerializer extends AbstractSerializer<ServerVariableObject> {

    protected ServerVariableObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ServerVariableObject serverVariableObject) {
        Map<String, Object> map = new LinkedHashMap<>();
        setList(map, OpenApiStringConstants.PROPERTY_ENUM_VALUES, serverVariableObject.getEnumValues());
        set(map, OpenApiStringConstants.PROPERTY_DEFAULT_VALUE, serverVariableObject.getDefaultValue());
        set(map, ServerVariableObject.Properties.DESCRIPTION.value(), serverVariableObject.getDescription());
        return map;
    }
}
