package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.v3.model.Example;
import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

class MediaTypeObjectSerializerTest extends AbstractSerializerTest {

    private MediaTypeObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        super.setUp();
        serializer = new MediaTypeObjectSerializer(context);
    }

    @Test
    void shouldCorrectlySerializeExample() {
        // Given
        Example example = new Example("{ \"name\": \"John\" }");
        MediaTypeObject mediaTypeObject = new MediaTypeObject();
        mediaTypeObject.setExample(example);

        doReturn("assets/my-example.json").when(context).createAsset(anyString(), any(AssetProperties.class));

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, mediaTypeObject);

        // Then
        Map<String, Object> expectedMap = ImmutableMap.of("example", "assets/my-example.json");
        assertThat(serialized).isEqualTo(expectedMap);
    }

    @Test
    void shouldCorrectlySerializeSchema() {
        // Given
        String schemaId = "mySchemaId";
        Schema schema = new Schema(schemaId);
        MediaTypeObject mediaTypeObject = new MediaTypeObject();
        mediaTypeObject.setSchema(schema);

        doReturn(Optional.of("assets/my-schema.schema.yaml"))
                .when(context)
                .getAssetFrom(schemaId);

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, mediaTypeObject);

        // Then
        Map<String, Object> expectedMap = ImmutableMap.of("schema", "assets/my-schema.schema.yaml");
        assertThat(serialized).isEqualTo(expectedMap);
    }

}
