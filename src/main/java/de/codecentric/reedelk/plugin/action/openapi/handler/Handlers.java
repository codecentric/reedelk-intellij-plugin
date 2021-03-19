package de.codecentric.reedelk.plugin.action.openapi.handler;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.openapi.v3.model.OperationObject;
import de.codecentric.reedelk.openapi.v3.model.RestMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Handlers {

    private Handlers() {
    }

    private static final List<Handler> HANDLERS = Arrays.asList(
            new GETHandler(),
            new POSTHandler(),
            new PUTHandler(),
            new HEADHandler(),
            new DELETEHandler(),
            new OPTIONSHandler());

    public static void handle(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        for (Handler handler : HANDLERS) {
            if (handler.isApplicable(pathDefinition)) {
                handler.accept(context, pathEntry, pathDefinition);
            }
        }
    }
}
