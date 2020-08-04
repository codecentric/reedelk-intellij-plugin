package com.reedelk.plugin.action.openapi.importer.handler;

import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;

import java.util.Map;

public class PUTHandler extends AbstractHandler {

    @Override
    String getHttpMethod() {
        return RestMethod.PUT.name();
    }

    @Override
    OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.get(RestMethod.PUT);
    }

    @Override
    public boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.containsKey(RestMethod.PUT);
    }
}