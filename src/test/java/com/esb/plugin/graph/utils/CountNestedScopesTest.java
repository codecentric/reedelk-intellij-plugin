package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountNestedScopesTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroWhenNodeDoesNotBelongToAnyScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenNodeBelongsToOneScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);

        choiceNode1.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenNodeBelongsToTwoScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnOneWhenNodeIsScopedDrawable() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, choiceNode1);

        // Then
        assertThat(scopesCount).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenNestedScopedDrawables() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        choiceNode1.addToScope(choiceNode2);

        // When
        int scopesCount = CountNestedScopes.of(graph, choiceNode2);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnThreeWhenNestedScopedDrawables() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, choiceNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(choiceNode3);

        // When
        int scopesCount = CountNestedScopes.of(graph, choiceNode3);

        // Then
        assertThat(scopesCount).isEqualTo(3);
    }

    @Test
    void shouldReturnTwoWhenThreeNestedScopesAndNodeIsOutFromInnermost() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, choiceNode3);
        graph.add(choiceNode3, componentNode1);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(choiceNode3);
        choiceNode2.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }
}