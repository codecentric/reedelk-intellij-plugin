package com.reedelk.plugin.action.openapi;

import com.reedelk.plugin.action.openapi.handler.Handlers;
import org.jetbrains.annotations.NotNull;

public class OpenApiImporter1 {

    private final OpenApiImporterContext context;
    private final String openAPIFilePath;

    public OpenApiImporter1(@NotNull OpenApiImporterContext context, @NotNull String openAPIFilePath) {
        this.context = context;
        this.openAPIFilePath = openAPIFilePath;
    }

    public void process() {
        ParserOpenAPI parserOpenAPI = new ParserOpenAPI(openAPIFilePath);
        parserOpenAPI.parse();
        parserOpenAPI.forEachPath((pathEntry, pathItem) -> Handlers.handle(context, pathEntry, pathItem));

        String title = parserOpenAPI.getTitle();
        context.createConfig(title + ".fconfig");
    }
}
