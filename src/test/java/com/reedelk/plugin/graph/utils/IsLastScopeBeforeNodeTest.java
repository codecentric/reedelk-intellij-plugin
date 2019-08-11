package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsLastScopeBeforeNodeTest extends AbstractGraphTest {

    @Test
    void shouldIsLastScopeBeforeNodeReturnTrueWhenLastScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, routerNode1, componentNode3);

        // Then
        assertThat(isLastScope).isTrue();
    }

    @Test
    void shouldIsLastScopeBeforeNodeReturnTrueWhenNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);

        routerNode2.addToScope(componentNode2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, routerNode1, componentNode3);

        // Then
        assertThat(isLastScope).isTrue();
    }

    @Test
    void shouldIsLastScopeBeforeNodeReturnFalseWhenInnermostNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);

        routerNode2.addToScope(componentNode2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, routerNode2, componentNode3);

        // Then
        assertThat(isLastScope).isFalse();
    }

}