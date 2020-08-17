package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.HeaderObject;
import com.reedelk.openapi.v3.model.ParameterStyle;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HeaderObjectSerializerTest extends AbstractSerializerTest {

    private HeaderObjectSerializer serializer;

    @BeforeEach
    void setUp() {
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
}
