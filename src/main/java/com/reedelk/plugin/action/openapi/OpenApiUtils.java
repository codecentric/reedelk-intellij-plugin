package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.reedelk.openapi.commons.NavigationPath.PathSegment;
import static com.reedelk.openapi.commons.NavigationPath.SegmentKey;
import static com.reedelk.openapi.commons.NavigationPath.SegmentKey.*;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class OpenApiUtils {

    private OpenApiUtils() {
    }

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
    public static String flowFileNameFrom(NavigationPath navigationPath) {
        return removeUnderscoreIfNeeded(baseOperationAwareFile(navigationPath))
                + "." + FileExtension.FLOW.value();
    }

    @NotNull
    public static String exampleFileNameFrom(NavigationPath navigationPath, OpenApiExampleFormat exampleFormat) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);

        if (segmentValueOf(navigationPath, REQUEST_BODY) != null) {
            // Example for Request Body
            appendUnderscoreIfNeeded(fileName).append(REQUEST_BODY.getKey());
        }
        if (segmentValueOf(navigationPath, RESPONSES) != null) {
            // Example for Response Body
            appendUnderscoreIfNeeded(fileName).append(RESPONSE.getKey());
        }
        if (segmentValueOf(navigationPath, STATUS_CODE) != null) {
            String statusCode = segmentValueOf(navigationPath, STATUS_CODE);
            appendUnderscoreIfNeeded(fileName).append(statusCode);
        }
        if (segmentValueOf(navigationPath, CONTENT_TYPE) != null) {
            String contentType = segmentValueOf(navigationPath, CONTENT_TYPE);
            appendUnderscoreIfNeeded(fileName).append(normalizeContentType(contentType));
        }
        if (segmentValueOf(navigationPath, EXAMPLE_ID) != null) {
            // Examples from components (they don't belong to a request or response)
            String exampleId = segmentValueOf(navigationPath, EXAMPLE_ID);
            appendUnderscoreIfNeeded(fileName).append(capitalize(exampleId));
        }

        fileName.append(".").append(EXAMPLE.getKey()).append(".")
                .append(exampleFormat.getExtension());
        return fileName.toString();
    }

    @NotNull
    public static String requestResponseSchemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        if (segmentValueOf(navigationPath, REQUEST_BODY) != null) {
            // It is request body.
            String contentType = segmentValueOf(navigationPath, CONTENT_TYPE);
            fileName.append(REQUEST_BODY.getKey()).append("_")
                    .append(normalizeContentType(contentType)).append(".")
                    .append(SCHEMA.getKey()).append(".")
                    .append(context.getSchemaFormat().getExtension());
            return fileName.toString();

        } else {
            // It is a response.
            String statusCode = segmentValueOf(navigationPath, STATUS_CODE);
            String contentType = segmentValueOf(navigationPath, CONTENT_TYPE);
            fileName.append(RESPONSE.getKey()).append("_")
                    .append(statusCode).append("_")
                    .append(normalizeContentType(contentType)).append(".")
                    .append(SCHEMA.getKey()).append(".")
                    .append(context.getSchemaFormat().getExtension());
            return fileName.toString();
        }
    }

    public static String schemaFileNameFrom(String schemaId, OpenApiImporterContext context) {
        return schemaId + "." +
                NavigationPath.SegmentKey.SCHEMA.getKey() + "." +
                context.getSchemaFormat().getExtension();
    }

    @NotNull
    public static String parameterSchemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        String parameterName = segmentValueOf(navigationPath, PARAMETER_NAME);
        fileName.append(PARAMETER.getKey()).append("_")
                .append(parameterName).append(".")
                .append(SCHEMA.getKey()).append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString();
    }

    @NotNull
    public static String headerSchemaFileNameFrom(NavigationPath navigationPath, OpenApiImporterContext context) {
        StringBuilder fileName = baseOperationAwareFile(navigationPath);
        String statusCode = segmentValueOf(navigationPath, STATUS_CODE);
        String headerName = segmentValueOf(navigationPath, HEADER_NAME);
        fileName.append(RESPONSE.getKey()).append("_")
                .append(statusCode).append("_")
                .append(HEADER.getKey()).append("_")
                .append(headerName).append(".")
                .append(SCHEMA.getKey()).append(".")
                .append(context.getSchemaFormat().getExtension());
        return fileName.toString();
    }

    @NotNull
    public static String getApiTitle(OpenApiObject openApiObject) {
        return getApiTitle(openApiObject, message("openapi.importer.config.default.file.title"));
    }

    @Nullable
    public static String escapeNewLines(String value) {
        if (value == null) return value;
        return value.replaceAll("\n", "\\\\n");
    }

    public static String escapeSingleQuotes(String value) {
        if (value == null) return value;
        return value.replaceAll("'", "\\\\'");
    }

    public static boolean isNotEmpty(List<?> value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isNotEmpty(Map<?,?> value) {
        return value != null && !value.isEmpty();
    }

    /**
     * Returns base file name given the navigation path. e.g given navigation path:
     * <p>
     * Navigation path: [
     * paths, /pet/{petId} (path), GET (method), getPetById (operationId),
     * responses, 200 (statusCode), content, application/xml (contentType), schema]
     * <p>
     * returns 'getPetById' if operation id is not empty, otherwise 'getPetPetId'.
     */
    @NotNull
    private static StringBuilder baseOperationAwareFile(NavigationPath navigationPath) {
        String operationId = segmentValueOf(navigationPath, OPERATION_ID);
        StringBuilder fileName = new StringBuilder();
        if (isNotBlank(operationId)) {
            fileName.append(operationId);
        } else {
            String method = segmentValueOf(navigationPath, METHOD);
            String path = segmentValueOf(navigationPath, PATH);
            if (method != null) fileName.append(method.toUpperCase());
            if (path != null) fileName.append(normalizePath(path));
        }
        return appendUnderscoreIfNeeded(fileName);
    }

    @Nullable
    private static String segmentValueOf(NavigationPath navigationPath, SegmentKey segmentKey) {
        List<PathSegment> pathList = navigationPath.getPathList();
        Optional<PathSegment> matchingPathSegment = pathList.stream()
                .filter(pathSegment -> segmentKey.equals(pathSegment.getSegmentKey()))
                .findFirst();
        return matchingPathSegment.map(PathSegment::getSegmentValue).orElse(null);
    }

    @NotNull
    private static String getApiTitle(OpenApiObject openApiObject, String defaultValue) {
        InfoObject info = openApiObject.getInfo();
        return info != null && isNotBlank(info.getTitle()) ?
                info.getTitle() : defaultValue;
    }

    @NotNull
    private static String normalizeContentType(String contentType) {
        return contentType != null ?
                contentType.replace('/', '_') :
                StringUtils.EMPTY;
    }

    @NotNull
    private static String normalizePath(String path) {
        String[] pathSegments = path.split("/");
        return Arrays.stream(pathSegments)
                .map(OpenApiUtils::removeSpecialChars)
                .map(OpenApiUtils::capitalize)
                .collect(Collectors.joining());
    }

    @NotNull
    private static String removeWhiteSpaces(String value) {
        return value.replaceAll(" ", "");
    }

    @NotNull
    private static String removeSpecialChars(String value) {
        String result = value.replaceAll("\\{", "");
        result = result.replaceAll("}", "");
        result = result.replaceAll(":", "");
        result = result.replaceAll("\\*", "");
        return result;
    }

    @NotNull
    private static String capitalize(String value) {
        if (value == null) return StringUtils.EMPTY;
        if (value.length() == 0) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private static StringBuilder appendUnderscoreIfNeeded(StringBuilder builder) {
        if (builder.length() == 0) return builder;
        return builder.charAt(builder.length() - 1) == '_' ? builder : builder.append("_");
    }

    private static StringBuilder removeUnderscoreIfNeeded(StringBuilder builder) {
        if (builder.length() == 0) return builder;
        return builder.charAt(builder.length() - 1) == '_' ? builder.deleteCharAt(builder.length() - 1) : builder;
    }
}
