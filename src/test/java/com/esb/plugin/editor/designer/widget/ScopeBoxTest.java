package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScopeBoxTest extends AbstractGraphTest {

    private FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
    }

    @Test
    void shouldComputeNodesOnBoundariesCorrectly() {
        // Given
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        routerNode1.addToScope(componentNode1);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(365, 155);

        // When
        ScopeBox.NodesOnBoundaries boundaries =
                ScopeBox.findNodesOnBoundaries(graph, routerNode1);

        // Then
        assertThat(boundaries.minX).isEqualTo(routerNode1);
        assertThat(boundaries.maxX).isEqualTo(componentNode1);
        assertThat(boundaries.minY).isEqualTo(routerNode1);
        assertThat(boundaries.maxY).isEqualTo(routerNode1);
    }

    @Test
    void shouldComputeNodesOnBoundariesCorrectlyWhenEmptyScopedNode() {
        // Given
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(365, 155);

        // When
        ScopeBox.NodesOnBoundaries boundaries =
                ScopeBox.findNodesOnBoundaries(graph, routerNode1);

        // Then
        assertThat(boundaries.minX).isEqualTo(routerNode1);
        assertThat(boundaries.maxX).isEqualTo(routerNode1);
        assertThat(boundaries.minY).isEqualTo(routerNode1);
        assertThat(boundaries.maxY).isEqualTo(routerNode1);
    }

    @Test
    void shouldComputeNodesOnBoundariesCorrectlyWhenMultipleChildrenAtSameLevelWithMoreNestedScopes() {
        // Given
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode3);
        graph.add(routerNode3, componentNode2);

        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode3);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode3);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode4);

        routerNode3.addToScope(componentNode2);
        routerNode2.addToScope(componentNode3);

        root.setPosition(65, 235);
        routerNode1.setPosition(215, 235);
        componentNode1.setPosition(365, 160);
        routerNode3.setPosition(495, 160);
        componentNode2.setPosition(630, 160);

        routerNode2.setPosition(365, 310);
        componentNode3.setPosition(495, 310);
        componentNode4.setPosition(630, 310);

        // When
        ScopeBox.NodesOnBoundaries boundaries =
                ScopeBox.findNodesOnBoundaries(graph, routerNode1);

        // Then
        assertThat(boundaries.minX).isEqualTo(routerNode1);
        assertThat(boundaries.maxX).isEqualTo(componentNode2);
        assertThat(boundaries.minY).isEqualTo(componentNode2);
        assertThat(boundaries.maxY).isIn(componentNode4, routerNode2, componentNode3);
    }
}