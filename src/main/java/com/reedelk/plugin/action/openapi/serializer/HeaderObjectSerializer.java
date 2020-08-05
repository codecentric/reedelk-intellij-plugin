package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.HeaderObject;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HeaderObjectSerializer extends com.reedelk.openapi.v3.serializer.HeaderObjectSerializer {

    private final OpenApiImporterContext context;

    public HeaderObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, HeaderObject input) {
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

        String headerName = segmentKeyValue.get(NavigationPath.SegmentKey.HEADER_NAME);
        fileName.append("header").append("_")
                .append(headerName).append(".")
                .append("schema").append(".")
                .append(context.getSchemaFormat().getExtension());

        return fileName.toString().replace('/', '_');
    }
}
