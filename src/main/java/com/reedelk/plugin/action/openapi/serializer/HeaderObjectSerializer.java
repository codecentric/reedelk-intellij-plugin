package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.commons.PredefinedSchema;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.HeaderObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.reedelk.openapi.v3.model.HeaderObject.Properties;

class HeaderObjectSerializer extends AbstractSerializer<HeaderObject> {

    com.reedelk.openapi.v3.serializer.HeaderObjectSerializer original =
            new com.reedelk.openapi.v3.serializer.HeaderObjectSerializer();

    // Reedelk REST Listener only property. This is not part of the OpenAPI specification.
    public static final String propertyPredefinedSchema = "predefinedSchema";
    public static final String propertyPredefinedSchemaNone = "NONE";

    public HeaderObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, HeaderObject input) {
        Map<String, Object> serialize = original.serialize(serializerContext, navigationPath, input);
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
            serialize.put(Properties.SCHEMA.value(), null);

        } else {
            String finalFileName = OpenApiUtils.headerSchemaFileNameFrom(navigationPath, context);
            setSchema(Properties.SCHEMA.value(), navigationPath, serialize, schema, finalFileName);
            serialize.put(propertyPredefinedSchema, propertyPredefinedSchemaNone);
        }

        return serialize;
    }
}
