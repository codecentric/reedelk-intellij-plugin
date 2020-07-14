package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.plugin.action.importopenapi.ImporterOpenAPIContext;
import io.swagger.v3.oas.models.PathItem;

import java.util.Arrays;
import java.util.List;

public class Handlers {

    private static final List<Handler> HANDLERS = Arrays.asList(
            new GETHandler(),
            new POSTHandler());

    public static void handle(ImporterOpenAPIContext context, String pathEntry, PathItem pathItem) {
        for (Handler handler : HANDLERS) {
            if (handler.isApplicable(pathItem)) {
                handler.accept(context, pathEntry, pathItem);
            }
        }
    }
}
