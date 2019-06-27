package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.component.type.router.RouterNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@DisplayName("Compute Max Height Tests")
@MockitoSettings(strictness = Strictness.LENIENT)
class ComputeMaxHeightTest extends AbstractGraphTest {

    private static final int DEFAULT_TOP_HEIGHT = 70;
    private static final int DEFAULT_BOTTOM_HEIGHT = 50;
    private static final int DEFAULT_HEIGHT = DEFAULT_TOP_HEIGHT + DEFAULT_BOTTOM_HEIGHT;

    @Mock
    Graphics2D graphics;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);
        mockDefaultNodeHeight(componentNode3);
        mockDefaultNodeHeight(componentNode4);
        mockDefaultNodeHeight(componentNode5);
        mockDefaultNodeHeight(componentNode6);
        mockDefaultNodeHeight(componentNode7);
        mockDefaultNodeHeight(componentNode8);
        mockDefaultNodeHeight(componentNode9);
        mockDefaultNodeHeight(componentNode10);
        mockDefaultNodeHeight(componentNode11);
    }

    protected void mockDefaultNodeHeight(GraphNode node) {
        mockNodeHeight(node, DEFAULT_TOP_HEIGHT, DEFAULT_BOTTOM_HEIGHT);
    }

    protected void mockNodeHeight(GraphNode node, int topHeight, int bottomHeight) {
        doReturn(topHeight + bottomHeight).when(node).height(graphics);
        doReturn(topHeight).when(node).topHalfHeight(graphics);
        doReturn(bottomHeight).when(node).bottomHalfHeight(graphics);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(DEFAULT_HEIGHT);
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
        assertThat(actual).isEqualTo(DEFAULT_HEIGHT);
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
                DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
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
                DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
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
                DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
                        DEFAULT_HEIGHT +
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

    @Test
    void shouldComputeMaxHeightCorrectlyWhenTallerComponentFollowedBySmallerScopedGraphNode() {
        // Given
        mockNodeHeight(componentNode1, 70, 180);

        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(componentNode1, forkNode2);
        graph.add(forkNode2, componentNode2);
        graph.add(forkNode2, componentNode3);

        forkNode1.addToScope(componentNode1);
        forkNode1.addToScope(forkNode2);

        forkNode2.addToScope(componentNode2);
        forkNode2.addToScope(componentNode3);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then:
        // the correct height is the greatest top height (100 + 5) -> componentNode2Spy height + 5 padding fork node2
        // plus the greatest bottom height (180) -> componentNode1Spy + first scoped node paddings top and bottom 5 + 5
        assertThat(actual).isEqualTo(DEFAULT_HEIGHT + 5 + 180 + 5 + 5);
    }

    // This test is the opposite of the above
    @Test
    void shouldComputeMaxHeightCorrectlyWhenSmallerScopedGraphNodeFollowedByTallerComponent() {
        // Given
        mockNodeHeight(componentNode1, 70, 180);

        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode2);
        graph.add(forkNode2, componentNode3);
        graph.add(componentNode2, componentNode1);
        graph.add(componentNode3, componentNode1);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode1);

        forkNode2.addToScope(componentNode2);
        forkNode2.addToScope(componentNode3);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then:
        // the correct height is the greatest top height (100 + 5) -> componentNode2Spy height + 5 padding fork node2
        // plus the greatest bottom height (180) -> componentNode1Spy + first scoped node paddings top and bottom 5 + 5
        assertThat(actual).isEqualTo(DEFAULT_HEIGHT + 5 + 180 + 5 + 5);
    }
}
