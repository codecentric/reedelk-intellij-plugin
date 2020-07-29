package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.RestMethod;

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
