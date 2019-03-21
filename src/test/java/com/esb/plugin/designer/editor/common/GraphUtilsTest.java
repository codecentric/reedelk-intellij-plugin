package com.esb.plugin.designer.editor.common;


import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphUtilsTest {

    /**
     *
     */
    @Test
    void shouldMaxNodesCountAtAnyLevelReturn0() {
        // Given
        MutableGraph<MyNode> testGraph = GraphBuilder.directed().build();

        // When
        int actualMax = GraphUtils.maxNodesCountAtAnyLevel(testGraph, null);

        // Then
        assertThat(actualMax).isEqualTo(0);
    }

    /**
     * N1
     */
    @Test
    void shouldMaxNodesCountAtAnyLevelReturn1WithOnlyRoot() {
        // Given
        MyNode node1 = new MyNode("Node1");

        MutableGraph<MyNode> testGraph = GraphBuilder.directed().build();
        testGraph.addNode(node1);

        // When
        int actualMax = GraphUtils.maxNodesCountAtAnyLevel(testGraph, node1);

        // Then
        assertThat(actualMax).isEqualTo(1);
    }

    /**
     * N1
     * |
     * N2
     * |
     * N3
     */
    @Test
    void shouldMaxNodesCountAtAnyLevelReturn1() {
        // Given
        MyNode node1 = new MyNode("Node1");
        MyNode node2 = new MyNode("Node2");
        MyNode node3 = new MyNode("Node3");

        MutableGraph<MyNode> testGraph = GraphBuilder.directed().build();
        testGraph.addNode(node1);
        testGraph.putEdge(node1, node2);
        testGraph.putEdge(node2, node3);

        // When
        int actualMax = GraphUtils.maxNodesCountAtAnyLevel(testGraph, node1);

        // Then
        assertThat(actualMax).isEqualTo(1);
    }

    /**
     * N1
     * |
     * N2
     * /   \
     * N3   N4
     * \  /
     * N5
     */
    @Test
    void shouldMaxNodesCountAtAnyLevelReturn2() {
        // Given
        MyNode node1 = new MyNode("Node1");
        MyNode node2 = new MyNode("Node2");
        MyNode node3 = new MyNode("Node3");
        MyNode node4 = new MyNode("Node4");
        MyNode node5 = new MyNode("Node5");

        MutableGraph<MyNode> testGraph = GraphBuilder.directed().build();
        testGraph.addNode(node1);
        testGraph.putEdge(node1, node2);
        testGraph.putEdge(node2, node3);
        testGraph.putEdge(node2, node4);
        testGraph.putEdge(node3, node5);
        testGraph.putEdge(node4, node5);

        // When
        int actualMax = GraphUtils.maxNodesCountAtAnyLevel(testGraph, node1);

        // Then
        assertThat(actualMax).isEqualTo(2);
    }

    /**
     * N1
     * /    \
     * N2    N3
     * / \   /  \
     * N4 N5 N6  N7
     * \ /   \  /
     * N8     N9
     */
    @Test
    void shouldMaxNodesCountAtAnyLevelReturn4() {
        // Given
        MyNode node1 = new MyNode("Node1");
        MyNode node2 = new MyNode("Node2");
        MyNode node3 = new MyNode("Node3");
        MyNode node4 = new MyNode("Node4");
        MyNode node5 = new MyNode("Node5");
        MyNode node6 = new MyNode("Node6");
        MyNode node7 = new MyNode("Node7");
        MyNode node8 = new MyNode("Node8");
        MyNode node9 = new MyNode("Node9");

        MutableGraph<MyNode> testGraph = GraphBuilder.directed().build();
        testGraph.addNode(node1);
        testGraph.putEdge(node1, node2);
        testGraph.putEdge(node1, node3);
        testGraph.putEdge(node2, node4);
        testGraph.putEdge(node2, node5);
        testGraph.putEdge(node3, node6);
        testGraph.putEdge(node3, node7);
        testGraph.putEdge(node4, node8);
        testGraph.putEdge(node5, node8);
        testGraph.putEdge(node6, node9);
        testGraph.putEdge(node7, node9);

        // When
        int actualMax = GraphUtils.maxNodesCountAtAnyLevel(testGraph, node1);

        // Then
        assertThat(actualMax).isEqualTo(4);
    }


    static class MyNode {
        private String name;

        MyNode(String name) {
            this.name = name;
        }
    }

}