package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.RestMethod;

import java.util.Map;

public class HEADHandler extends AbstractHandler {

    @Override
    String getHttpMethod() {
        return RestMethod.HEAD.name();
    }

    @Override
    OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.get(RestMethod.HEAD);
    }

    @Override
    public boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition) {
        return pathDefinition.containsKey(RestMethod.HEAD);
    }
}
