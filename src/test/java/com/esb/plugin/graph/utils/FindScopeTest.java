package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;
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
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode1);
    }

    @Test
    void shouldReturnCorrectScope() {
        // When
        Optional<ScopedGraphNode> actualScope = FindScope.of(graph, componentNode1);

        // Then
        assertThat(actualScope).isPresent();
        assertThat(actualScope).get().isEqualTo(choiceNode2);
    }

    @Test
    void shouldReturnCorrectScopeOfScopedNode() {
        // When
        Optional<ScopedGraphNode> actualScope = FindScope.of(graph, choiceNode2);

        // Then
        assertThat(actualScope).isPresent();
        assertThat(actualScope).get().isEqualTo(choiceNode1);
    }
}
