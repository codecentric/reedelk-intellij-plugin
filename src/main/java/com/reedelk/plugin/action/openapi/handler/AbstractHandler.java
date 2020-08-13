package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import com.reedelk.plugin.template.FlowWithRestListenerProperties;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Map;
import java.util.Properties;

abstract class AbstractHandler implements Handler {

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public void accept(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        OperationObject operation = getOperation(pathDefinition);

        String operationId = operation.getOperationId();
        String configId = context.getRestListenerConfigId();
        String flowTitle = getOrDefault(operation.getSummary(), operationId + " Flow");
        String flowDescription = getOrDefault(operation.getDescription(), flowTitle + " description");
        String restListenerDescription = "Path: " + pathEntry;
        String restPath = pathEntry;
        String restMethod = getHttpMethod();

        NavigationPath navigationPath =
                NavigationPath.create()
                        .with(NavigationPath.SegmentKey.OPERATION_ID, operationId)
                        .with(NavigationPath.SegmentKey.PATH, pathEntry)
                        .with(NavigationPath.SegmentKey.METHOD, restMethod);
        String openApiOperationObject = Serializer.toJson(operation, context, navigationPath);

        Properties properties = new FlowWithRestListenerProperties(
                        configId, flowTitle, flowDescription, restListenerDescription,
                        restPath, restMethod, openApiOperationObject);

        String fileName = OpenApiUtils.flowFileNameFrom(navigationPath);
        context.createRestListenerFlow(fileName, properties);
    }

    private String getOrDefault(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    abstract String getHttpMethod();

    abstract OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition);
}
