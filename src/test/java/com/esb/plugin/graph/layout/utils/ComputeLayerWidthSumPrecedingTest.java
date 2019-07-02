package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.ComputeLayerWidthSumPreceding;
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
    void shouldReturnCorrectSumForRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        List<GraphNode> layer0 = singletonList(root);
        List<List<GraphNode>> layers = singletonList(layer0);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 1);

        // Then
        assertThat(widthSum).isEqualTo(130);
    }

    @Test
    void shouldReturnCorrectSumForNodeFollowingRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(componentNode1);
        List<List<GraphNode>> layers = asList(layer0, layer1);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 2);

        // Then
        assertThat(widthSum).isEqualTo(260);
    }

    @Test
    void shouldReturnCorrectSumForEmptyScopedNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(forkNode1);
        List<List<GraphNode>> layers = asList(layer0, layer1);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 2);

        // Then
        assertThat(widthSum).isEqualTo(260);
    }

    @Test
    void shouldReturnCorrectSumForScopedNodeFollowedByNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);

        forkNode1.setPosition(195, 10);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(forkNode1);
        List<GraphNode> layer2 = singletonList(componentNode1);
        List<List<GraphNode>> layers = asList(layer0, layer1, layer2);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 3);

        // Then
        assertThat(widthSum).isEqualTo(390);
    }

    @Test
    void shouldReturnCorrectSumForNestedScopeFollowedByNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);

        forkNode1.addToScope(forkNode2);

        forkNode1.setPosition(195, 10);
        forkNode2.setPosition(325, 10);

        List<GraphNode> layer0 = singletonList(root);
        List<GraphNode> layer1 = singletonList(forkNode1);
        List<GraphNode> layer2 = singletonList(forkNode2);
        List<GraphNode> layer3 = singletonList(componentNode1);
        List<List<GraphNode>> layers = asList(layer0, layer1, layer2, layer3);

        // When
        int widthSum = ComputeLayerWidthSumPreceding.of(graph, graphics2D, layers, 4);

        // Then
        assertThat(widthSum).isEqualTo(525);
    }

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
        assertThat(widthSum).isEqualTo(
                AbstractGraphNode.WIDTH +
                        ForkNode.WIDTH + HORIZONTAL_PADDING + // layer 1 (1 nested scope)
                        ForkNode.WIDTH + HORIZONTAL_PADDING + HORIZONTAL_PADDING); // layer 2 (2 nested scopes)
    }
}