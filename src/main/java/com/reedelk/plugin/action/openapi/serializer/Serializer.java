package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.OpenApiModel;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;

import java.util.HashMap;
import java.util.Map;

public class Serializer {

    private Serializer() {
    }

    public static String toJson(OpenApiModel openApiModel, OpenApiImporterContext context) {
        return OpenApi.toJson(openApiModel, serializers(context));
    }

    public static String toJson(OpenApiModel openApiModel, OpenApiImporterContext context, NavigationPath navigationPath) {
        return OpenApi.toJson(openApiModel, serializers(context), navigationPath);
    }

    public static Map<Class<?>, com.reedelk.openapi.Serializer<?>> serializers(OpenApiImporterContext context) {
        Map<Class<?>, com.reedelk.openapi.Serializer<?>> serializerMap = new HashMap<>();
        serializerMap.put(ServerVariableObject.class, new ServerVariableObjectSerializer(context));
        serializerMap.put(ConfigOpenApiObject.class, new ConfigOpenApiObjectSerializer(context));
        serializerMap.put(RequestBodyObject.class, new RequestBodyObjectSerializer(context));
        serializerMap.put(ComponentsObject.class, new ComponentsObjectSerializer(context));
        serializerMap.put(MediaTypeObject.class, new MediaTypeObjectSerializer(context));
        serializerMap.put(ParameterObject.class, new ParameterObjectSerializer(context));
        serializerMap.put(HeaderObject.class, new HeaderObjectSerializer(context));
        return serializerMap;
    }
}
