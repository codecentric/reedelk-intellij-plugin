package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.v3.model.MediaTypeObject;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

class RequestBodyObjectSerializerTest extends AbstractSerializerTest {

    private RequestBodyObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        super.setUp();
        serializer = new RequestBodyObjectSerializer(context);
    }

    @Test
    void shouldCorrectlySerializeRequestBody() {
        // Given
        String schemaId = "mySchemaId";
        MediaTypeObject mediaTypeObject = new MediaTypeObject();
        mediaTypeObject.setSchema(new Schema(schemaId));

        doReturn(Optional.of("assets/my-schema.schema.yaml"))
                .when(context)
                .getAssetFrom(schemaId);

        RequestBodyObject requestBodyObject = new RequestBodyObject();
        requestBodyObject.setDescription("My description");
        requestBodyObject.setRequired(true);
        requestBodyObject.setContent(ImmutableMap.of("application/json", mediaTypeObject));

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, requestBodyObject);

        // Then
        Map<String, Object> contentMap = ImmutableMap.of("application/json",
                ImmutableMap.of("schema", "assets/my-schema.schema.yaml"));
        Map<String, Object> expectedMap =
                ImmutableMap.of("description", "My description",
                        "content", contentMap,
                        "required", true);

        assertThat(serialized).isEqualTo(expectedMap);
    }

    @Test
    void shouldCorrectlySerializeRequestBodyWithReference() {
        // Given
        String schemaId = "mySchemaId";
        MediaTypeObject mediaTypeObject = new MediaTypeObject();
        mediaTypeObject.setSchema(new Schema(schemaId));

        doReturn(Optional.of("assets/my-schema.schema.yaml"))
                .when(context)
                .getAssetFrom(schemaId);

        RequestBodyObject referencedBodyObject = new RequestBodyObject();
        referencedBodyObject.setDescription("My description");
        referencedBodyObject.setRequired(true);
        referencedBodyObject.setContent(ImmutableMap.of("application/json", mediaTypeObject));

        RequestBodyObject requestBodyObject = new RequestBodyObject();
        requestBodyObject.set$ref("#/components/requestBodies/UserArray");

        doReturn(Optional.of(referencedBodyObject))
                .when(context)
                .getRequestBodyById("UserArray");

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, requestBodyObject);

        // Then
        Map<String, Object> contentMap = ImmutableMap.of("application/json",
                ImmutableMap.of("schema", "assets/my-schema.schema.yaml"));
        Map<String, Object> expectedMap =
                ImmutableMap.of("description", "My description",
                        "content", contentMap,
                        "required", true);

        assertThat(serialized).isEqualTo(expectedMap);
    }
}
