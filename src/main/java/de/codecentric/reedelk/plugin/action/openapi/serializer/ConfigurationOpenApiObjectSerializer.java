package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.ComponentsObject;
import de.codecentric.reedelk.openapi.v3.model.ServerObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.codecentric.reedelk.openapi.v3.model.OpenApiObject.Properties.*;
import static de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils.isNotEmpty;
import static java.util.stream.Collectors.toList;

class ConfigurationOpenApiObjectSerializer extends AbstractSerializer<ConfigurationOpenApiObject> {

    protected ConfigurationOpenApiObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ConfigurationOpenApiObject configurationOpenApiObject) {
        Map<String, Object> serialized = new LinkedHashMap<>();

        Map<String, Object> serializedInfo = serializerContext.serialize(navigationPath, configurationOpenApiObject.getInfo());
        set(serialized, INFO.value(), serializedInfo); // REQUIRED

        List<ServerObject> servers = configurationOpenApiObject.getServers();
        if (OpenApiUtils.isNotEmpty(servers)) {
            List<Map<String, Object>> mappedServers = servers
                    .stream()
                    .map(serverObject ->
                            serializerContext.serialize(navigationPath, serverObject))
                    .collect(toList());
            serialized.put(SERVERS.value(), mappedServers);
        }

        ComponentsObject components = configurationOpenApiObject.getComponents();
        if (components != null) {
            Map<String, Object> serializedComponents = serializerContext.serialize(navigationPath, components);
            if (OpenApiUtils.isNotEmpty(serializedComponents)) set(serialized, COMPONENTS.value(), serializedComponents);
        }

        return serialized;
    }
}
