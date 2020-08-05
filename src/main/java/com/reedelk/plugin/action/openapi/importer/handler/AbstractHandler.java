package com.reedelk.plugin.action.openapi.importer.handler;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.serializer.HeaderObjectSerializer;
import com.reedelk.plugin.action.openapi.serializer.MediaTypeObjectSerializer;
import com.reedelk.plugin.action.openapi.serializer.ParameterObjectSerializer;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static com.reedelk.runtime.api.commons.ImmutableMap.of;

abstract class AbstractHandler implements Handler {

    @Override
    public void accept(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        OperationObject operation = getOperation(pathDefinition);

        String operationId = operation.getOperationId();

        String summary = getOrDefault(operation.getSummary(), operationId + " Flow");
        String description = getOrDefault(operation.getDescription(), summary + " description");

        String operationDescription = "Path: " + pathEntry;
        String httpMethod = getHttpMethod();

        String openApiOperation = OpenApi.toJson(operation,
                of(MediaTypeObject.class, new MediaTypeObjectSerializer(context),
                        ParameterObject.class, new ParameterObjectSerializer(context),
                        HeaderObject.class, new HeaderObjectSerializer(context)),
                NavigationPath.create().with(NavigationPath.SegmentKey.OPERATION_ID, operationId)); // TODO: Path entry as well in the navigation path

        Properties properties =
                new OperationFlowProperties(context.getConfigId(), summary, description, operationDescription, pathEntry, httpMethod, openApiOperation);

        String fileName = operationId + "." + FileExtension.FLOW.value();
        context.createFlow(fileName, properties);
    }

    private String getOrDefault(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    abstract String getHttpMethod();

    abstract OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition);

    static class OperationFlowProperties extends Properties {

        OperationFlowProperties(String configId, String flowTitle, String flowDescription, String restListenerDescription, String restPath, String restMethod, String openApiOperationObject) {
            put("id", UUID.randomUUID().toString());
            put("title", flowTitle);
            put("description", flowDescription);
            put("restDescription", restListenerDescription);
            put("restConfigId", configId);
            put("restPath", restPath);
            put("restMethod", restMethod);
            put("openApiOperationObject", openApiOperationObject);
        }
    }
}
