package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ComponentsObject;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.openapi.v3.model.SchemaObject;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiSchemaFormat;
import com.reedelk.plugin.template.AssetProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.reedelk.openapi.v3.model.ComponentsObject.Properties.SCHEMAS;
import static com.reedelk.openapi.v3.model.SchemaObject.Properties;

class ComponentsObjectSerializer extends AbstractSerializer<ComponentsObject> {

    public ComponentsObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ComponentsObject componentsObject) {
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
                OpenApiSchemaFormat schemaFormat = context.getSchemaFormat();
                String data = schemaFormat.dump(schemaObject.getSchema());
                AssetProperties properties = new AssetProperties(data);

                String finalFileName = schemaId + "." + schemaFormat.getExtension();
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
        Map<String, Object> map = new LinkedHashMap<>();
        if (!schemasMap.isEmpty()) set(map, SCHEMAS.value(), schemasMap);
        return map;
    }
}
