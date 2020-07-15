package com.reedelk.plugin.action.importopenapi.handler;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

public class GETHandler extends AbstractHandler {

    private static final String HTTP_METHOD = "GET";

    @Override
    public boolean isApplicable(PathItem pathItem) {
        return pathItem.getGet() != null;
    }

    @Override
    String getHttpMethod() {
        return HTTP_METHOD;
    }

    @Override
    Operation getOperation(PathItem pathItem) {
        return pathItem.getGet();
    }
}