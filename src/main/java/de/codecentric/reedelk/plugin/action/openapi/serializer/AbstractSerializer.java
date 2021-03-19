package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.openapi.v3.model.Schema;
import de.codecentric.reedelk.plugin.template.AssetProperties;

import java.util.Map;
import java.util.Optional;

import static de.codecentric.reedelk.openapi.v3.model.MediaTypeObject.Properties.SCHEMA;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

abstract class AbstractSerializer<T> extends de.codecentric.reedelk.openapi.commons.AbstractSerializer<T> {

    protected final OpenApiImporterContext context;

    protected AbstractSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    protected void setSchema(String propertyKey, Map<String, Object> dataMap, Schema schema, String finalFileName) {
        // It is a schema reference
        if (isNotBlank(schema.getSchemaId())) {
            Optional<String> schemaAsset = context.getAssetFrom(schema.getSchemaId());
            schemaAsset.ifPresent(schemaAssetPath -> dataMap.put(SCHEMA.value(), schemaAssetPath));

        } else if (schema.getSchemaData() != null){
            // We must create a schema asset.
            String data = context.getSchemaFormat().dump(schema.getSchemaData());
            AssetProperties properties = new AssetProperties(data);

            String schemaAssetPath = context.createAsset(finalFileName, properties);

            dataMap.put(propertyKey, schemaAssetPath);
            dataMap.put(OpenApiConstants.PROPERTY_INLINE_SCHEMA, true);
        }
    }
}
