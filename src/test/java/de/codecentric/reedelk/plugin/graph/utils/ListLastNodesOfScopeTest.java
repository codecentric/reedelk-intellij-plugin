package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ListLastNodesOfScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnCorrectlyLastNodesFromInnerScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode1);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode3);
        routerNode2.addToScope(componentNode1);
        routerNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode2);

        // Then
        assertThat(lastNodesOfScope).containsExactlyInAnyOrder(componentNode1, componentNode2);
    }

    @Test
    void shouldReturnCorrectlyLastNodesFromOuterScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, componentNode1);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode3);
        routerNode2.addToScope(componentNode1);
        routerNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(componentNode3);
    }

    @Test
    void shouldReturnCorrectlyLastNodesWhenInnerNodeIsScopedNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        routerNode1.addToScope(routerNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(routerNode2);
    }

    @Test
    void shouldReturnCorrectlyLastNodeOfScopeWhenThreeNestedScopeNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, routerNode3);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(routerNode3);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(routerNode3);
    }

    @Test
    void shouldReturnCorrectlyLastNodeOfScopeWhenNestedContainsNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode2);
        routerNode2.addToScope(componentNode3);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactlyInAnyOrder(componentNode1, componentNode2, componentNode3);
    }

    @Test
    void shouldReturnCorrectlyLastNodeOfScopeWhenMultipleLevelScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(componentNode1, routerNode3);
        graph.add(routerNode3, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);
        graph.add(routerNode2, componentNode5);
        graph.add(componentNode5, componentNode6);
        graph.add(componentNode6, componentNode7);
        graph.add(componentNode7, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(routerNode3);

        routerNode2.addToScope(componentNode5);
        routerNode2.addToScope(componentNode6);
        routerNode2.addToScope(componentNode7);

        routerNode3.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode1);

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

    @Test
    void shouldReturnCorrectlyLastNodesWhenScopeIsEmpty() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        // When
        Collection<GraphNode> lastNodesOfScope = ListLastNodesOfScope.from(graph, routerNode1);

        // Then
        assertThat(lastNodesOfScope).containsExactly(routerNode1);
    }
}