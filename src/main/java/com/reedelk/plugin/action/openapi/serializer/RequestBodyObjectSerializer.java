package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.AbstractSerializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.reedelk.openapi.v3.model.RequestBodyObject.Properties;

class RequestBodyObjectSerializer extends AbstractSerializer<RequestBodyObject> {

    private final OpenApiImporterContext context;

    public RequestBodyObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, RequestBodyObject requestBodyObject) {
        Map<String, Object> map = new LinkedHashMap<>();

        RequestBodyObject realRequestBodyObject = requestBodyObject;

        String request$ref = requestBodyObject.get$ref();
        if (StringUtils.isNotBlank(request$ref)) {
            String[] segments = request$ref.split("/");
            // Last segment is the id
            String segment = segments[segments.length - 1];
            realRequestBodyObject = context.getRequestBodyById(segment);
        }

        set(map, Properties.DESCRIPTION.value(), realRequestBodyObject.getDescription());
        if (realRequestBodyObject.getContent().isEmpty()) {
            map.put(Properties.CONTENT.value(), new LinkedHashMap<>());

        } else {
            Map<String, Map<String, Object>> contentTypeMediaTypeMap = new LinkedHashMap<>();
            Map<String, MediaTypeObject> content = realRequestBodyObject.getContent();
            content.forEach((contentType, mediaTypeObject) -> {
                NavigationPath currentNavigationPath = navigationPath
                        .with(NavigationPath.SegmentKey.CONTENT)
                        .with(NavigationPath.SegmentKey.CONTENT_TYPE, contentType);
                Map<String, Object> serialized = serializerContext.serialize(currentNavigationPath, mediaTypeObject);
                contentTypeMediaTypeMap.put(contentType, serialized);
            });
            map.put(Properties.CONTENT.value(), contentTypeMediaTypeMap);
        }

        set(map, Properties.REQUIRED.value(), realRequestBodyObject.getRequired());

        return map;
    }
}
