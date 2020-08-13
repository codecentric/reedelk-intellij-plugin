package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.RestMethod;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import com.reedelk.plugin.template.FlowWithRestListenerProperties;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;

import java.util.Map;
import java.util.Properties;

abstract class AbstractHandler implements Handler {

    @Override
    public void accept(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        OperationObject operation = getOperation(pathDefinition);

        String operationId = operation.getOperationId();

        String summary = getOrDefault(operation.getSummary(), operationId + " Flow");
        String description = getOrDefault(operation.getDescription(), summary + " description");

        String operationDescription = "Path: " + pathEntry;
        String httpMethod = getHttpMethod();

        // TODO: Path entry as well in the navigation path
        NavigationPath navigationPath = NavigationPath.create().with(NavigationPath.SegmentKey.OPERATION_ID, operationId);

        String openApiOperation = Serializer.toJson(operation, context, navigationPath);

        Properties properties =
                new FlowWithRestListenerProperties(context.getConfigId(), summary, description, operationDescription, pathEntry, httpMethod, openApiOperation);

        String fileName = operationId + "." + FileExtension.FLOW.value();
        context.createFlow(fileName, properties);
    }

    private String getOrDefault(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    abstract String getHttpMethod();

    abstract OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition);
}
