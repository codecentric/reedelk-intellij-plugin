package de.codecentric.reedelk.plugin.action.openapi.handler;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.openapi.v3.model.OperationObject;
import de.codecentric.reedelk.openapi.v3.model.RestMethod;

import java.util.Map;

public interface Handler {

    boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition);

    void accept(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition);

}
