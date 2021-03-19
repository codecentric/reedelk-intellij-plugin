package de.codecentric.reedelk.plugin.action.openapi.handler;

import de.codecentric.reedelk.openapi.v3.model.OperationObject;
import de.codecentric.reedelk.openapi.v3.model.RestMethod;

import java.util.Map;

public class DELETEHandler extends AbstractHandler {

    @Override
    String getHttpMethod() {
        return RestMethod.DELETE.name();
    }

    @Override
    OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.get(RestMethod.DELETE);
    }

    @Override
    public boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.containsKey(RestMethod.DELETE);
    }
}
