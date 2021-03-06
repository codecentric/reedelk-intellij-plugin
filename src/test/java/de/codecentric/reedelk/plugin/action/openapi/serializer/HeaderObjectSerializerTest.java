package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.serializer.HeaderObjectSerializer;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.commons.PredefinedSchema;
import de.codecentric.reedelk.openapi.v3.model.HeaderObject;
import de.codecentric.reedelk.openapi.v3.model.ParameterStyle;
import de.codecentric.reedelk.openapi.v3.model.Schema;
import de.codecentric.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class HeaderObjectSerializerTest extends AbstractSerializerTest {

    private HeaderObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        super.setUp();
        serializer = new HeaderObjectSerializer(context);
    }

    @Test
    void shouldCorrectlySerializeHeaderObject() {
        // Given
        HeaderObject headerObject = new HeaderObject();
        headerObject.setExplode(true);
        headerObject.setExample("test-header-example");
        headerObject.setStyle(ParameterStyle.simple);

        NavigationPath navigationPath = NavigationPath.create();

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, headerObject);

        // Then
        Map<String, Object> expectedHeaderMap =
                ImmutableMap.of("style", "simple", "example", "test-header-example", "explode", true);
        assertThat(serialized).isEqualTo(expectedHeaderMap);
    }

    @Test
    void shouldCorrectlySerializeHeaderObjectWithPredefinedSchema() {
        // Given
        String schemaId = "mySchemaId";
        Map<String, Object> schemaData =
                ImmutableMap.of("type", "array", "items",
                        ImmutableMap.of("type", "integer"));

        Schema schema = new Schema();
        schema.setSchemaData(schemaData);
        schema.setSchemaId(schemaId);

        HeaderObject headerObject = new HeaderObject();
        headerObject.setSchema(schema);

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, headerObject);

        // Then
        Map<String, Object> expectedHeaderMap =
                ImmutableMap.of("predefinedSchema", PredefinedSchema.ARRAY_INTEGER.name());
        assertThat(serialized).isEqualTo(expectedHeaderMap);
    }

    @Test
    void shouldCorrectlySerializeHeaderObjectWithNonPredefinedSchema() {
        // Given
        String schemaId = "mySchemaId";
        doReturn(Optional.of("assets/mySchemaId.yaml")).when(context).getAssetFrom(schemaId);

        Map<String, Object> schemaData =
                ImmutableMap.of("type", "array", "items",
                        ImmutableMap.of("type", "Pet"));

        Schema schema = new Schema();
        schema.setSchemaData(schemaData);
        schema.setSchemaId(schemaId);

        HeaderObject headerObject = new HeaderObject();
        headerObject.setSchema(schema);

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, headerObject);

        // Then
        Map<String, Object> expectedHeaderMap =
                ImmutableMap.of("predefinedSchema", "NONE", "schema", "assets/mySchemaId.yaml");
        assertThat(serialized).isEqualTo(expectedHeaderMap);
    }
}
