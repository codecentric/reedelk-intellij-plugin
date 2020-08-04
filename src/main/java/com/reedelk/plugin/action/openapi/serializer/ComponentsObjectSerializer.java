package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ComponentsObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.openapi.v3.model.SchemaObject;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.importer.SchemaFormat;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

public class ComponentsObjectSerializer implements Serializer<ComponentsObject> {

    private final OpenApiImporterContext context;
    private final SchemaFormat schemaFormat;

    public ComponentsObjectSerializer(OpenApiImporterContext context, SchemaFormat schemaFormat) {
        this.context = context;
        this.schemaFormat = schemaFormat;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, ComponentsObject componentsObject) {
        // TODO: Response objects?
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> schemasMap = new LinkedHashMap<>();


        Map<String, SchemaObject> schemas = componentsObject.getSchemas();
        schemas.forEach(new BiConsumer<String, SchemaObject>() {
            @Override
            public void accept(String schemaId, SchemaObject schemaObject) {
                // Create schema schemaObject.getSchema().getSchemaData()
                // For each schema we must create a file and assign an ID.
                Schema schema = schemaObject.getSchema();
                if (schema.getSchemaData() != null) {
                    Properties schemaProperties = new Properties();
                    schemaProperties.put("schema", new Yaml().dump(schema.getSchemaData()));
                    context.createSchema(schemaId, schemaObject, schemaFormat).ifPresent(schemaPath -> {
                        context.register(schemaId, schemaPath);
                        Map<String, Object> schemasMap1 = new LinkedHashMap<>();
                        schemasMap1.put("schema", schemaPath);
                        schemasMap.put(schemaId, schemasMap1);
                    });
                }
            }
        });

        map.put("schemas", schemasMap);
        return map;
    }
}
