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
        Map<Class<?>, com.reedelk.openapi.Serializer<?>> all = new HashMap<>();
        all.put(ServerVariableObject.class, new ServerVariableObjectSerializer(context));
        all.put(ConfigurationOpenApiObject.class, new ConfigurationOpenApiObjectSerializer(context));
        all.put(RequestBodyObject.class, new RequestBodyObjectSerializer(context));
        all.put(ComponentsObject.class, new ComponentsObjectSerializer(context));
        all.put(MediaTypeObject.class, new MediaTypeObjectSerializer(context));
        all.put(ParameterObject.class, new ParameterObjectSerializer(context));
        all.put(ExampleObject.class, new ExampleObjectSerializer(context));
        all.put(HeaderObject.class, new HeaderObjectSerializer(context));
        all.put(OperationObject.class, new OperationObjectSerializer());
        return all;
    }
}
