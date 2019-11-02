package com.reedelk.plugin.component.type.stop;

import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StopDeserializerTest extends AbstractNodeDeserializerTest {

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
