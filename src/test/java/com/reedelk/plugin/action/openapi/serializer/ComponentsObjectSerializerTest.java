package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.DataFormat;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.model.ComponentsObject;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.openapi.v3.model.SchemaObject;
import com.reedelk.openapi.v3.serializer.Serializers;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ComponentsObjectSerializerTest extends AbstractSerializerTest {

    private ComponentsObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        super.setUp();
        serializer = new ComponentsObjectSerializer(context);
    }

    /*
     *       "schemas": {
     *         "Category": {
     *           "schema": "assets/my-api/Category.yaml"
     *         },
     *         "Pet": {
     *           "schema": "assets/my-api/Pet.yaml"
     *         }
     *       }
     */
    @Test
    void shouldCorrectlySerializeComponentsSchema() {
        // Given
        String petSchemaAsset = "asset/Pet.yaml";
        String petSchemaId = "Pet";

        SchemaObject petSchema = createSchemaObject(petSchemaId, ImmutableMap.of("type", "string"));
        ComponentsObject componentsObject = new ComponentsObject();
        componentsObject.setSchemas(ImmutableMap.of(petSchemaId, petSchema));

        // Make sure that it creates the resource asset for the schema.
        doReturn(petSchemaAsset)
                .when(context)
                .createAsset(eq("Pet.yaml"), any(AssetProperties.class));

        doReturn(DataFormat.YAML).when(context).getSchemaFormat();

        // When
        Map<String, Object> serialized = serializer.serialize(serializerContext, navigationPath, componentsObject);

        // Then
        Map<String, Object> petSchemaMap = ImmutableMap.of("schema", petSchemaAsset);
        Map<String, Object> schemasMap = ImmutableMap.of(petSchemaId, petSchemaMap);
        Map<String, Object> componentsMap = ImmutableMap.of("schemas", schemasMap);
        assertThat(serialized).isEqualTo(componentsMap);
    }

    /*
     *  // Expected
     *   {}
     */
    @Test
    void shouldCorrectlyRegisterRequestBody() {
        // Given
        String requestBodyId = "Pet";
        RequestBodyObject requestBodyObject = new RequestBodyObject();

        SerializerContext serializerContext = new SerializerContext(new Serializers());

        ComponentsObject componentsObject = new ComponentsObject();
        componentsObject.setRequestBodies(ImmutableMap.of(requestBodyId, requestBodyObject));

        // When
        Map<String, Object> serialize = serializer.serialize(serializerContext, navigationPath, componentsObject);

        // Then
        verify(context).registerRequestBody(requestBodyId, requestBodyObject);
        assertThat(serialize).isEmpty();
    }

    private SchemaObject createSchemaObject(String schemaId, Map<String,Object> schemaDataMap) {
        SchemaObject petSchema = new SchemaObject();
        petSchema.setSchema(new Schema(schemaDataMap));
        petSchema.setSchemaId(schemaId);
        return petSchema;
    }
}