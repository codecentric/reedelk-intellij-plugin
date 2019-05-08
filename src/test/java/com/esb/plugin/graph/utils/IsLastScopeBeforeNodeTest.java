package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsLastScopeBeforeNodeTest extends AbstractGraphTest {

    @Test
    void shouldIsLastScopeBeforeNodeReturnTrueWhenLastScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(n2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, choice1, n3);

        // Then
        assertThat(isLastScope).isTrue();
    }

    @Test
    void shouldIsLastScopeBeforeNodeReturnTrueWhenNestedScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);

        choice2.addToScope(n2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, choice1, n3);

        // Then
        assertThat(isLastScope).isTrue();
    }

    @Test
    void shouldIsLastScopeBeforeNodeReturnFalseWhenInnermostNestedScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);

        choice2.addToScope(n2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, choice2, n3);

        // Then
        assertThat(isLastScope).isFalse();
    }

}