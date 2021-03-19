package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CollectNodesBetweenTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyCollectAllElementsBetweenNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode1, componentNode4);
        graph.add(componentNode4, componentNode5);
        graph.add(componentNode2, componentNode5);
        graph.add(componentNode3, componentNode5);
        graph.add(componentNode5, componentNode6);

        // When
        Collection<GraphNode> nodes = CollectNodesBetween.them(graph, routerNode1, componentNode5);

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(componentNode1, componentNode2, componentNode3, componentNode4);
    }

    @Test
    void shouldCollectAllElementsBetweenNodesExcludingTheOnesInNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);

        routerNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> nodes = CollectNodesBetween.them(graph, routerNode1, componentNode4);

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(componentNode1, routerNode2, componentNode3);
    }

}
