package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BelongToSameScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnTrueWhenBothOutsideAnyScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        boolean actual = BelongToSameScope.from(graph, componentNode1, componentNode2);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnTrueWhenBothBelongToSameScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode2);

        // When
        boolean actual = BelongToSameScope.from(graph, componentNode1, componentNode2);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBelongToDifferentScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode2);

        // When
        boolean actual = BelongToSameScope.from(graph, componentNode1, componentNode2);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnFalseWhenOneBelongToScopeAndTheOtherOutsideAnyScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, componentNode2);

        choiceNode1.addToScope(componentNode1);

        // When
        boolean actual = BelongToSameScope.from(graph, componentNode1, componentNode2);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnFalseWhenOneIsScopedNodeAndTheOtherOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);

        // When
        boolean actual = BelongToSameScope.from(graph, choiceNode1, componentNode1);

        // Then
        assertThat(actual).isFalse();
    }
}