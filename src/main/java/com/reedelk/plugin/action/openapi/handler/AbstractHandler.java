package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.OperationObject;
import com.reedelk.openapi.v3.model.ResponseObject;
import com.reedelk.openapi.v3.model.RestMethod;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import com.reedelk.plugin.template.FlowWithRestListenerAndResourceProperties;
import com.reedelk.plugin.template.FlowWithRestListenerProperties;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.reedelk.openapi.commons.NavigationPath.SegmentKey;
import static com.reedelk.openapi.commons.NavigationPath.create;

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

        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, operationId)
                .with(SegmentKey.PATH, pathEntry)
                .with(SegmentKey.METHOD, restMethod);
        String openApiOperationObject = Serializer.toJson(operation, context, navigationPath);

        String fileName = OpenApiUtils.flowFileNameFrom(navigationPath);

        Optional<SuccessExample> example = findSuccessExampleForFlow(operation.getResponses(), navigationPath, context);
        if (example.isPresent()) {
            SuccessExample successExample = example.get();
            FlowWithRestListenerAndResourceProperties properties = new FlowWithRestListenerAndResourceProperties(
                    configId, flowTitle, flowDescription, restListenerDescription,
                    restPath, restMethod, openApiOperationObject,
                    successExample.assetResourceFile, successExample.contentType);
            context.createRestListenerFlowWithExample(fileName, properties);
        } else {
            Properties properties = new FlowWithRestListenerProperties(
                    configId, flowTitle, flowDescription, restListenerDescription,
                    restPath, restMethod, openApiOperationObject);
            context.createRestListenerFlow(fileName, properties);
        }
    }

    protected Optional<SuccessExample> findSuccessExampleForFlow(Map<String, ResponseObject> responses, NavigationPath navigationPath, OpenApiImporterContext context) {
        if (responses.containsKey("200")) {
            ResponseObject responseObject = responses.get("200");
            Map<String, MediaTypeObject> content = responseObject.getContent();
            for (Map.Entry<String, MediaTypeObject> response : content.entrySet()) {
                String contentType = response.getKey();
                MediaTypeObject mediaTypeObject = response.getValue();
                if (mediaTypeObject.getExample() != null) {
                    // We have found an example for 200
                    NavigationPath responseNavigationPath = navigationPath
                            .with(SegmentKey.RESPONSES)
                            .with(SegmentKey.STATUS_CODE, "200")
                            .with(SegmentKey.CONTENT)
                            .with(SegmentKey.CONTENT_TYPE, contentType);

                    String exampleFileName = OpenApiUtils.exampleFileNameFrom(responseNavigationPath, context);
                    String assetPath = context.createAsset(exampleFileName, mediaTypeObject.getExample().data());
                    SuccessExample successExample = new SuccessExample(contentType, assetPath);
                    return Optional.of(successExample);
                }
            }
        }
        return Optional.empty();
    }

    private String getOrDefault(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    abstract String getHttpMethod();

    abstract OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition);

    static class SuccessExample {

        final String contentType;
        final String assetResourceFile;

        SuccessExample(String contentType, String assetResourceFile) {
            this.contentType = contentType;
            this.assetResourceFile = assetResourceFile;
        }
    }
}
