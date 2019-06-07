package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CollectNodesBetweenTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyCollectAllElementsBetweenNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, componentNode2);
        graph.add(choiceNode1, componentNode3);
        graph.add(componentNode1, componentNode4);
        graph.add(componentNode4, componentNode5);
        graph.add(componentNode2, componentNode5);
        graph.add(componentNode3, componentNode5);
        graph.add(componentNode5, componentNode6);

        // When
        Collection<GraphNode> nodes = CollectNodesBetween.them(graph, choiceNode1, componentNode5);

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(componentNode1, componentNode2, componentNode3, componentNode4);
    }

    @Test
    void shouldCollectAllElementsBetweenNodesExcludingTheOnesInNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);

        choiceNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> nodes = CollectNodesBetween.them(graph, choiceNode1, componentNode4);

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(componentNode1, choiceNode2, componentNode3);
    }

}
