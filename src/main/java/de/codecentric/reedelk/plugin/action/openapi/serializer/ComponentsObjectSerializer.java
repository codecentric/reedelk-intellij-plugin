package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiExampleFormat;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiUtils;
import de.codecentric.reedelk.openapi.commons.DataFormat;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.*;
import de.codecentric.reedelk.plugin.template.AssetProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.codecentric.reedelk.openapi.v3.model.ComponentsObject.Properties.EXAMPLES;
import static de.codecentric.reedelk.openapi.v3.model.ComponentsObject.Properties.SCHEMAS;
import static de.codecentric.reedelk.openapi.v3.model.SchemaObject.Properties;

class ComponentsObjectSerializer extends de.codecentric.reedelk.openapi.v3.serializer.ComponentsObjectSerializer {

    private final OpenApiImporterContext context;

    public ComponentsObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ComponentsObject componentsObject) {
        Map<String, Object> serialized = new LinkedHashMap<>();

        // Request bodies are not serialized here.
        Map<String, RequestBodyObject> requestBodies = componentsObject.getRequestBodies();
        requestBodies.forEach(context::registerRequestBody);

        // Schemas
        Map<String, SchemaObject> schemas = componentsObject.getSchemas();
        if (OpenApiUtils.isNotEmpty(schemas)) serializeSchemas(serialized, schemas);

        // Examples
        Map<String, ExampleObject> examples = componentsObject.getExamples();
        if (OpenApiUtils.isNotEmpty(examples)) serializeExamples(navigationPath, serialized, examples);

        // Security schemes
        Map<String, SecuritySchemeObject> securitySchemesMap = componentsObject.getSecuritySchemes();
        if (OpenApiUtils.isNotEmpty(securitySchemesMap)) {
            super.serializeSecuritySchemas(serializerContext, navigationPath, serialized, securitySchemesMap);
        }

        return serialized;
    }

    public void serializeSchemas(Map<String, Object> serialized, Map<String, SchemaObject> schemas) {
        Map<String, Object> schemasMap = new LinkedHashMap<>();
        schemas.forEach((schemaId, schemaObject) -> {
            // Create schema schemaObject.getSchema().getSchemaData()
            // For each schema we must create a file and assign an ID.
            Schema schema = schemaObject.getSchema();
            if (schema.getSchemaData() == null) return;

            // Create Asset
            DataFormat schemaFormat = context.getSchemaFormat();
            String finalFileName = OpenApiUtils.schemaFileNameFrom(schemaId, context);

            String data = schemaFormat.dump(schemaObject.getSchema().getSchemaData());
            AssetProperties properties = new AssetProperties(data);
            String schemaAssetPath = context.createAsset(finalFileName, properties);

            // Register Asset Path
            context.registerAssetPath(schemaId, schemaAssetPath);

            // Put an entry "schema": "assets/my-schema.json"
            Map<String, Object> schemaMap = new LinkedHashMap<>();
            schemaMap.put(Properties.SCHEMA.value(), schemaAssetPath);
            schemasMap.put(schemaId, schemaMap);
        });
        set(serialized, SCHEMAS.value(), schemasMap);
    }

    public void serializeExamples(NavigationPath navigationPath, Map<String, Object> serialized, Map<String, ExampleObject> examples) {
        Map<String, Object> examplesMap = new LinkedHashMap<>();
        examples.forEach((exampleId, exampleObject) -> {
            // Create Asset
            String data = exampleObject.getValue();
            if (data == null) return;

            OpenApiExampleFormat exampleFormat = context.exampleFormatOf(data);

            NavigationPath currentNavigationPath = navigationPath
                    .with(NavigationPath.SegmentKey.EXAMPLES)
                    .with(NavigationPath.SegmentKey.EXAMPLE_ID, exampleId);
            String finalFileName = OpenApiUtils.exampleFileNameFrom(currentNavigationPath, exampleFormat);

            AssetProperties properties = new AssetProperties(data);
            String exampleAssetPath = context.createAsset(finalFileName, properties);

            // Register Asset
            context.registerAssetPath(exampleId, exampleAssetPath);

            // Serialize the example
            Map<String, Object> exampleMap = new LinkedHashMap<>();
            exampleMap.put(OpenApiConstants.PROPERTY_EXAMPLE_SUMMARY, exampleObject.getSummary());
            exampleMap.put(OpenApiConstants.PROPERTY_EXAMPLE_DESCRIPTION, exampleObject.getDescription());
            exampleMap.put(OpenApiConstants.PROPERTY_EXAMPLE_EXTERNAL_VALUE, exampleObject.getExternalValue());
            exampleMap.put(OpenApiConstants.PROPERTY_EXAMPLE_VALUE, exampleAssetPath);
            examplesMap.put(exampleId, exampleMap);
        });
        set(serialized, EXAMPLES.value(), examplesMap);
    }
}
