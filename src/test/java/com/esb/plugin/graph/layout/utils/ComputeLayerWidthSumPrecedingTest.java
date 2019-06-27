package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.graph.node.ScopedGraphNode.HORIZONTAL_PADDING;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ComputeLayerWidthSumPrecedingTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics2D;

    @Test
    void shouldReturnCorrectWidthSumWhenTwoParallelForksWithFollowingNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode1, forkNode3);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode3, componentNode2);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(forkNode3);
        forkNode2.addToScope(componentNode1);
        forkNode3.addToScope(componentNode2);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(forkNode1);
        List<GraphNode> layer2 = asList(forkNode2, forkNode3);
        List<GraphNode> layer3 = asList(componentNode1, componentNode2);
        List<List<GraphNode>> layers = asList(layer0, layer1, layer2, layer3);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 3);

        // Then
        assertThat(widthSum).isEqualTo(
                AbstractGraphNode.WIDTH +
                        ForkNode.WIDTH + HORIZONTAL_PADDING + // layer 1 (1 nested scope)
                        ForkNode.WIDTH + HORIZONTAL_PADDING + HORIZONTAL_PADDING); // layer 2 (2 nested scopes)
    }

    @Test
    void shouldReturnCorrectWidthSumWhenTwoParallelForksWithJustOneHavingFollowingNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode1, forkNode3);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode3);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(forkNode3);
        forkNode2.addToScope(componentNode1);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(forkNode1);
        List<GraphNode> layer2 = asList(forkNode2, forkNode3);
        List<GraphNode> layer3 = singletonList(componentNode1);
        List<List<GraphNode>> layers = asList(layer0, layer1, layer2, layer3);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 3);

        // Then
        assertThat(widthSum).isEqualTo(345);
    }
}