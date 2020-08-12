package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Handlers {

    private static final List<Handler1> HANDLER_1s = Arrays.asList(
            new GETHandler(),
            new POSTHandler(),
            new PUTHandler(),
            new HEADHandler(),
            new DELETEHandler(),
            new OPTIONSHandler());

    public static void handle(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        for (Handler1 handler1 : HANDLER_1s) {
            if (handler1.isApplicable(pathDefinition)) {
                handler1.accept(context, pathEntry, pathDefinition);
            }
        }
    }
}
