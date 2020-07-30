package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.OpenApiSerializer;
import com.reedelk.openapi.v3.OperationObject;
import com.reedelk.openapi.v3.ResponseObject;
import com.reedelk.openapi.v3.RestMethod;
import com.reedelk.plugin.action.openapi.ImporterOpenAPIContext;
import com.reedelk.plugin.template.Template;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.BiConsumer;

abstract class AbstractHandler implements Handler {

    @Override
    public void accept(ImporterOpenAPIContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        OperationObject operation = getOperation(pathDefinition);
        // Replace all schemas with reference object to the Resource Text.

        String operationId = operation.getOperationId();

        Map<String, ResponseObject> responses = operation.getResponses();
        responses.forEach(new BiConsumer<String, ResponseObject>() {
            @Override
            public void accept(String responseCode, ResponseObject apiResponse) {
                // TODO: Complete me
            }
        });

        String summary = getOrDefault(operation.getSummary(), operationId + " Flow");
        String description = getOrDefault(operation.getDescription(), summary + " description");

        String operationDescription = "Path: " + pathEntry;
        String httpMethod = getHttpMethod();

        String openApiOperation = OpenApiSerializer.toJson(operation);

        Properties properties =
                new OperationFlowProperties(context.getConfigId(), summary, description, operationDescription, pathEntry, httpMethod, openApiOperation);

        String fileName = operationId + "." + FileExtension.FLOW.value();
        context.createTemplate(Template.OpenAPI.FLOW_WITH_REST_LISTENER, fileName, properties);
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
