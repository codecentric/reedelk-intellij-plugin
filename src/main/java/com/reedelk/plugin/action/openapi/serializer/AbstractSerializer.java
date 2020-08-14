package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.template.AssetProperties;

import java.util.Map;
import java.util.Optional;

import static com.reedelk.openapi.v3.model.MediaTypeObject.Properties.SCHEMA;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

abstract class AbstractSerializer<T> implements Serializer<T> {

    private final String propertyInlineSchema = "inlineSchema";

    protected final OpenApiImporterContext context;

    protected AbstractSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    protected void setSchema(String propertyKey, NavigationPath navigationPath, Map<String, Object> dataMap, Schema schema, String finalFileName) {
        // It is a schema reference
        if (isNotBlank(schema.getSchemaId())) {
            Optional<String> schemaAsset = context.assetFrom(schema.getSchemaId());
            schemaAsset.ifPresent(schemaAssetPath -> dataMap.put(SCHEMA.value(), schemaAssetPath));

        } else if (schema.getSchemaData() != null){
            // We must create a schema asset.
            String data = context.getSchemaFormat().dump(schema);
            AssetProperties properties = new AssetProperties(data);

            String schemaAssetPath = context.createAsset(finalFileName, properties);

            dataMap.put(propertyKey, schemaAssetPath);
            dataMap.put(propertyInlineSchema, true);
        }
    }
}
