package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ParameterObject;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ParameterObjectSerializer extends com.reedelk.openapi.v3.serializer.ParameterObjectSerializer {

    private final OpenApiImporterContext context;

    public ParameterObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ParameterObject input) {
        Map<String, Object> serialize = super.serialize(serializerContext, navigationPath, input);
        if (input.getSchema() != null) {
            Properties properties = new Properties();
            properties.put("schema", new Yaml().dump(input.getSchema().getSchemaData()));  // TODO: Might be JSON instead of YAML

            String finalFileName = schemaFileNameFrom(navigationPath);
            String schemaAssetPath = context.createSchema(finalFileName, properties);
            serialize.put("schema", schemaAssetPath);

        }
        return serialize;
    }

    @NotNull
    private String schemaFileNameFrom(NavigationPath navigationPath) {
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
}
