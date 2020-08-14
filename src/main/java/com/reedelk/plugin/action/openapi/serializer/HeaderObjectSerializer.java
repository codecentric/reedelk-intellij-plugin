package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.commons.PredefinedSchema;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.HeaderObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.template.AssetProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

class HeaderObjectSerializer extends com.reedelk.openapi.v3.serializer.HeaderObjectSerializer {

    // Reedelk REST Listener only property. This is not part of the OpenAPI specification.
    public static final String propertyPredefinedSchema = "predefinedSchema";
    public static final String propertyPredefinedSchemaNone = "NONE";

    private final OpenApiImporterContext context;

    public HeaderObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, HeaderObject input) {
        Map<String, Object> serialize = super.serialize(serializerContext, navigationPath, input);
        if (input.getSchema() == null) return serialize;

        Schema schema = input.getSchema();
        Map<String, Object> schemaData = schema.getSchemaData();
        Optional<PredefinedSchema> isPredefinedSchema = Arrays.stream(PredefinedSchema.values())
                .filter(predefinedSchema -> predefinedSchema.schema().equals(schemaData))
                .findFirst();

        if (isPredefinedSchema.isPresent()) {
            serialize.put(propertyPredefinedSchema, isPredefinedSchema.get().name());
            // schema must be set to null because the super.serialize would serialize the schema inline.
            // The REST listener expects the asset path (a string), rather than the inline schema definition.
            serialize.put(HeaderObject.Properties.SCHEMA.value(), null);

        } else {
            String data = context.getSchemaFormat().dump(input.getSchema());
            AssetProperties properties = new AssetProperties(data);

            String finalFileName = OpenApiUtils.headerSchemaFileNameFrom(navigationPath, context);
            String schemaAssetPath = context.createAsset(finalFileName, properties);

            serialize.put(propertyPredefinedSchema, propertyPredefinedSchemaNone);
            serialize.put(HeaderObject.Properties.SCHEMA.value(), schemaAssetPath);
        }

        return serialize;
    }
}
