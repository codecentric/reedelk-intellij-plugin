package com.reedelk.plugin.action.importopenapi.handler;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

public class PUTHandler extends AbstractHandler {

    private static final String HTTP_METHOD = "PUT";

    @Override
    public boolean isApplicable(PathItem pathEntry) {
        return pathEntry.getPut() != null;
    }

    @Override
    String getHttpMethod() {
        return HTTP_METHOD;
    }

    @Override
    Operation getOperation(PathItem pathItem) {
        return pathItem.getPut();
    }
}
