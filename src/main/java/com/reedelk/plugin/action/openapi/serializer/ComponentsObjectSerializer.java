package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.DataFormat;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.OpenApiExampleFormat;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.template.AssetProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.reedelk.openapi.v3.model.ComponentsObject.Properties.REQUEST_BODIES;
import static com.reedelk.openapi.v3.model.ComponentsObject.Properties.SCHEMAS;
import static com.reedelk.openapi.v3.model.SchemaObject.Properties;

class ComponentsObjectSerializer extends com.reedelk.openapi.v3.serializer.ComponentsObjectSerializer {

    private final OpenApiImporterContext context;

    public ComponentsObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ComponentsObject componentsObject) {
        Map<String, Object> serialized = super.serialize(serializerContext, navigationPath, componentsObject);
        serialized.remove(REQUEST_BODIES.value());
        serialized.remove(SCHEMAS.value());

        // Request bodies are not serialized here.
        Map<String, RequestBodyObject> requestBodies = componentsObject.getRequestBodies();
        requestBodies.forEach(context::registerRequestBody);

        Map<String, Object> schemasMap = new LinkedHashMap<>();
        Map<String, SchemaObject> schemas = componentsObject.getSchemas();
        schemas.forEach((schemaId, schemaObject) -> {
            // Create schema schemaObject.getSchema().getSchemaData()
            // For each schema we must create a file and assign an ID.
            Schema schema = schemaObject.getSchema();
            if (schema.getSchemaData() != null) {

                // Create Asset
                DataFormat schemaFormat = context.getSchemaFormat();
                String data = schemaFormat.dump(schemaObject.getSchema().getSchemaData());
                AssetProperties properties = new AssetProperties(data);


                // TODO: Refactor the final file name to use the file name from openapi utils
                String finalFileName = schemaId + "." +
                        NavigationPath.SegmentKey.SCHEMA.getKey() + "." +
                        schemaFormat.getExtension();
                String schemaAssetPath = context.createAsset(finalFileName, properties);

                // Register Asset Path
                context.registerAssetPath(schemaId, schemaAssetPath);

                // Put an entry "schema": "assets/my-schema.json"
                Map<String, Object> schemaMap = new LinkedHashMap<>();
                schemaMap.put(Properties.SCHEMA.value(), schemaAssetPath);
                schemasMap.put(schemaId, schemaMap);
            }
        });

        // We only set the "schemas" object if there are schemas registered.
        if (!schemasMap.isEmpty()) set(serialized, SCHEMAS.value(), schemasMap);

        // We need to create the assets for all the examples
        Map<String, Object> examplesMap = new LinkedHashMap<>();
        Map<String, ExampleObject> examples = componentsObject.getExamples();
        examples.forEach(new BiConsumer<String, ExampleObject>() {
            @Override
            public void accept(String exampleId, ExampleObject exampleObject) {
                // Create Asset
                String data = exampleObject.getValue();
                OpenApiExampleFormat exampleFormat = context.exampleFormatOf(data);

                AssetProperties properties = new AssetProperties(data);

                NavigationPath currentNavigationPath = navigationPath
                        .with(NavigationPath.SegmentKey.EXAMPLES)
                        .with(NavigationPath.SegmentKey.EXAMPLE_ID, exampleId);
                String finalFileName = OpenApiUtils.exampleFileNameFrom(currentNavigationPath, exampleFormat);

                String exampleAssetPath = context.createAsset(finalFileName, properties);

                // Register Asset Path
                context.registerAssetPath(exampleId, exampleAssetPath);

                // Put an entry "schema": "assets/my-schema.json"
                Map<String, Object> exampleMap = new LinkedHashMap<>();
                exampleMap.put("summary", exampleObject.getSummary());
                exampleMap.put("description", exampleObject.getDescription());
                exampleMap.put("externalValue", exampleObject.getExternalValue());
                exampleMap.put("value", exampleAssetPath);
                examplesMap.put(exampleId, exampleMap);

            }
        });
        serialized.put(ComponentsObject.Properties.EXAMPLES.value(), examplesMap);

        return serialized;
    }
}
