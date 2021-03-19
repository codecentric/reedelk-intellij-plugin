package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.commons.PredefinedSchema;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.ParameterObject;
import de.codecentric.reedelk.openapi.v3.model.Schema;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static de.codecentric.reedelk.openapi.v3.model.ParameterObject.Properties.SCHEMA;

class ParameterObjectSerializer extends AbstractSerializer<ParameterObject> {

    de.codecentric.reedelk.openapi.v3.serializer.ParameterObjectSerializer original =
            new de.codecentric.reedelk.openapi.v3.serializer.ParameterObjectSerializer();

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
            serialize.put(OpenApiConstants.PROPERTY_PREDEFINED_SCHEMA, isPredefinedSchema.get().name());
            // schema must be removed because the super.serialize would serialize the schema inline.
            // The REST listener expects the asset path (a string), rather than the inline schema definition.
            serialize.remove(SCHEMA.value());

        } else {
            String finalFileName = OpenApiUtils.parameterSchemaFileNameFrom(navigationPath, context);
            setSchema(SCHEMA.value(), serialize, schema, finalFileName);
            serialize.put(OpenApiConstants.PROPERTY_PREDEFINED_SCHEMA, OpenApiConstants.PROPERTY_PREDEFINED_SCHEMA_NONE);
        }

        return serialize;
    }
}
