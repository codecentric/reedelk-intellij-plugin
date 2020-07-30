package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.OpenApiDeserializer;
import com.reedelk.openapi.v3.OpenApiObject;
import com.reedelk.openapi.v3.PathsObject;
import com.reedelk.plugin.action.openapi.handler.Handlers;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OpenApiImporter {

    private final OpenApiImporterContext context;
    private final String openAPIFilePath;

    public OpenApiImporter(@NotNull OpenApiImporterContext context, @NotNull String openAPIFilePath) {
        this.context = context;
        this.openAPIFilePath = openAPIFilePath;
    }

    public void process() {
        String content = null;
        try {
            content = org.apache.commons.io.FileUtils.readFileToString(new File(openAPIFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PluginException("Could not read OpenAPI file");
        }
        OpenApiObject openApiObject = OpenApiDeserializer.from(content);
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) -> Handlers.handle(context, pathEntry, pathItem));

        String openApiTitle = openApiObject.getInfo() != null ?
                openApiObject.getInfo().getTitle() : StringUtils.EMPTY;

        String title = openApiTitle;
        context.createConfig(title + ".fconfig");
    }
}
