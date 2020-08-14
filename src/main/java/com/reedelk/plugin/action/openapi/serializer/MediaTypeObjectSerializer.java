package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.Example;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

class MediaTypeObjectSerializer implements Serializer<MediaTypeObject> {

    private final OpenApiImporterContext context;

    public MediaTypeObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, MediaTypeObject mediaTypeObject) {
        Map<String,Object> dataMap = new LinkedHashMap<>();

        Example example = mediaTypeObject.getExample();
        if (example != null) {
            // We must create an example asset.
            String finalFileName = OpenApiUtils.exampleFileNameFrom(navigationPath, context);
            String exampleAssetPath = context.createAsset(finalFileName, example.data());
            dataMap.put(MediaTypeObject.Properties.EXAMPLE.value(), exampleAssetPath);
        }

        // Replace all schemas with reference object to the Resource Text.
        Schema schema = mediaTypeObject.getSchema();
        if (schema != null) {
            // It is a schema reference
            if (isNotBlank(schema.getSchemaId())) {
                Optional<String> schemaAsset = context.assetFrom(schema.getSchemaId());
                schemaAsset.ifPresent(schemaAssetPath -> dataMap.put(MediaTypeObject.Properties.SCHEMA.value(), schemaAssetPath));

            } else if (schema.getSchemaData() != null){
                // We must create a schema asset.
                String finalFileName = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);
                String data = context.getSchemaFormat().dump(schema);
                String schemaAssetPath = context.createAsset(finalFileName, data);
                dataMap.put(MediaTypeObject.Properties.SCHEMA.value(), schemaAssetPath);
            }
        }

        return dataMap;
    }
}
