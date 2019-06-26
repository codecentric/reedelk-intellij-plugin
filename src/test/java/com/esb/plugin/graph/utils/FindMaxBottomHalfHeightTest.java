package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@MockitoSettings(strictness = Strictness.LENIENT)
class FindMaxBottomHalfHeightTest extends AbstractGraphTest {

    private GraphNode UNTIL_LAST_NODE = null;

    @Mock
    private Graphics2D graphics;
    private FlowGraph graph;

    private GraphNode componentNode1Spy;
    private GraphNode componentNode2Spy;
    private GraphNode componentNode3Spy;
    private GraphNode componentNode4Spy;
    private GraphNode componentNode5Spy;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();

        componentNode1Spy = spy(componentNode1);
        componentNode2Spy = spy(componentNode2);
        componentNode3Spy = spy(componentNode3);
        componentNode4Spy = spy(componentNode4);
        componentNode5Spy = spy(componentNode5);


        doReturn(80)
                .when(componentNode1Spy)
                .bottomHalfHeight(graphics);

        doReturn(140)
                .when(componentNode1Spy)
                .height(graphics);

        doReturn(230)
                .when(componentNode2Spy)
                .bottomHalfHeight(graphics);

        doReturn(310)
                .when(componentNode2Spy)
                .height(graphics);

        doReturn(100)
                .when(componentNode3Spy)
                .bottomHalfHeight(graphics);

        doReturn(150)
                .when(componentNode3Spy)
                .height(graphics);

        doReturn(410)
                .when(componentNode4Spy)
                .bottomHalfHeight(graphics);

        doReturn(470)
                .when(componentNode4Spy)
                .height(graphics);
    }

    @Test
    void shouldFindBottomHalfHeightCorrectlyUntilLastNode() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1Spy);
        graph.add(componentNode1Spy, componentNode2Spy);
        graph.add(componentNode2Spy, componentNode3Spy);


        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, componentNode1Spy, UNTIL_LAST_NODE);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(230);
    }

    @Test
    void shouldFindBottomHalfHeightCorrectlyWhenScopedNode() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1Spy);
        graph.add(componentNode1Spy, forkNode1);
        graph.add(forkNode1, componentNode2Spy);
        graph.add(forkNode1, componentNode3Spy);
        graph.add(componentNode2Spy, componentNode4Spy);
        graph.add(componentNode3Spy, componentNode4Spy);

        forkNode1.addToScope(componentNode2Spy);
        forkNode1.addToScope(componentNode3Spy);

        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, componentNode1Spy, componentNode4Spy);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(Half.of(310 + 150 + 5 + 5));
    }

    @Test
    void shouldFindBottomHalfHeightCorrectlyWhenTallestAfterScopedNode() {
        // Given
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode2Spy);
        graph.add(componentNode2Spy, forkNode2);
        graph.add(forkNode2, componentNode3Spy);
        graph.add(componentNode3Spy, componentNode4Spy);
        graph.add(componentNode4Spy, componentNode5Spy);

        forkNode1.addToScope(componentNode2Spy);
        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode4Spy);
        forkNode2.addToScope(componentNode3Spy);

        // When
        int maxBottomHalfHeight =
                FindMaxBottomHalfHeight.of(graph, graphics, componentNode2Spy, componentNode5Spy);

        // Then
        assertThat(maxBottomHalfHeight).isEqualTo(410);
    }

}