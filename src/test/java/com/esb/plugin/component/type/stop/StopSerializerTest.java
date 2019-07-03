package com.esb.plugin.component.type.stop;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StopSerializerTest extends AbstractGraphTest {

    private StopSerializer serializer;

    private FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
        serializer = new StopSerializer();
    }

    @Test
    void shouldThrowException() {
        // Given
        JSONArray sequence = new JSONArray();

        // Expect
        assertThrows(UnsupportedOperationException.class,
                () -> serializer.serialize(graph, sequence, componentNode1, componentNode2));
    }
}
