package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
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
        graph = new FlowGraphImpl();
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
        graph.root(n1);

        // Then
        assertThat(graph.root()).isEqualTo(n1);
    }

    @Test
    void shouldReturnAllNodes() {
        // Given
        graph.root(root);
        graph.add(root, n1);
        graph.add(n1, n2);

        // When
        Collection<Drawable> nodes = graph.nodes();

        // Then
        assertThat(nodes).containsExactlyInAnyOrder(root, n1, n2);
    }

    @Test
    void shouldReturnCorrectPredecessors() {
        // Given
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);

        // When
        List<Drawable> predecessors = graph.predecessors(n3);

        // Then
        assertThat(predecessors).containsExactlyInAnyOrder(n1, n2);
    }

    @Test
    void shouldReturnCorrectSuccessors() {
        // Given
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, n2);

        // When
        List<Drawable> successors = graph.successors(choice1);

        // Then
        assertThat(successors).containsExactlyInAnyOrder(n1, n2);
    }

    @Test
    void shouldCopyGraphCorrectly() {
        // Given
        graph.root(root);
        graph.add(root, n1);
        graph.add(n1, n2);

        // When
        FlowGraph copy = graph.copy();

        // Then
        assertThat(copy).isNotEqualTo(graph);
        assertThat(copy.successors(root)).containsExactly(n1);
        assertThat(copy.successors(n1)).containsExactly(n2);
    }

    @Test
    void shouldCountNodesCorrectly() {
        // Given
        graph.root(root);
        graph.add(root, n1);
        graph.add(n1, n2);

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
        graph.add(root, n1);

        assertThat(graph.successors(root)).containsExactly(n1);

        // When
        graph.remove(root, n1);

        // Then
        assertThat(graph.successors(root)).isEmpty();
        assertThat(graph.nodesCount()).isEqualTo(2);
    }
}