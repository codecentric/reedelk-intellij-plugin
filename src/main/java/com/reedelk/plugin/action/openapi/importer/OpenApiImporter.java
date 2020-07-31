package com.reedelk.plugin.action.openapi.importer;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.importer.handler.Handlers;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;

public class OpenApiImporter {

    private final OpenApiImporterContext context;
    private final String openAPIFilePath;

    public OpenApiImporter(@NotNull OpenApiImporterContext context, @NotNull String openAPIFilePath) {
        this.context = context;
        this.openAPIFilePath = openAPIFilePath;
    }

    public void process() {
        String content;
        try {
            content = org.apache.commons.io.FileUtils.readFileToString(new File(openAPIFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PluginException("Could not read OpenAPI file");
        }

        OpenApiObject openApiObject = OpenApi.from(content);

        ComponentsObject components = openApiObject.getComponents();
        Map<String, SchemaObject> schemas = components.getSchemas();
        schemas.forEach(new BiConsumer<String, SchemaObject>() {
            @Override
            public void accept(String s, SchemaObject schemaObject) {
                Schema schema = schemaObject.getSchema();
                // TODO: Custom schema object serializer, serialized in the rest listener is:
                
            }
        });


        // Paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) ->
                Handlers.handle(context, pathEntry, pathItem));


        // Config
        String openApiTitle = openApiObject.getInfo() != null ?
                openApiObject.getInfo().getTitle() : StringUtils.EMPTY;
        String title = openApiTitle;

        context.createConfig(title + ".fconfig");
    }
}
