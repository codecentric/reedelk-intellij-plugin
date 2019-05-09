package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnCorrectScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode1, componentNode1);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode1);

        // When
        Optional<ScopedGraphNode> actualScope = FindScope.of(graph, componentNode1);

        // Then
        assertThat(actualScope.isPresent()).isTrue();
        assertThat(actualScope.get()).isEqualTo(choiceNode2);
    }
}
