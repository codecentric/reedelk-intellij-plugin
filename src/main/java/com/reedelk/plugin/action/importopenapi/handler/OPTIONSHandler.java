package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.RestMethod;

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
