package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.Example;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.commons.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class MediaTypeObjectSerializer implements Serializer<MediaTypeObject> {

    private final OpenApiImporterContext context;

    public MediaTypeObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, MediaTypeObject mediaTypeObject) {
        Example example = mediaTypeObject.getExample();

        // Replace all schemas with reference object to the Resource Text.
        // Example?
        Schema schema = mediaTypeObject.getSchema();

        if (schema != null) {
            if (StringUtils.isNotBlank(schema.getSchemaId())) {
                Optional<String> schemaAsset = context.assetFrom(schema.getSchemaId());
                if (schemaAsset.isPresent()) {
                    return ImmutableMap.of("schema", schemaAsset.get());
                }


            } else if (schema.getSchemaData() != null){
                // Must create the schema dn set a name.
                Properties properties = new Properties();
                properties.put("schema", new Yaml().dump(schema.getSchemaData()));
                String schemaPath = context.createSchema("template", properties);
                return ImmutableMap.of("schema", schemaPath);
            }
        }
        return new HashMap<>();
    }
}