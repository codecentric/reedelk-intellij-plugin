package de.codecentric.reedelk.plugin.action.openapi.handler;

import de.codecentric.reedelk.openapi.v3.model.OperationObject;
import de.codecentric.reedelk.openapi.v3.model.RestMethod;

import java.util.Map;

public class OPTIONSHandler extends AbstractHandler {

    @Override
    String getHttpMethod() {
        return RestMethod.OPTIONS.name();
    }

    @Override
    OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.get(RestMethod.OPTIONS);
    }

    @Override
    public boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.containsKey(RestMethod.OPTIONS);
    }
}
