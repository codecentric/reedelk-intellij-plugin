package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.RestMethod;
import com.reedelk.plugin.action.importopenapi.ImporterOpenAPIContext;

import java.util.Map;

public interface Handler {

    boolean isApplicable(Map<RestMethod, OperationObject> pathDefinition);

    void accept(ImporterOpenAPIContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition);

}
