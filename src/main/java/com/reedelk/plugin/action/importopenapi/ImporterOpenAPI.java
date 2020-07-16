package com.reedelk.plugin.action.importopenapi;

import com.reedelk.plugin.action.importopenapi.handler.Handlers;
import org.jetbrains.annotations.NotNull;

public class ImporterOpenAPI {

    private final ImporterOpenAPIContext context;
    private final String openAPIFilePath;

    public ImporterOpenAPI(@NotNull ImporterOpenAPIContext context, @NotNull String openAPIFilePath) {
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
