package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.component.type.router.RouterNode;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Compute Max Height Tests")
class ComputeMaxHeightTest extends AbstractGraphTest {

    @Mock
    Graphics2D graphics;

    @Test
    void shouldComputeMaxHeightCorrectlyForRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(AbstractGraphNode.HEIGHT);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRootFollowedByOneNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(AbstractGraphNode.HEIGHT);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRouterSubtree() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, routerNode1);

        // Then
        // Two nodes on top of each other plus need to take into account
        // the vertical top and bottom padding of the scope.
        assertThat(actual).isEqualTo(
                AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRouterWithoutSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(routerNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, routerNode1);

        // Then
        // scoped graph node has top and bottom padding
        assertThat(actual).isEqualTo(
                RouterNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRouterWithSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(routerNode1);
        graph.add(routerNode1, componentNode1);
        routerNode1.addToScope(componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, routerNode1);

        // Then
        // scoped graph node has top and bottom padding, the following
        // componentNode1 has height lower than the router.
        assertThat(actual).isEqualTo(
                RouterNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForForkWithoutSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(forkNode1);
        graph.add(forkNode1, componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, forkNode1);

        // Then
        assertThat(actual).isEqualTo(
                ForkNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForForkWithSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(forkNode1);
        graph.add(forkNode1, componentNode1);
        forkNode1.addToScope(componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, forkNode1);

        // Then
        assertThat(actual).isEqualTo(
                ForkNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForNestedRouter() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, routerNode1);

        // Then
        // Nexted Router height is the height of the node + the 2 vertical
        // padding for the innermost router and the outermost router.
        assertThat(actual).isEqualTo(RouterNode.HEIGHT +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForDisjointSubsequentRouters() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, routerNode2);
        graph.add(routerNode2, componentNode4);
        graph.add(routerNode2, componentNode5);
        graph.add(routerNode2, componentNode6);
        graph.add(componentNode4, componentNode7);
        graph.add(componentNode5, componentNode7);
        graph.add(componentNode6, componentNode7);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);

        routerNode2.addToScope(componentNode4);
        routerNode2.addToScope(componentNode5);
        routerNode2.addToScope(componentNode6);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, routerNode1);

        // Then
        assertThat(actual).isEqualTo(
                AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightForNestedRouterCorrectlyWhenEndNodeOutsideScopeIsProvided() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.add(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode1);

        routerNode1.addToScope(routerNode2);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, routerNode1, componentNode1);

        // Then
        assertThat(actual).isEqualTo(RouterNode.HEIGHT +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightForMultipleNestedRouters() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode1, routerNode3);

        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(routerNode3);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then: 2 routers on top of each other + padding/s for 2 routers
        assertThat(actual).isEqualTo(
                RouterNode.HEIGHT +
                        RouterNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightForDisjointRoutersOnSameLevel() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode2, routerNode2);
        graph.add(routerNode2, componentNode4);
        graph.add(routerNode2, componentNode5);
        graph.add(routerNode2, componentNode6);
        graph.add(componentNode1, componentNode7);
        graph.add(componentNode4, componentNode7);
        graph.add(componentNode5, componentNode7);
        graph.add(componentNode6, componentNode7);
        graph.add(componentNode3, componentNode7);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode4);
        routerNode2.addToScope(componentNode5);
        routerNode2.addToScope(componentNode6);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(
                AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightCorrectlyForFollowingRoutersWithOneSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode3);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode3);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(
                RouterNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightCorrectlyForRouterNestedWithFork() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, forkNode1);
        graph.add(forkNode1, componentNode1);

        routerNode1.addToScope(forkNode1);
        forkNode1.addToScope(componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(
                ForkNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }
}
