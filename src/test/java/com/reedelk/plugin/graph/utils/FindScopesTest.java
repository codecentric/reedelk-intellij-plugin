package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

class FindScopesTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReturnStackForTwoNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode3);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode3);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode2); // innermost is router 2
        assertThat(scopes.pop()).isEqualTo(routerNode1); // outermost is router 1
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnStackForThreeNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, routerNode3);
        graph.add(routerNode3, componentNode3);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);
        routerNode2.addToScope(routerNode3);
        routerNode3.addToScope(componentNode3);
        routerNode3.addToScope(componentNode4);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode4);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode3);
        assertThat(scopes.pop()).isEqualTo(routerNode2);
        assertThat(scopes.pop()).isEqualTo(routerNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnEmptyStack() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode1);

        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnEmptyStackForRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, root);

        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenElementRightOutsideInnermostScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode2);
        routerNode1.addToScope(componentNode3);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode3);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode1); // scope is only router 1
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenRouterIsRootWithoutElementsInTheScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, routerNode1);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenRouterContainsSingleScopeElement() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        routerNode1.addToScope(componentNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode1);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenMultipleDisjointNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        // Upper layer
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);
        graph.add(componentNode4, componentNode5);

        // Lower layer
        graph.add(routerNode1, routerNode3);
        graph.add(routerNode3, componentNode6);
        graph.add(componentNode6, routerNode4);
        graph.add(routerNode4, componentNode7);
        graph.add(componentNode7, componentNode8);
        graph.add(componentNode8, componentNode11);

        graph.add(routerNode4, componentNode9);
        graph.add(componentNode9, routerNode5);
        graph.add(routerNode5, componentNode10);
        graph.add(componentNode10, componentNode11);

        // Setting up the scopes
        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode3);
        routerNode1.addToScope(componentNode4);
        routerNode1.addToScope(componentNode5);
        routerNode1.addToScope(routerNode3);

        routerNode2.addToScope(componentNode2);

        routerNode3.addToScope(componentNode6);
        routerNode3.addToScope(routerNode4);
        routerNode3.addToScope(componentNode11);

        routerNode4.addToScope(componentNode7);
        routerNode4.addToScope(componentNode8);
        routerNode4.addToScope(componentNode9);
        routerNode4.addToScope(routerNode5);

        routerNode5.addToScope(componentNode10);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode10);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode5);
        assertThat(scopes.pop()).isEqualTo(routerNode4);
        assertThat(scopes.pop()).isEqualTo(routerNode3);
        assertThat(scopes.pop()).isEqualTo(routerNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldReturnEmptyScopeWhenNodeRightAfterScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, componentNode2);
        routerNode1.addToScope(componentNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode2);

        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldReturnCorrectScopeWhenTwoAdjacentScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);

        routerNode1.addToScope(componentNode1);
        routerNode2.addToScope(componentNode2);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode2);

        // Then
        assertThat(scopes).containsExactly(routerNode2);
    }

    @Test
    void shouldReturnScopeItselfWhenNodeIsScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, routerNode1);

        // Then
        assertThat(scopes).containsExactly(routerNode1);
    }

    @Test
    void shouldReturnScopeItselfOnTopOfStackWhenNodeIsScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        graph.add(routerNode2, routerNode3);

        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(routerNode3);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, routerNode3);

        // Then
        assertThat(scopes.pop()).isEqualTo(routerNode3);
        assertThat(scopes.pop()).isEqualTo(routerNode2);
        assertThat(scopes.pop()).isEqualTo(routerNode1);
    }
}