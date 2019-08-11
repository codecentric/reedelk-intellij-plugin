package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindScopeTest extends AbstractGraphTest {

    private FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode1);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode1);
    }

    @Test
    void shouldReturnCorrectScope() {
        // When
        Optional<ScopedGraphNode> actualScope = FindScope.of(graph, componentNode1);

        // Then
        assertThat(actualScope).isPresent();
        assertThat(actualScope).get().isEqualTo(routerNode2);
    }

    @Test
    void shouldReturnCorrectScopeOfScopedNode() {
        // When
        Optional<ScopedGraphNode> actualScope = FindScope.of(graph, routerNode2);

        // Then
        assertThat(actualScope).isPresent();
        assertThat(actualScope).get().isEqualTo(routerNode1);
    }
}
