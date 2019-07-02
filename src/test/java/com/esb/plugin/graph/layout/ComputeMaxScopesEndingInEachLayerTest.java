package com.esb.plugin.graph.layout;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class ComputeMaxScopesEndingInEachLayerTest extends AbstractGraphTest {

    private FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
    }

    @Test
    void shouldDoSomething() {
        // Given
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, forkNode3);
        graph.add(forkNode3, componentNode1);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode1);
        forkNode2.addToScope(forkNode3);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(forkNode1);
        List<GraphNode> layer2 = singletonList(forkNode2);
        List<GraphNode> layer3 = singletonList(forkNode3);
        List<GraphNode> layer4 = singletonList(componentNode1);
        List<List<GraphNode>> layers = asList(layer0, layer1, layer2, layer3, layer4);


        // When
        ComputeMaxScopesEndingInEachLayer evaluate = new ComputeMaxScopesEndingInEachLayer(graph, layers);
        int actual = evaluate.forLayer(3);

        // Then
        Assertions.assertThat(actual).isEqualTo(2);
    }
}