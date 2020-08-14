package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Handlers {

    private Handlers() {
    }

    private static final List<Handler> HANDLER_1s = Arrays.asList(
            new GETHandler(),
            new POSTHandler(),
            new PUTHandler(),
            new HEADHandler(),
            new DELETEHandler(),
            new OPTIONSHandler());

    public static void handle(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        for (Handler handler : HANDLER_1s) {
            if (handler.isApplicable(pathDefinition)) {
                handler.accept(context, pathEntry, pathDefinition);
            }
        }
    }
}
