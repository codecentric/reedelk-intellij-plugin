package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.Example;
import com.reedelk.openapi.v3.model.ExampleObject;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.action.openapi.OpenApiExampleFormat;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.plugin.template.AssetProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.reedelk.openapi.commons.NavigationPath.SegmentKey.EXAMPLES;
import static com.reedelk.openapi.commons.NavigationPath.SegmentKey.EXAMPLE_ID;
import static com.reedelk.openapi.v3.model.MediaTypeObject.Properties.EXAMPLE;
import static com.reedelk.openapi.v3.model.MediaTypeObject.Properties.SCHEMA;

class MediaTypeObjectSerializer extends AbstractSerializer<MediaTypeObject> {

    public MediaTypeObjectSerializer(OpenApiImporterContext context) {
        super(context);
    }

    @Override
    public Map<String, Object> serialize(SerializerContext serializerContext, NavigationPath navigationPath, MediaTypeObject mediaTypeObject) {
        Map<String,Object> serialized = new LinkedHashMap<>();

        Example example = mediaTypeObject.getExample();
        if (example != null) {
            // We must create an example asset.
            String data = example.data();
            OpenApiExampleFormat exampleFormat = context.exampleFormatOf(data);

            AssetProperties properties = new AssetProperties(data);
            String finalFileName = OpenApiUtils.exampleFileNameFrom(navigationPath, exampleFormat);

            String exampleAssetPath = context.createAsset(finalFileName, properties);
            serialized.put(EXAMPLE.value(), exampleAssetPath);
        }

        // Replace all schemas with reference object to the Resource Text.
        Schema schema = mediaTypeObject.getSchema();
        if (schema != null) {
            String finalFileName = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);
            setSchema(SCHEMA.value(), serialized, schema, finalFileName);
        }

        // Examples
        Map<String, ExampleObject> examples = mediaTypeObject.getExamples();
        if (OpenApiUtils.isNotEmpty(examples)) {
            NavigationPath currentNavigationPath = navigationPath.with(EXAMPLES);
            Map<String,Object> serializedExamples = new LinkedHashMap<>();
            examples.forEach((exampleId, exampleObject) -> {
                Map<String, Object> serializedExample = serializerContext.serialize(currentNavigationPath.with(EXAMPLE_ID, exampleId), exampleObject);
                serializedExamples.put(exampleId, serializedExample);
            });
            serialized.put(MediaTypeObject.Properties.EXAMPLES.value(), serializedExamples);
        }

        return serialized;
    }
}
