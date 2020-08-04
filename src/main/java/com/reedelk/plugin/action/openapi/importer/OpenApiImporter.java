package com.reedelk.plugin.action.openapi.importer;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.OpenApiModel;
import com.reedelk.openapi.commons.AbstractSerializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.importer.handler.Handlers;
import com.reedelk.plugin.action.openapi.serializer.ComponentsObjectSerializer;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.runtime.api.commons.ImmutableMap.of;
import static java.util.stream.Collectors.toList;

public class OpenApiImporter {

    private final OpenApiImporterContext context;
    private final String openAPIFilePath;

    public OpenApiImporter(@NotNull OpenApiImporterContext context, @NotNull String openAPIFilePath) {
        this.context = context;
        this.openAPIFilePath = openAPIFilePath;
    }

    public void process() {
        // Read the OpenAPI file as string.
        String content;
        try {
            content = org.apache.commons.io.FileUtils.readFileToString(new File(openAPIFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PluginException("Could not read OpenAPI file");
        }



        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);

        // Config
        String openApiTitle = openApiObject.getInfo() != null ?
                openApiObject.getInfo().getTitle() : StringUtils.EMPTY;

        // Title
        String title = openApiTitle;

        CustomOpenApiObject customOpenApiObject = new CustomOpenApiObject(openApiObject);

        String configOpenApi = OpenApi.toJson(customOpenApiObject,
                of(CustomOpenApiObject.class, new CustomOpenApiObjectSerializer(),
                        ComponentsObject.class, new ComponentsObjectSerializer(context, SchemaFormat.formatOf(openAPIFilePath))));
        context.createConfig(title + ".fconfig", configOpenApi);

        // Paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) -> Handlers.handle(context, pathEntry, pathItem));
    }

    static class CustomOpenApiObjectSerializer extends AbstractSerializer<CustomOpenApiObject> {

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

    // The rest listener config
    static class CustomOpenApiObject implements OpenApiModel {

        private InfoObject info;
        private List<ServerObject> servers;
        private ComponentsObject components;

        public CustomOpenApiObject(OpenApiObject object) {
            info = object.getInfo();
            servers = object.getServers();
            components = object.getComponents();
        }

        public InfoObject getInfo() {
            return info;
        }

        public List<ServerObject> getServers() {
            return servers;
        }

        public ComponentsObject getComponents() {
            return components;
        }
    }
}
