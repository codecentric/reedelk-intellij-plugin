package de.codecentric.reedelk.plugin.graph;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlowGraphImplTest extends AbstractGraphTest {

    private FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        graph = provider.createGraph();
    }

    @Test
    void shouldAddRootCorrectly() {
        // When
        graph.root(root);

        // Then
        assertThat(graph.root()).isEqualTo(root);
    }

    @Test
    void shouldReplaceRootCorrectly() {
        // Given
        graph.root(root);

        // When
        graph.root(componentNode1);

        // Then
        assertThat(graph.root()).isEqualTo(componentNode1);
    }

    @Test
    void shouldReturnAllNodes() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        Collection<GraphNode> nodes = graph.nodes();

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(root, componentNode1, componentNode2);
    }

    @Test
    void shouldReturnCorrectPredecessors() {
        // Given
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        // When
        List<GraphNode> predecessors = graph.predecessors(componentNode3);

        // Then
        assertThat(predecessors).containsExactlyInAnyOrder(componentNode1, componentNode2);
    }

    @Test
    void shouldReturnCorrectSuccessors() {
        // Given
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);

        // When
        List<GraphNode> successors = graph.successors(routerNode1);

        // Then
        assertThat(successors).containsExactlyInAnyOrder(componentNode1, componentNode2);
    }

    @Test
    void shouldCopyGraphCorrectly() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        FlowGraph copy = graph.copy();

        // Then
        assertThat(copy).isNotEqualTo(graph);
        assertThat(copy.successors(root)).containsExactly(componentNode1);
        assertThat(copy.successors(componentNode1)).containsExactly(componentNode2);
    }

    @Test
    void shouldCountNodesCorrectly() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        int nodesCount = graph.nodesCount();

        // Then
        assertThat(nodesCount).isEqualTo(3);
    }

    @Test
    void shouldReturnIsEmptyTrue() {
        // Expect
        assertThat(graph.isEmpty()).isTrue();
    }

    @Test
    void shouldReturnIsEmptyFalse() {
        // Whe
        graph.root(root);

        // Expect
        assertThat(graph.isEmpty()).isFalse();
    }

    @Test
    void shouldCorrectlyRemoveEdge() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);

        assertThat(graph.successors(root)).containsExactly(componentNode1);

        // When
        graph.remove(root, componentNode1);

        // Then
        assertThat(graph.successors(root)).isEmpty();
        assertThat(graph.nodesCount()).isEqualTo(2);
    }

    @Test
    void shouldCorrectlyReturnSingleEndNode() {
        // Given
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        List<GraphNode> graphNodes = graph.endNodes();

        // Then
        assertThat(graphNodes).containsOnly(componentNode1);
    }

    @Test
    void shouldCorrectlyReturnMultipleEndNodes() {
        // Given
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode1, componentNode2);

        // When
        List<GraphNode> graphNodes = graph.endNodes();

        // Then
        assertThat(graphNodes).containsExactlyInAnyOrder(componentNode1, componentNode2);
    }
}