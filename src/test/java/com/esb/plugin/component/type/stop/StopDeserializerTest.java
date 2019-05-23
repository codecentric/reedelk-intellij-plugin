package com.esb.plugin.component.type.stop;

import com.esb.plugin.AbstractDeserializerTest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.LENIENT)
public class StopDeserializerTest extends AbstractDeserializerTest {

    private StopDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new StopDeserializer(graph, context);
    }

    @Test
    void shouldThrowException() {
        // Given
        JSONObject stopComponentDefinition = new JSONObject();

        // Expect
        assertThrows(UnsupportedOperationException.class,
                () -> deserializer.deserialize(root, stopComponentDefinition));
    }
}
