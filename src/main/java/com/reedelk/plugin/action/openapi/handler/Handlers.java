package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.RestMethod;
import com.reedelk.plugin.action.openapi.ImporterOpenAPIContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Handlers {

    private static final List<Handler> HANDLERS = Arrays.asList(
            new GETHandler(),
            new POSTHandler(),
            new PUTHandler(),
            new HEADHandler(),
            new DELETEHandler(),
            new OPTIONSHandler());

    public static void handle(ImporterOpenAPIContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        for (Handler handler : HANDLERS) {
            if (handler.isApplicable(pathDefinition)) {
                handler.accept(context, pathEntry, pathDefinition);
            }
        }
    }
}
