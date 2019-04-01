package com.esb.plugin.designer.graph;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DirectedGraphTest {

    private final LiteralNode n1 = LiteralNode.create("N1");
    private final LiteralNode n2 = LiteralNode.create("N2");
    private final LiteralNode n3 = LiteralNode.create("N3");
    private final LiteralNode n4 = LiteralNode.create("N3");
    private final LiteralNode n5 = LiteralNode.create("N3");
    private final LiteralNode n6 = LiteralNode.create("N3");


    @Test
    void shouldReturnCorrectCommonSuccessor() {
        // Given
        DirectedGraph<LiteralNode> graph = new DirectedGraph<>(n1);
        graph.putEdge(n1, n2);
        graph.putEdge(n1, n3);
        graph.putEdge(n2, n4);
        graph.putEdge(n3, n5);
        graph.putEdge(n4, n6);
        graph.putEdge(n5, n6);

        // When
        Optional<LiteralNode> successor = graph.commonSuccessor(n2, n3);

        // Then
        assertThat(successor).isNotNull();
        assertThat(successor.isPresent()).isTrue();

        LiteralNode actual = successor.get();
        assertThat(actual).isEqualTo(n6);
    }


    static class LiteralNode {

        private String value;

        static LiteralNode create(String value) {
            LiteralNode node = new LiteralNode();
            node.value = value;
            return node;
        }

    }
}