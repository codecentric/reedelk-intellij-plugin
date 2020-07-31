package com.reedelk.plugin.action.openapi.importer.handler;

import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;

import java.util.Map;

public class POSTHandler extends AbstractHandler {

    @Override
    String getHttpMethod() {
        return RestMethod.POST.name();
    }

    @Override
    OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.get(RestMethod.POST);
    }

    @Override
    public boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.containsKey(RestMethod.POST);
    }
}
