package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindFirstNodeOutsideScopeTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReturnEmptyWhenTwoLevelsAndOneContainsANestedScopeWithoutSuccessorsOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(routerNode1, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(routerNode2);

        routerNode2.addToScope(componentNode2);

        // When
        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, routerNode1);

        // Then
        assertThat(firstNodeOutsideScope.isPresent()).isFalse();
    }

    @Test
    void shouldCorrectlyReturnFirstDrawableOutsideScopeWhenRouterWithTwoChildren() {
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
        Optional<GraphNode> nodes = FindFirstNodeOutsideScope.of(graph, routerNode1);

        // Then
        assertThat(nodes.get()).isEqualTo(componentNode3);
    }

    @Test
    void shouldCorrectlyReturnFirstNodeOutsideScopeWhenScopeIsEmpty() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);

        // When
        Optional<GraphNode> nodes = FindFirstNodeOutsideScope.of(graph, forkNode1);

        // Then
        assertThat(nodes.get()).isEqualTo(componentNode1);
    }
}