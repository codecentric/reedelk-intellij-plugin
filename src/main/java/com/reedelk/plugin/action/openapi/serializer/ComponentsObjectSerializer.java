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
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;

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
                Map<String, Object> schemaData = schemaObject.getSchema().getSchemaData();

                String data = OpenApiSchemaFormat.JSON.equals(schemaFormat) ?
                        new JSONObject(schemaData).toString(2) : // JSON
                        new Yaml().dump(schemaData); // YAML

                String finalFileName = schemaId + "." + schemaFormat.getExtension();
                String schemaAssetPath = context.createAsset(finalFileName, data);

                context.register(schemaId, schemaAssetPath);

                Map<String, Object> schemaMap = new LinkedHashMap<>();
                schemaMap.put(SchemaObject.Properties.SCHEMA.value(), schemaAssetPath);
                schemasMap.put(schemaId, schemaMap);
            }
        });

        map.put(ComponentsObject.Properties.SCHEMAS.value(), schemasMap);
        return map;
    }
}
