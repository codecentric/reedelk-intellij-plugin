package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.reedelk.openapi.commons.NavigationPath.PathSegment;
import static com.reedelk.openapi.commons.NavigationPath.SegmentKey;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class OpenApiUtils {

    @NotNull
    public static String restListenerConfigTitleFrom(OpenApiObject openApiObject) {
        String openApiTitle = getApiTitle(openApiObject);
        return openApiTitle + " REST Listener";
    }

    @NotNull
    public static String restListenerConfigFileNameFrom(OpenApiObject openApiObject) {
        String openApiTitle = getApiTitle(openApiObject);
        return removeWhiteSpaces(openApiTitle) + "RESTListener" + "." + FileExtension.CONFIG.value();
    }

    @NotNull
    public static String exampleFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        String statusCode = segmentValueOf(navigationPath, SegmentKey.STATUS_CODE);
        String contentType = segmentValueOf(navigationPath, SegmentKey.CONTENT_TYPE);
        fileName.append("response").append("_")
                .append(statusCode).append("_")
                .append(normalizeContentType(contentType)).append(".")
                .append("example").append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString();
    }

    @NotNull
    public static String schemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        if (segmentValueOf(navigationPath, SegmentKey.REQUEST_BODY) != null) {
            // It is request body.
            String contentType = segmentValueOf(navigationPath, SegmentKey.CONTENT_TYPE);
            fileName.append("requestBody").append("_")
                    .append(normalizeContentType(contentType)).append(".")
                    .append("schema").append(".")
                    .append(context.getSchemaFormat().getExtension());
            return fileName.toString();

        } else {
            // It is a response.
            String statusCode = segmentValueOf(navigationPath, SegmentKey.STATUS_CODE);
            String contentType = segmentValueOf(navigationPath, SegmentKey.CONTENT_TYPE);
            fileName.append("response").append("_")
                    .append(statusCode).append("_")
                    .append(normalizeContentType(contentType)).append(".")
                    .append("schema").append(".")
                    .append(context.getSchemaFormat().getExtension());
            return fileName.toString();
        }
    }

    @NotNull
    public static String parameterSchemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        String parameterName = segmentValueOf(navigationPath, SegmentKey.PARAMETER_NAME);
        fileName.append("parameter").append("_")
                .append(parameterName).append(".")
                .append("schema").append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString();
    }

    @NotNull
    public static String headerFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        String headerName = segmentValueOf(navigationPath, SegmentKey.HEADER_NAME);
        fileName.append("header").append("_")
                .append(headerName).append(".")
                .append("schema").append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString();
    }

    /**
     * Returns base file name given the navigation path. e.g given navigation path:
     *
     * Navigation path: [
     *          paths, /pet/{petId} (path), GET (method), getPetById (operationId),
     *          responses, 200 (statusCode), content, application/xml (contentType), schema]
     *
     * returns 'getPetById' if operation id is not empty, otherwise 'getPetPetId'.
     */
    @NotNull
    private static StringBuilder baseOperationAwareFile(NavigationPath navigationPath) {
        String operationId = segmentValueOf(navigationPath, SegmentKey.OPERATION_ID);
        StringBuilder fileName = new StringBuilder();
        if (isNotBlank(operationId)) {
            fileName.append(operationId).append("_");
        } else {
            String method = segmentValueOf(navigationPath, SegmentKey.METHOD);
            String path = segmentValueOf(navigationPath, SegmentKey.PATH);
            fileName = new StringBuilder()
                    .append(method.toLowerCase())
                    .append(normalizePath(path)).append("_");
        }
        return fileName;
    }

    private static String segmentValueOf(NavigationPath navigationPath, SegmentKey segmentKey) {
        List<PathSegment> pathList = navigationPath.getPathList();
        Optional<PathSegment> matchingPathSegment = pathList.stream()
                .filter(pathSegment -> segmentKey.equals(pathSegment.getSegmentKey()))
                .findFirst();
        return matchingPathSegment.map(PathSegment::getSegmentValue).orElse(null);
    }

    public static String getApiTitle(OpenApiObject openApiObject) {
        return getApiTitle(openApiObject, message("openapi.importer.config.default.file.title"));
    }

    public static String getApiTitle(OpenApiObject openApiObject, String defaultValue) {
        InfoObject info = openApiObject.getInfo();
        return info != null && isNotBlank(info.getTitle()) ?
                info.getTitle() : defaultValue;
    }

    private static String normalizeContentType(String contentType) {
        return contentType.replace('/', '_');
    }

    private static String normalizePath(String path) {
        String[] pathSegments = path.split("/");
        return Arrays.stream(pathSegments)
                .map(OpenApiUtils::removeSpecialChars)
                .map(OpenApiUtils::capitalize)
                .collect(Collectors.joining());
    }

    private static String removeWhiteSpaces(String value) {
        return value.replaceAll(" ", "");
    }

    private static String removeSpecialChars(String value) {
        String result = value.replaceAll("\\{", "");
        result = result.replaceAll("}", "");
        result = result.replaceAll(":", "");
        result = result.replaceAll("\\*", "");
        return result;
    }

    public static String capitalize(String value) {
        if (value == null) return null;
        if (value.length() == 0) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }
}
