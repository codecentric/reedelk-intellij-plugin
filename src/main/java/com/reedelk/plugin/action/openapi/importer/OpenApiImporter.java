package com.reedelk.plugin.action.openapi.importer;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.ComponentsObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.PathsObject;
import com.reedelk.plugin.action.openapi.importer.handler.Handlers;
import com.reedelk.plugin.action.openapi.serializer.ComponentsObjectSerializer;
import com.reedelk.plugin.action.openapi.serializer.CustomOpenApiObjectSerializer;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.reedelk.plugin.action.openapi.serializer.CustomOpenApiObjectSerializer.CustomOpenApiObject;
import static com.reedelk.runtime.api.commons.ImmutableMap.of;

public class OpenApiImporter {

    private final OpenApiImporterContext context;

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public void process() throws OpenApiException {
        // Read the OpenAPI file as string.
        String content = readOpenApiFile();

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);

        // Generate listener config
        String openApiTitle = openApiObject.getInfo() != null ?
                openApiObject.getInfo().getTitle() : StringUtils.EMPTY;

        CustomOpenApiObject customOpenApiObject = new CustomOpenApiObject(openApiObject);

        String configOpenApi = OpenApi.toJson(customOpenApiObject,
                of(CustomOpenApiObject.class, new CustomOpenApiObjectSerializer(),
                        ComponentsObject.class, new ComponentsObjectSerializer(context)));
        context.createConfig(openApiTitle + "." + FileExtension.CONFIG, configOpenApi);

        // Generate rest flows from paths
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
