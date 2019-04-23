package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountNestedScopesTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroWhenNodeDoesNotBelongToAnyScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, n1);

        // When
        int scopesCount = CountNestedScopes.of(graph, n1);

        // Then
        assertThat(scopesCount).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenNodeBelongsToOneScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, n1);

        choice1.addToScope(n1);

        // When
        int scopesCount = CountNestedScopes.of(graph, n1);

        // Then
        assertThat(scopesCount).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenNodeBelongsToTwoScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice2, n1);

        choice1.addToScope(choice2);
        choice2.addToScope(n1);

        // When
        int scopesCount = CountNestedScopes.of(graph, n1);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnOneWhenNodeIsScopedDrawable() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);

        // When
        int scopesCount = CountNestedScopes.of(graph, choice1);

        // Then
        assertThat(scopesCount).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenNestedScopedDrawables() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        choice1.addToScope(choice2);

        // When
        int scopesCount = CountNestedScopes.of(graph, choice2);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }

    @Test
    void shouldReturnThreeWhenNestedScopedDrawables() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice2, choice3);

        choice1.addToScope(choice2);
        choice2.addToScope(choice3);

        // When
        int scopesCount = CountNestedScopes.of(graph, choice3);

        // Then
        assertThat(scopesCount).isEqualTo(3);
    }

    @Test
    void shouldReturnTwoWhenThreeNestedScopesAndNodeIsOutFromInnermost() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice2, choice3);
        graph.add(choice3, n1);

        choice1.addToScope(choice2);
        choice2.addToScope(choice3);
        choice2.addToScope(n1);

        // When
        int scopesCount = CountNestedScopes.of(graph, n1);

        // Then
        assertThat(scopesCount).isEqualTo(2);
    }
}