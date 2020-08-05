package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.commons.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public class MediaTypeObjectSerializer implements Serializer<MediaTypeObject> {

    private final OpenApiImporterContext context;

    public MediaTypeObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, MediaTypeObject mediaTypeObject) {
        // TODO: Example
        //Example example = mediaTypeObject.getExample();

        // Replace all schemas with reference object to the Resource Text.
        // Example?
        Schema schema = mediaTypeObject.getSchema();

        if (schema != null) {
            // It is a schema reference
            if (StringUtils.isNotBlank(schema.getSchemaId())) {
                Optional<String> schemaAsset = context.assetFrom(schema.getSchemaId());
                if (schemaAsset.isPresent()) {
                    return ImmutableMap.of("schema", schemaAsset.get());
                }


            } else if (schema.getSchemaData() != null){
                // We must create a schema asset.
                Properties properties = new Properties();
                properties.put("schema", new Yaml().dump(schema.getSchemaData()));

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

                String statusCode = segmentKeyValue.get(NavigationPath.SegmentKey.STATUS_CODE);
                String contentType = segmentKeyValue.get(NavigationPath.SegmentKey.CONTENT_TYPE);
                fileName.append("response").append("_")
                        .append(statusCode).append("_")
                        .append(contentType).append("_")
                        .append("schema.json");// TODO: OR .YAML

                String schemaAssetPath = context.createSchema(fileName.toString(), properties);
                return ImmutableMap.of("schema", schemaAssetPath);
            }
        }
        return new LinkedHashMap<>();
    }
}
