package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
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

class ComponentsObjectSerializer implements Serializer<ComponentsObject> {

    private final OpenApiImporterContext context;

    public ComponentsObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, ComponentsObject componentsObject) {
        Map<String, Object> map = new LinkedHashMap<>();

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

                OpenApiSchemaFormat schemaFormat = context.getSchemaFormat();
                String data = schemaFormat.dump(schemaObject.getSchema());
                AssetProperties properties = new AssetProperties(data);

                String finalFileName = schemaId + "." + schemaFormat.getExtension();
                String schemaAssetPath = context.createAsset(finalFileName, properties);

                context.registerAssetPath(schemaId, schemaAssetPath);

                Map<String, Object> schemaMap = new LinkedHashMap<>();
                schemaMap.put(SchemaObject.Properties.SCHEMA.value(), schemaAssetPath);
                schemasMap.put(schemaId, schemaMap);
            }
        });

        if (!schemasMap.isEmpty()) map.put(SCHEMAS.value(), schemasMap);
        return map;
    }
}
