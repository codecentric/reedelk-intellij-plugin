package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BelongToSameScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnTrueWhenBothOutsideAnyScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, n1);
        graph.add(n1, n2);

        // When
        boolean actual = BelongToSameScope.from(graph, n1, n2);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnTrueWhenBothBelongToSameScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(n2);

        // When
        boolean actual = BelongToSameScope.from(graph, n1, n2);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBelongToDifferentScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice2.addToScope(n2);

        // When
        boolean actual = BelongToSameScope.from(graph, n1, n2);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnFalseWhenOneBelongToScopeAndTheOtherOutsideAnyScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, n2);

        choice1.addToScope(n1);

        // When
        boolean actual = BelongToSameScope.from(graph, n1, n2);

        // Then
        assertThat(actual).isFalse();
    }

}