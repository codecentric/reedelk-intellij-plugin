package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.ServerVariableObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerVariableObjectSerializer extends AbstractSerializer<ServerVariableObject> {

    protected ServerVariableObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ServerVariableObject serverVariableObject) {
        Map<String, Object> map = new LinkedHashMap<>();
        setList(map, OpenApiConstants.PROPERTY_ENUM_VALUES, serverVariableObject.getEnumValues());
        set(map, OpenApiConstants.PROPERTY_DEFAULT_VALUE, serverVariableObject.getDefaultValue());
        set(map, ServerVariableObject.Properties.DESCRIPTION.value(), serverVariableObject.getDescription());
        return map;
    }
}
