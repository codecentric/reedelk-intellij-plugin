package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.ExampleObject;
import com.reedelk.plugin.template.AssetProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExampleObjectSerializerTest extends AbstractSerializerTest {

    private ExampleObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        super.setUp();
        serializer = new ExampleObjectSerializer(context);
    }

    @Test
    void shouldCorrectlySerializeExampleObject() {
        // Given
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setSummary("My summary");
        exampleObject.setExternalValue("http://my.external.value");
        exampleObject.setDescription("My description");
        exampleObject.setValue("Example test");

        NavigationPath navigationPath = NavigationPath.create();

        doReturn("assets/MyExample.json")
                .when(context)
                .createAsset(anyString(), any(AssetProperties.class));

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, exampleObject);

        // Then
        Map<String, Object> expectedHeaderMap = new HashMap<>();
        expectedHeaderMap.put("description", "My description");
        expectedHeaderMap.put("externalValue", "http://my.external.value");
        expectedHeaderMap.put("inlineExample", true);
        expectedHeaderMap.put("summary", "My summary");
        expectedHeaderMap.put("value", "assets/MyExample.json");
        assertThat(serialized).isEqualTo(expectedHeaderMap);
    }

    @Test
    void shouldCorrectlySerializeExampleObjectWithReference() {
        // Given
        String $ref = "#/components/examples/bookingPutExample";
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setExampleRef($ref);

        NavigationPath navigationPath = NavigationPath.create();

        doReturn(Optional.of("assets/MyExample.json"))
                .when(context)
                .getAssetFrom($ref);

        // When
        Map<String, Object> serialized =
                serializer.serialize(serializerContext, navigationPath, exampleObject);

        // Then
        Map<String, Object> expectedHeaderMap = new HashMap<>();
        expectedHeaderMap.put("value", "assets/MyExample.json");
        assertThat(serialized).isEqualTo(expectedHeaderMap);
    }
}
