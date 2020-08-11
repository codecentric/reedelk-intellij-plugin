package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.commons.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

class MediaTypeObjectSerializer implements Serializer<MediaTypeObject> {

    private final OpenApiImporterContext context;

    public MediaTypeObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, MediaTypeObject mediaTypeObject) {
        // TODO: Example: should create a file in the assets and add the reference in the serialized object.
        // TODO: Add custom example object serializer.
        //Example example = mediaTypeObject.getExample();

        // Replace all schemas with reference object to the Resource Text.
        // Example?
        Schema schema = mediaTypeObject.getSchema();

        if (schema != null) {
            // It is a schema reference
            if (StringUtils.isNotBlank(schema.getSchemaId())) {
                Optional<String> schemaAsset = context.assetFrom(schema.getSchemaId());
                if (schemaAsset.isPresent()) {
                    return ImmutableMap.of("schema", schemaAsset.get());
                }


            } else if (schema.getSchemaData() != null){
                // We must create a schema asset.
                String finalFileName = OpenApiUtils.schemaFileNameFrom(navigationPath, context);

                Properties properties = new Properties();
                properties.put("schema", new Yaml().dump(schema.getSchemaData())); // TODO: Might be JSON instead of YAML
                String schemaAssetPath = context.createSchema(finalFileName, properties);
                return ImmutableMap.of("schema", schemaAssetPath);
            }
        }
        return new LinkedHashMap<>();
    }
}
