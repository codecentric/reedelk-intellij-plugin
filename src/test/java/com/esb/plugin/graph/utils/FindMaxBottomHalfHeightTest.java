package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class FindMaxBottomHalfHeightTest extends AbstractGraphTest {

    private GraphNode UNTIL_LAST_NODE = null;

    private FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
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

    @Test
    void shouldFindBottomHalfHeightCorrectlyUntilLastNode() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);
        graph.add(componentNode2, componentNode3);

        mockNodeHeight(componentNode2, 70, 250);

        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, componentNode1, UNTIL_LAST_NODE);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(250);
    }

    @Test
    void shouldFindBottomHalfHeightCorrectlyWhenScopedNode() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, forkNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(forkNode1, componentNode3);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode3, componentNode4);

        forkNode1.addToScope(componentNode2);
        forkNode1.addToScope(componentNode3);

        mockNodeHeight(componentNode2, 70, 450);

        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, componentNode1, componentNode4);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(Half.of(DEFAULT_HEIGHT + 70 + 450 + 5 + 5));
    }

    @Test
    void shouldFindBottomHalfHeightCorrectlyWhenTallestAfterScopedNode() {
        // Given
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(componentNode2, forkNode2);
        graph.add(forkNode2, componentNode3);
        graph.add(componentNode3, componentNode4);
        graph.add(componentNode4, componentNode5);

        forkNode1.addToScope(componentNode2);
        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode4);
        forkNode2.addToScope(componentNode3);

        mockNodeHeight(componentNode4, 70, 410);

        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, componentNode2, componentNode5);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(410);
    }

    @Test
    void shouldFindBottomHalfCorrectlyBetweenTwoSubsequentScopedNodes() {
        // Given
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode2, componentNode2);
        graph.add(componentNode1, forkNode3);
        graph.add(componentNode2, forkNode3);
        graph.add(forkNode3, componentNode3);
        graph.add(forkNode3, componentNode4);

        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(forkNode3);

        forkNode2.addToScope(componentNode1);
        forkNode2.addToScope(componentNode2);

        forkNode3.addToScope(componentNode3);
        forkNode3.addToScope(componentNode4);

        mockNodeHeight(componentNode4, 70, 410);

        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, forkNode2, UNTIL_LAST_NODE);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(Half.of(DEFAULT_HEIGHT + 70 + 410 + 5 + 5));
    }
}