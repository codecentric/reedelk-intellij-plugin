package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.PathsObject;
import com.reedelk.plugin.action.openapi.handler.Handlers;
import com.reedelk.plugin.action.openapi.serializer.ConfigOpenApiObject;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class OpenApiImporter {

    private final OpenApiImporterContext context;

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public void processImport() throws OpenApiException {
        // Read the OpenAPI file as string.
        String content = readOpenApiFile();

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);


        // Generate listener config
        String openApiConfigFileName = OpenApiUtils.configFileNameOf(openApiObject);
        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);
        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);
        context.createConfig(openApiConfigFileName, configOpenApiObjectJson);

        // Generate REST flows from paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) ->
                Handlers.handle(context, pathEntry, pathItem));
    }

    private String readOpenApiFile() throws OpenApiException {
        try {
            return com.reedelk.plugin.commons.FileUtils.readFileToString(context.getOpenApiFilePath());
        } catch (IOException exception) {
            throw new OpenApiException("Could not read OpenAPI file", exception);
        }
    }
}
