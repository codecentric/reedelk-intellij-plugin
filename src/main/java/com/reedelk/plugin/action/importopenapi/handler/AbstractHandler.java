package com.reedelk.plugin.action.importopenapi.handler;

import com.reedelk.plugin.action.importopenapi.ImporterOpenAPIContext;
import com.reedelk.plugin.template.Template;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

import java.util.Properties;
import java.util.UUID;

abstract class AbstractHandler implements Handler {

    @Override
    public void accept(ImporterOpenAPIContext context, String pathEntry, PathItem pathItem) {

        String description = getOrDefault(pathItem.getDescription(), StringUtils.EMPTY);

        Operation operation = getOperation(pathItem);
        String operationId = operation.getOperationId();
        String summary = getOrDefault(operation.getSummary(), operationId + " Flow");
        String operationDescription = "Path: " + pathEntry;
        String httpMethod = getHttpMethod();

        Properties properties =
                new OperationFlowProperties(
                        context.getConfigId(),
                        summary,
                        description,
                        operationDescription,
                        pathEntry,
                        httpMethod);

        String fileName = operationId + "." + FileExtension.FLOW.value();
        context.createTemplate(Template.OpenAPI.FLOW_WITH_REST_LISTENER, fileName, properties);
    }

    private String getOrDefault(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    abstract String getHttpMethod();

    abstract Operation getOperation(PathItem pathItem);

    static class OperationFlowProperties extends Properties {

        OperationFlowProperties(String configId, String flowTitle, String flowDescription, String restListenerDescription, String restPath, String restMethod) {
            put("id", UUID.randomUUID().toString());
            put("title", flowTitle);
            put("description", flowDescription);
            put("operationDescription", restListenerDescription);
            put("configId", configId);
            put("operationPath", restPath);
            put("operationMethod", restMethod);
        }
    }
}
