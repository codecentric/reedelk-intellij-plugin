package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.PredefinedSchema;
import com.reedelk.openapi.v3.model.ParameterLocation;
import com.reedelk.openapi.v3.model.ParameterObject;
import com.reedelk.openapi.v3.model.Schema;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterObjectSerializerTest extends AbstractSerializerTest {

    private ParameterObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        super.setUp();
        serializer = new ParameterObjectSerializer(context);
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

        ParameterObject parameterObject = new ParameterObject();
        parameterObject.setSchema(schema);
        parameterObject.setRequired(true);
        parameterObject.setName("myParam");
        parameterObject.setIn(ParameterLocation.query);

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, parameterObject);

        // Then
        Map<String, Object> expectedParameterMap = new LinkedHashMap<>();
        expectedParameterMap.put("predefinedSchema", PredefinedSchema.ARRAY_INTEGER.name());
        expectedParameterMap.put("required", true);
        expectedParameterMap.put("name", "myParam");
        expectedParameterMap.put("in", "query");
        assertThat(serialized).isEqualTo(expectedParameterMap);
    }
}
