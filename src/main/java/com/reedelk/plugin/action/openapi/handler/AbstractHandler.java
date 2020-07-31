package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.template.Template;
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
                of(MediaTypeObject.class, new MediaTypeObjectSerializer(),
                        ParameterObject.class, new ParameterObjectSerializer(),
                        HeaderObject.class, new HeaderObjectSerializer()));

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
