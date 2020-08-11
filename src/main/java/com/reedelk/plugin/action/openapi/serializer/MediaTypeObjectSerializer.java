package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.Serializer;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.Example;
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

class MediaTypeObjectSerializer implements Serializer<MediaTypeObject> {

    private final OpenApiImporterContext context;

    public MediaTypeObjectSerializer(OpenApiImporterContext context) {
        this.context = context;
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, MediaTypeObject mediaTypeObject) {
        Example example = mediaTypeObject.getExample();
        if (example != null) {
            // We must create an example asset.
            String finalFileName = OpenApiUtils.exampleFileNameFrom(navigationPath, context);

            String exampleAssetPath = context.createAsset(finalFileName, example.data());
            return ImmutableMap.of("example", exampleAssetPath);
        }


        // Replace all schemas with reference object to the Resource Text.
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

                String data = new Yaml().dump(schema.getSchemaData()); // TODO: Might be JSON instead of YAML
                String schemaAssetPath = context.createAsset(finalFileName, data);
                return ImmutableMap.of("schema", schemaAssetPath);
            }
        }
        return new LinkedHashMap<>();
    }
}
