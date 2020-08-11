package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.AbstractSerializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

class CustomOpenApiObjectSerializer extends AbstractSerializer<CustomOpenApiObject> {

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, CustomOpenApiObject customOpenApiObject) {
        Map<String, Object> map = new LinkedHashMap<>();


        Map<String, Object> serializedInfo = serializerContext.serialize(navigationPath, customOpenApiObject.getInfo());
        set(map, "info", serializedInfo); // REQUIRED

        List<Map<String, Object>> mappedServers = customOpenApiObject
                .getServers()
                .stream()
                .map(serverObject ->
                        serializerContext.serialize(navigationPath, serverObject))
                .collect(toList());
        map.put("servers", mappedServers);

        Map<String, Object> serializedComponents = serializerContext.serialize(navigationPath, customOpenApiObject.getComponents());
        set(map, "components", serializedComponents);

        return map;
    }

}
