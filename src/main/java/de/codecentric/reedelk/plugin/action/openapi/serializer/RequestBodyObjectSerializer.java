package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.intellij.openapi.diagnostic.Logger;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.MediaTypeObject;
import de.codecentric.reedelk.openapi.v3.model.RequestBodyObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static de.codecentric.reedelk.openapi.v3.model.RequestBodyObject.Properties.*;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

class RequestBodyObjectSerializer extends AbstractSerializer<RequestBodyObject> {

    private static final Logger LOG = Logger.getInstance(RequestBodyObjectSerializer.class);

    public RequestBodyObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, RequestBodyObject requestBodyObject) {
        Map<String, Object> map = new LinkedHashMap<>();

        RequestBodyObject resolvedRequestBodyObject = requestBodyObject;

        // This is the case where the RequestBody is expressed as a reference. At the moment
        // we don't support this use case, and the components OpenAPI json object does not
        // keep commons request bodies. Therefore we need to find the referenced request body object.
        String requestBodyReference = requestBodyObject.get$ref();
        if (isNotBlank(requestBodyReference)) {
            String[] segments = requestBodyReference.split("/");
            if (segments.length > 1) {
                // Last segment is the id of the request body object.
                String requestBodyId = segments[segments.length - 1];
                Optional<RequestBodyObject> maybeResolvedRequestBodyObject = context.getRequestBodyById(requestBodyId);
                if (maybeResolvedRequestBodyObject.isPresent()) {
                    resolvedRequestBodyObject = maybeResolvedRequestBodyObject.get();
                } else {
                    // Could not resolve request body object (perhaps there was a typo in the ID by the user),
                    // we just returned the map.
                    LOG.warn(String.format("Could not resolve request body for reference id=[%s]", requestBodyId));
                    return map;
                }
            }
        }

        set(map, DESCRIPTION.value(), resolvedRequestBodyObject.getDescription());

        Map<String, MediaTypeObject> requestBodyObjectContent = resolvedRequestBodyObject.getContent();
        if (requestBodyObjectContent != null) {

            if (requestBodyObjectContent.isEmpty()) {
                map.put(CONTENT.value(), new LinkedHashMap<>());
            } else {
                Map<String, Map<String, Object>> contentTypeMediaTypeMap = new LinkedHashMap<>();
                requestBodyObjectContent.forEach((contentType, mediaTypeObject) -> {
                    NavigationPath currentNavigationPath = navigationPath
                            .with(NavigationPath.SegmentKey.CONTENT)
                            .with(NavigationPath.SegmentKey.CONTENT_TYPE, contentType);
                    Map<String, Object> serialized = serializerContext.serialize(currentNavigationPath, mediaTypeObject);
                    contentTypeMediaTypeMap.put(contentType, serialized);
                });
                map.put(CONTENT.value(), contentTypeMediaTypeMap);
            }
        }

        set(map, REQUIRED.value(), resolvedRequestBodyObject.getRequired());
        return map;
    }
}
