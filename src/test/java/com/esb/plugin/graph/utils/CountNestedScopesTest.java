package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountNestedScopesTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroWhenNodeDoesNotBelongToAnyScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenNodeBelongsToOneScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        routerNode1.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenNodeBelongsToTwoScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode1);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnOneWhenNodeIsScopedDrawable() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, routerNode1);

        // Then
        assertThat(scopesCount).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenNestedScopedDrawables() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        routerNode1.addToScope(routerNode2);

        // When
        int scopesCount = CountNestedScopes.of(graph, routerNode2);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnThreeWhenNestedScopedDrawables() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, routerNode3);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(routerNode3);

        // When
        int scopesCount = CountNestedScopes.of(graph, routerNode3);

        // Then
        assertThat(scopesCount).isEqualTo(3);
    }

    @Test
    void shouldReturnTwoWhenThreeNestedScopesAndNodeIsOutFromInnermost() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, routerNode3);
        graph.add(routerNode3, componentNode1);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(routerNode3);
        routerNode2.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, componentNode1);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnTwoWhenNestedScopedNodesOnDifferentLevels() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode1, forkNode3);
        graph.add(forkNode2, componentNode1);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(forkNode3);
        forkNode2.addToScope(componentNode1);

        // When
        int scopesCount = CountNestedScopes.of(graph, forkNode3);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }
}