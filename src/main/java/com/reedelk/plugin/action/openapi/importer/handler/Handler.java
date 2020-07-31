package com.reedelk.plugin.action.openapi.importer.handler;

import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;

import java.util.Map;

public interface Handler {

    boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition);

    void accept(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition);

}
