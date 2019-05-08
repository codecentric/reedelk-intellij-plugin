package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CollectNodesBetweenTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyCollectAllElementsBetweenNodes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, n2);
        graph.add(choice1, n3);
        graph.add(n1, n4);
        graph.add(n4, n5);
        graph.add(n2, n5);
        graph.add(n3, n5);
        graph.add(n5, n6);

        // When
        Collection<GraphNode> nodes = CollectNodesBetween.them(graph, choice1, n5);

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(n1, n2, n3, n4);
    }

}
