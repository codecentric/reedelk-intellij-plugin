package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenApiUtils {

    private static final String DEFAULT_TITLE = "my_api";

    public static String configFileNameOf(OpenApiObject openApiObject) {
        String openApiName = DEFAULT_TITLE;
        if (openApiObject.getInfo() != null) {
            openApiName = openApiObject.getInfo().getTitle();
        }
        return StringUtils.isBlank(openApiName) ?
                DEFAULT_TITLE + "." + FileExtension.CONFIG.value() :
                normalize(openApiName) + "." + FileExtension.CONFIG.value();
    }

    public static String normalize(String value) {
        return value.replaceAll(" ", "_");
    }

    @NotNull
    public static String schemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        List<NavigationPath.PathSegment> pathList = navigationPath.getPathList();

        Map<NavigationPath.SegmentKey,String> segmentKeyValue = new HashMap<>();

        pathList.forEach(pathSegment ->
                segmentKeyValue.put(pathSegment.getSegmentKey(), pathSegment.getSegmentValue()));

        if (segmentKeyValue.containsKey(NavigationPath.SegmentKey.REQUEST_BODY)) {
            // It is request body.
            // It is a response.

            String operationId = segmentKeyValue.get(NavigationPath.SegmentKey.OPERATION_ID);
            StringBuilder fileName = new StringBuilder();

            // operationId + 'response' + statusCode + contentType.schema.[json|yaml]
            // or
            // method + path + 'response' + statusCode + contentType.schema.[json|yaml]
            //[paths, /pet/{petId} (path), GET (method), getPetById (operationId), responses, 200 (statusCode), content, application/xml (contentType), schema]

            if (StringUtils.isNotBlank(operationId)) {
                fileName.append(operationId).append("_");

            } else {
                String method = segmentKeyValue.get(NavigationPath.SegmentKey.METHOD);
                String path = segmentKeyValue.get(NavigationPath.SegmentKey.PATH);
                fileName = new StringBuilder()
                        .append(method).append("_")
                        .append(path).append("_");

            }

            String contentType = segmentKeyValue.get(NavigationPath.SegmentKey.CONTENT_TYPE);
            fileName.append("requestBody").append("_")
                    .append(contentType).append(".")
                    .append("schema").append(".")
                    .append(context.getSchemaFormat().getExtension());


            return normalizeFileName(fileName.toString());

        } else {
            // It is a response.

            String operationId = segmentKeyValue.get(NavigationPath.SegmentKey.OPERATION_ID);
            StringBuilder fileName = new StringBuilder();

            // operationId + 'response' + statusCode + contentType.schema.[json|yaml]
            // or
            // method + path + 'response' + statusCode + contentType.schema.[json|yaml]
            //[paths, /pet/{petId} (path), GET (method), getPetById (operationId), responses, 200 (statusCode), content, application/xml (contentType), schema]

            if (StringUtils.isNotBlank(operationId)) {
                fileName.append(operationId).append("_");

            } else {
                String method = segmentKeyValue.get(NavigationPath.SegmentKey.METHOD);
                String path = segmentKeyValue.get(NavigationPath.SegmentKey.PATH);
                fileName = new StringBuilder()
                        .append(method).append("_")
                        .append(path).append("_");

            }

            String statusCode = segmentKeyValue.get(NavigationPath.SegmentKey.STATUS_CODE);
            String contentType = segmentKeyValue.get(NavigationPath.SegmentKey.CONTENT_TYPE);
            fileName.append("response").append("_")
                    .append(statusCode).append("_")
                    .append(contentType).append(".")
                    .append("schema").append(".")
                    .append(context.getSchemaFormat().getExtension());

            return normalizeFileName(fileName.toString());
        }
    }

    @NotNull
    public static String parameterSchemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        List<NavigationPath.PathSegment> pathList = navigationPath.getPathList();

        Map<NavigationPath.SegmentKey,String> segmentKeyValue = new HashMap<>();

        pathList.forEach(pathSegment ->
                segmentKeyValue.put(pathSegment.getSegmentKey(), pathSegment.getSegmentValue()));

        String operationId = segmentKeyValue.get(NavigationPath.SegmentKey.OPERATION_ID);
        StringBuilder fileName = new StringBuilder();

        // operationId + 'response' + statusCode + contentType.schema.[json|yaml]
        // or
        // method + path + 'response' + statusCode + contentType.schema.[json|yaml]
        //[paths, /pet/{petId} (path), GET (method), getPetById (operationId), responses, 200 (statusCode), content, application/xml (contentType), schema]

        if (StringUtils.isNotBlank(operationId)) {
            fileName.append(operationId).append("_");

        } else {
            String method = segmentKeyValue.get(NavigationPath.SegmentKey.METHOD);
            String path = segmentKeyValue.get(NavigationPath.SegmentKey.PATH);
            fileName = new StringBuilder()
                    .append(method).append("_")
                    .append(path).append("_");

        }

        String parameterName = segmentKeyValue.get(NavigationPath.SegmentKey.PARAMETER_NAME);
        fileName.append("parameter").append("_")
                .append(parameterName).append(".")
                .append("schema").append(".")
                .append(context.getSchemaFormat().getExtension());

        return fileName.toString().replace('/', '_');
    }

    private static String normalizeFileName(String originalName) {
        return originalName.replace('/', '_');
    }

    public enum SchemaFormat {

        YAML("yaml"),
        JSON("json");

        final String extension;

        SchemaFormat(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public static SchemaFormat formatOf(String fileName) {
            for (SchemaFormat schemaFormat : SchemaFormat.values()) {
                if (fileName.toLowerCase().endsWith("." + schemaFormat.extension)) {
                    return schemaFormat;
                }
            }
            throw new IllegalArgumentException("Could not find schema format for file with name=[" + fileName + "]");
        }
    }
}
