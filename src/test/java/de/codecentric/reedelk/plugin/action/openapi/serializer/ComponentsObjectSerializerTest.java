package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.openapi.commons.DataFormat;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.model.*;
import de.codecentric.reedelk.openapi.v3.serializer.Serializers;
import de.codecentric.reedelk.plugin.template.AssetProperties;
import de.codecentric.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
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
                .createAsset(eq("Pet.schema.yaml"), any(AssetProperties.class));

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

    /*
     * "examples": {
     *   "bookingGetExample": {
     *       "value": "{ \"book_ref\": \"00006A\"}"
     *   },
     *   "bookingPutExample": {
     *       "value": "{ \"book_ref\": \"00006B\"}"
     *   }
     * }
     */
    @Test
    void shouldCorrectlySerializeComponentsExamples() {
        // Given
        String example1SchemaAsset = "asset/BookingGetExample.example.json";
        String exampleId = "bookingGetExample";

        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setSummary("My example");
        exampleObject.setDescription("My example description");
        exampleObject.setExternalValue("http://my.external.value");
        exampleObject.setValue("{  \"book_ref\": \"00006A\"}");

        ComponentsObject componentsObject = new ComponentsObject();
        componentsObject.setExamples(ImmutableMap.of(exampleId, exampleObject));

        // Make sure that it creates the resource asset for the schema.
        doReturn(example1SchemaAsset)
                .when(context)
                .createAsset(eq("BookingGetExample.example.json"), any(AssetProperties.class));

        // When
        Map<String, Object> serialized = serializer.serialize(serializerContext, navigationPath, componentsObject);

        // Then
        Map<String, Object> exampleContent = new HashMap<>();
        exampleContent.put("description", "My example description");
        exampleContent.put("externalValue", "http://my.external.value");
        exampleContent.put("summary", "My example");
        exampleContent.put("value", "asset/BookingGetExample.example.json");

        Map<String, Object> bookingGetExample = ImmutableMap.of(exampleId, exampleContent);
        Map<String, Object> componentsMap = ImmutableMap.of("examples", bookingGetExample);
        assertThat(serialized).isEqualTo(componentsMap);
    }

    @Test
    void shouldCorrectlySerializeComponentsSecuritySchemes() {
        // Given
        String securitySchemeId = "petstore_auth";

        SecuritySchemeObject securitySchemeObject = new SecuritySchemeObject();
        securitySchemeObject.setType(SecurityType.http);
        securitySchemeObject.setDescription("My description");
        securitySchemeObject.setName("api_key");
        securitySchemeObject.setIn(SecurityKeyLocation.header);
        securitySchemeObject.setScheme("basic");
        securitySchemeObject.setBearerFormat("JWT");
        securitySchemeObject.setOpenIdConnectUrl("http://openid.connect.url/url");

        Map<String, SecuritySchemeObject> securitySchemeObjectMap = new HashMap<>();
        securitySchemeObjectMap.put(securitySchemeId, securitySchemeObject);

        ComponentsObject componentsObject = new ComponentsObject();
        componentsObject.setSecuritySchemes(securitySchemeObjectMap);

        // When
        Map<String, Object> serialized = serializer.serialize(serializerContext, navigationPath, componentsObject);

        // Then
        Map<String, Object> securityScheme = new HashMap<>();
        securityScheme.put("type", "http");
        securityScheme.put("description", "My description");
        securityScheme.put("name", "api_key");
        securityScheme.put("in", "header");
        securityScheme.put("scheme", "basic");
        securityScheme.put("bearerFormat", "JWT");
        securityScheme.put("openIdConnectUrl", "http://openid.connect.url/url");

        Map<String, Object> securitySchemes = ImmutableMap.of(securitySchemeId, securityScheme);
        Map<String, Object> componentsMap = ImmutableMap.of("securitySchemes", securitySchemes);
        assertThat(serialized).isEqualTo(componentsMap);
    }

    private SchemaObject createSchemaObject(String schemaId, Map<String,Object> schemaDataMap) {
        SchemaObject petSchema = new SchemaObject();
        petSchema.setSchema(new Schema(schemaDataMap));
        petSchema.setSchemaId(schemaId);
        return petSchema;
    }
}
