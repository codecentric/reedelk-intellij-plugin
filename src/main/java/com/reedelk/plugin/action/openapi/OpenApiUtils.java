package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class OpenApiUtils {

    public static String configTitleOf(OpenApiObject openApiObject) {
        String openApiName = message("openapi.importer.config.default.file.title");
        if (openApiObject.getInfo() != null) {
            openApiName = openApiObject.getInfo().getTitle();
        }
        return openApiName + " Configuration";
    }

    public static String configFileNameOf(OpenApiObject openApiObject) {
        String openApiName = message("openapi.importer.config.default.file.name");
        if (openApiObject.getInfo() != null) {
            openApiName = openApiObject.getInfo().getTitle();
        }
        return normalize(openApiName) + "_configuration" + "." + FileExtension.CONFIG.value();
    }

    public static String normalize(String value) {
        return value.replaceAll(" ", "_");
    }

    @NotNull
    public static String exampleFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder baseFileName = baseFindFile(navigationPath);
        String statusCode = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.STATUS_CODE);
        String contentType = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.CONTENT_TYPE);
        baseFileName.append("response").append("_")
                .append(statusCode).append("_")
                .append(contentType).append(".")
                .append("example").append(".")
                .append(context.getSchemaFormat().getExtension());
        return normalizeFileName(baseFileName.toString());
    }

    @NotNull
    public static String schemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder baseFileName = baseFindFile(navigationPath);
        if (findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.REQUEST_BODY) != null) {
            // It is request body.
            String contentType = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.CONTENT_TYPE);
            baseFileName.append("requestBody").append("_")
                    .append(contentType).append(".")
                    .append("schema").append(".")
                    .append(context.getSchemaFormat().getExtension());
            return normalizeFileName(baseFileName.toString());

        } else {
            // It is a response.
            String statusCode = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.STATUS_CODE);
            String contentType = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.CONTENT_TYPE);
            baseFileName.append("response").append("_")
                    .append(statusCode).append("_")
                    .append(contentType).append(".")
                    .append("schema").append(".")
                    .append(context.getSchemaFormat().getExtension());
            return normalizeFileName(baseFileName.toString());
        }
    }

    @NotNull
    public static String parameterSchemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseFindFile(navigationPath);
        String parameterName = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.PARAMETER_NAME);
        fileName.append("parameter").append("_")
                .append(parameterName).append(".")
                .append("schema").append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString().replace('/', '_');
    }

    @NotNull
    public static String headerFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseFindFile(navigationPath);
        String headerName = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.HEADER_NAME);
        fileName.append("header").append("_")
                .append(headerName).append(".")
                .append("schema").append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString().replace('/', '_');
    }

    /**
     * operationId + 'response' + statusCode + contentType.schema.[json|yaml]
     * or
     * method + path + 'response' + statusCode + contentType.schema.[json|yaml]
     * [paths, /pet/{petId} (path), GET (method), getPetById (operationId), responses, 200 (statusCode), content, application/xml (contentType), schema]
     */
    @NotNull
    private static StringBuilder baseFindFile(NavigationPath navigationPath) {
        String operationId = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.OPERATION_ID);
        StringBuilder fileName = new StringBuilder();
        if (StringUtils.isNotBlank(operationId)) {
            fileName.append(operationId).append("_");
        } else {
            String method = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.METHOD);
            String path = findSegmentValueFrom(navigationPath, NavigationPath.SegmentKey.PATH);
            fileName = new StringBuilder()
                    .append(method).append("_")
                    .append(path).append("_");
        }
        return fileName;
    }

    private static String normalizeFileName(String originalName) {
        return originalName.replace('/', '_');
    }

    private static String findSegmentValueFrom(NavigationPath navigationPath, NavigationPath.SegmentKey segmentKey) {
        List<NavigationPath.PathSegment> pathList = navigationPath.getPathList();
        Optional<NavigationPath.PathSegment> matchingPathSegment = pathList.stream()
                .filter(pathSegment -> segmentKey.equals(pathSegment.getSegmentKey()))
                .findFirst();
        return matchingPathSegment.map(NavigationPath.PathSegment::getSegmentValue).orElse(null);
    }
}
