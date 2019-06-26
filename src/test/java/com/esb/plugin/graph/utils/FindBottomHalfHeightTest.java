package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

class FindBottomHalfHeightTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;
    FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
    }

    @Test
    void shouldDoSomething() {
        // Given


        // When
        FindBottomHalfHeight.of(graph, graphics, componentNode1, null, 0);

        // Then
    }

}