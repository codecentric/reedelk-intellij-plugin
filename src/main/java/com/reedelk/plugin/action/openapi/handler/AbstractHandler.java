package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.OpenApiExampleFormat;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithPayloadSet;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithResource;
import com.reedelk.runtime.api.message.content.MimeType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

import static com.reedelk.openapi.commons.NavigationPath.SegmentKey;
import static com.reedelk.openapi.commons.NavigationPath.create;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

abstract class AbstractHandler implements Handler {

    private static final String HTTP_SUCCESS_CODE = "200";

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public void accept(OpenApiImporterContext context, String pathEntry, Map<RestMethod, OperationObject> pathDefinition) {
        OperationObject operation = getOperation(pathDefinition);

        Map<String, ResponseObject> responses = operation.getResponses();
        String operationId = operation.getOperationId();
        String configId = context.getRestListenerConfigId();
        String restMethod = getHttpMethod();
        String flowTitle = getFlowTitle(operation, pathEntry, restMethod);
        String flowDescription = OpenApiUtils.escapeNewLines(getOrDefault(operation.getDescription(), flowTitle + " description"));
        String restListenerDescription = "Path: " + pathEntry;
        String restPath = pathEntry;

        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, operationId)
                .with(SegmentKey.PATH, pathEntry)
                .with(SegmentKey.METHOD, restMethod);
        String openApiOperationObject = Serializer.toJson(operation, context, navigationPath);

        String fileName = OpenApiUtils.flowFileNameFrom(navigationPath);

        Optional<SuccessExample> example = findSuccessExampleForFlow(responses, navigationPath, context);
        if (example.isPresent()) {
            // The generated flow will return the example from the project's asset directory.
            SuccessExample successExample = example.get();
            OpenAPIRESTListenerWithResource properties = new OpenAPIRESTListenerWithResource(
                    configId, flowTitle, flowDescription, restListenerDescription,
                    restPath, restMethod, openApiOperationObject,
                    successExample.assetResourceFile, successExample.contentType);
            context.createRestListenerFlowWithExample(fileName, properties);

        } else {
            MimeType responseMimeType = findResponseMimeType(responses, navigationPath, context);
            OpenAPIRESTListenerWithPayloadSet properties = new OpenAPIRESTListenerWithPayloadSet(
                    configId, flowTitle, flowDescription, restListenerDescription,
                    restPath, restMethod, responseMimeType, openApiOperationObject);
            context.createRestListenerFlowWithPayload(fileName, properties);
        }
    }

    abstract String getHttpMethod();

    abstract OperationObject getOperation(Map<RestMethod, OperationObject> pathDefinition);

    @NotNull
    private Optional<SuccessExample> findSuccessExampleForFlow(Map<String, ResponseObject> responses, NavigationPath navigationPath, OpenApiImporterContext context) {
        if (responses.containsKey(HTTP_SUCCESS_CODE)) {
            ResponseObject responseObject = responses.get(HTTP_SUCCESS_CODE);
            Map<String, MediaTypeObject> content = responseObject.getContent();
            for (Map.Entry<String, MediaTypeObject> response : content.entrySet()) {
                String contentType = response.getKey();
                MediaTypeObject mediaTypeObject = response.getValue();
                if (mediaTypeObject.getExample() != null) {
                    return createSuccessExample(navigationPath, context, contentType, mediaTypeObject.getExample());
                }
            }
        }
        return Optional.empty();
    }

    @NotNull
    private MimeType findResponseMimeType(Map<String, ResponseObject> responses, NavigationPath navigationPath, OpenApiImporterContext context) {
        if (responses.containsKey(HTTP_SUCCESS_CODE)) {
            ResponseObject responseObject = responses.get(HTTP_SUCCESS_CODE);
            Map<String, MediaTypeObject> content = responseObject.getContent();
            for (Map.Entry<String, MediaTypeObject> response : content.entrySet()) {
                String contentType = response.getKey();
                return MimeType.parse(contentType, MimeType.TEXT_PLAIN);
            }
        }
        return MimeType.TEXT_PLAIN;
    }

    @NotNull
    private Optional<SuccessExample> createSuccessExample(NavigationPath navigationPath, OpenApiImporterContext context, String contentType, Example example) {
        // We have found an example for 200
        NavigationPath responseNavigationPath = navigationPath
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, HTTP_SUCCESS_CODE)
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, contentType);

        String exampleData = example.data();

        OpenApiExampleFormat exampleFormat = context.exampleFormatOf(exampleData);
        String exampleFileName = OpenApiUtils.exampleFileNameFrom(responseNavigationPath, exampleFormat);

        AssetProperties properties = new AssetProperties(exampleData);
        String assetPath = context.createAsset(exampleFileName, properties);
        SuccessExample successExample = new SuccessExample(contentType, assetPath);
        return Optional.of(successExample);
    }

    @NotNull
    private String getFlowTitle(OperationObject operation, String pathEntry, String method) {
        if (isNotBlank(operation.getSummary())) {
            return operation.getSummary() + " Flow";
        } else {
            return method.toUpperCase() + " " + pathEntry + " Flow";
        }
    }


    private String getOrDefault(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    static class SuccessExample {

        final String contentType;
        final String assetResourceFile;

        SuccessExample(String contentType, String assetResourceFile) {
            this.contentType = contentType;
            this.assetResourceFile = assetResourceFile;
        }
    }
}
