package com.reedelk.plugin.action.openapi.importer.handler;

import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;

import java.util.Map;

public class GETHandler extends AbstractHandler {

    @Override
    String getHttpMethod() {
        return RestMethod.GET.name();
    }

    @Override
    OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.get(RestMethod.GET);
    }

    @Override
    public boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.containsKey(RestMethod.GET);
    }
}
