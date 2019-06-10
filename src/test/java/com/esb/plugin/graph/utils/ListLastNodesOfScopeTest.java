package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ListLastNodesOfScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnCorrectlyLastNodesFromInnerScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode2.addToScope(componentNode1);
        choiceNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, choiceNode2);

        // Then
        assertThat(lastNodesOfScope).containsExactlyInAnyOrder(componentNode1, componentNode2);
    }

    @Test
    void shouldReturnCorrectlyLastNodesFromOuterScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode2.addToScope(componentNode1);
        choiceNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(componentNode3);
    }

    @Test
    void shouldReturnCorrectlyLastNodesWhenInnerNodeIsScopedNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        choiceNode1.addToScope(choiceNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(choiceNode2);
    }

    @Test
    void shouldReturnCorrectlyLastNodeOfScopeWhenThreeNestedScopeNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, choiceNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(choiceNode3);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(choiceNode3);
    }

    @Test
    void shouldReturnCorrectlyLastNodeOfScopeWhenNestedContainsNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode1, componentNode2);
        graph.add(choiceNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode2);
        choiceNode2.addToScope(componentNode3);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactlyInAnyOrder(componentNode1, componentNode2, componentNode3);
    }

    @Test
    void shouldReturnCorrectlyLastNodeOfScopeWhenMultipleLevelScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(componentNode1, choiceNode3);
        graph.add(choiceNode3, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);
        graph.add(choiceNode2, componentNode5);
        graph.add(componentNode5, componentNode6);
        graph.add(componentNode6, componentNode7);
        graph.add(componentNode7, componentNode4);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(choiceNode3);

        choiceNode2.addToScope(componentNode5);
        choiceNode2.addToScope(componentNode6);
        choiceNode2.addToScope(componentNode7);

        choiceNode3.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactlyInAnyOrder(componentNode3, componentNode7);
    }

    @Test
    void shouldReturnCorrectlyLastNodesOfScopeForTwoNestedForks() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(componentNode1, forkNode2);
        graph.add(forkNode2, componentNode2);
        graph.add(forkNode1, componentNode3);
        graph.add(componentNode3, forkNode3);
        graph.add(forkNode3, componentNode4);
        graph.add(forkNode3, componentNode5);
        graph.add(componentNode2, componentNode6);
        graph.add(componentNode4, componentNode6);
        graph.add(componentNode5, componentNode6);

        forkNode1.addToScope(componentNode1);
        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(forkNode3);
        forkNode1.addToScope(componentNode2);
        forkNode1.addToScope(componentNode3);
        forkNode3.addToScope(componentNode4);
        forkNode3.addToScope(componentNode5);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, forkNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactlyInAnyOrder(componentNode2, componentNode4, componentNode5);
    }
}