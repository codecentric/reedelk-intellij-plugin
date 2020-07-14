package com.reedelk.plugin.action.importopenapi.handler;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

public class OPTIONSHandler extends AbstractHandler {

    private static final String HTTP_METHOD = "OPTIONS";

    @Override
    String getHttpMethod() {
        return HTTP_METHOD;
    }

    @Override
    Operation getOperation(PathItem pathItem) {
        return pathItem.getOptions();
    }

    @Override
    public boolean isApplicable(PathItem pathEntry) {
        return pathEntry.getOptions() != null;
    }
}
