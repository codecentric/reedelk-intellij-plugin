package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.AbstractSerializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.openapi.v3.model.OpenApiObject.Properties;
import static java.util.stream.Collectors.toList;

class ConfigOpenApiObjectSerializer extends AbstractSerializer<ConfigOpenApiObject> {

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ConfigOpenApiObject configOpenApiObject) {
        Map<String, Object> map = new LinkedHashMap<>();


        Map<String, Object> serializedInfo = serializerContext.serialize(navigationPath, configOpenApiObject.getInfo());
        set(map, Properties.INFO.value(), serializedInfo); // REQUIRED

        List<Map<String, Object>> mappedServers = configOpenApiObject
                .getServers()
                .stream()
                .map(serverObject ->
                        serializerContext.serialize(navigationPath, serverObject))
                .collect(toList());
        map.put(Properties.SERVERS.value(), mappedServers);

        Map<String, Object> serializedComponents = serializerContext.serialize(navigationPath, configOpenApiObject.getComponents());
        set(map, Properties.COMPONENTS.value(), serializedComponents);

        return map;
    }
}
