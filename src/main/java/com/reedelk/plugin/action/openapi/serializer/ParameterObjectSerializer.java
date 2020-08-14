package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.commons.PredefinedSchema;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ParameterObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.reedelk.openapi.v3.model.ParameterObject.Properties.SCHEMA;
import static com.reedelk.plugin.action.openapi.serializer.HeaderObjectSerializer.propertyPredefinedSchema;
import static com.reedelk.plugin.action.openapi.serializer.HeaderObjectSerializer.propertyPredefinedSchemaNone;

class ParameterObjectSerializer extends AbstractSerializer<ParameterObject> {

    com.reedelk.openapi.v3.serializer.ParameterObjectSerializer original =
            new com.reedelk.openapi.v3.serializer.ParameterObjectSerializer();

    public ParameterObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ParameterObject input) {
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
            serialize.put(SCHEMA.value(), null);

        } else {
            String finalFileName = OpenApiUtils.parameterSchemaFileNameFrom(navigationPath, context);
            setSchema(SCHEMA.value(), navigationPath, serialize, schema, finalFileName);
            serialize.put(propertyPredefinedSchema, propertyPredefinedSchemaNone);
        }

        return serialize;
    }
}
