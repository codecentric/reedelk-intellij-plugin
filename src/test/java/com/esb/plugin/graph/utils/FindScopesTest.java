package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

class FindScopesTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReturnStackForTwoNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode3);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode3);

        // Then
        assertThat(scopes.pop()).isEqualTo(choiceNode2); // innermost is choice 2
        assertThat(scopes.pop()).isEqualTo(choiceNode1); // outermost is choice 1
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnStackForThreeNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, choiceNode3);
        graph.add(choiceNode3, componentNode3);
        graph.add(componentNode3, componentNode4);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode2);
        choiceNode2.addToScope(choiceNode3);
        choiceNode3.addToScope(componentNode3);
        choiceNode3.addToScope(componentNode4);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode4);

        // Then
        assertThat(scopes.pop()).isEqualTo(choiceNode3);
        assertThat(scopes.pop()).isEqualTo(choiceNode2);
        assertThat(scopes.pop()).isEqualTo(choiceNode1);
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
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode2);
        choiceNode1.addToScope(componentNode3);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode3);

        // Then
        assertThat(scopes.pop()).isEqualTo(choiceNode1); // scope is only choice 1
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenChoiceIsRootWithoutElementsInTheScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, choiceNode1);

        // Then
        assertThat(scopes.pop()).isEqualTo(choiceNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenChoiceContainsSingleScopeElement() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        choiceNode1.addToScope(componentNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode1);

        // Then
        assertThat(scopes.pop()).isEqualTo(choiceNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenMultipleDisjointNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);

        // Upper layer
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);
        graph.add(componentNode4, componentNode5);

        // Lower layer
        graph.add(choiceNode1, choiceNode3);
        graph.add(choiceNode3, componentNode6);
        graph.add(componentNode6, choiceNode4);
        graph.add(choiceNode4, componentNode7);
        graph.add(componentNode7, componentNode8);
        graph.add(componentNode8, componentNode11);

        graph.add(choiceNode4, componentNode9);
        graph.add(componentNode9, choiceNode5);
        graph.add(choiceNode5, componentNode10);
        graph.add(componentNode10, componentNode11);

        // Setting up the scopes
        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(componentNode4);
        choiceNode1.addToScope(componentNode5);
        choiceNode1.addToScope(choiceNode3);

        choiceNode2.addToScope(componentNode2);

        choiceNode3.addToScope(componentNode6);
        choiceNode3.addToScope(choiceNode4);
        choiceNode3.addToScope(componentNode11);

        choiceNode4.addToScope(componentNode7);
        choiceNode4.addToScope(componentNode8);
        choiceNode4.addToScope(componentNode9);
        choiceNode4.addToScope(choiceNode5);

        choiceNode5.addToScope(componentNode10);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode10);

        // Then
        assertThat(scopes.pop()).isEqualTo(choiceNode5);
        assertThat(scopes.pop()).isEqualTo(choiceNode4);
        assertThat(scopes.pop()).isEqualTo(choiceNode3);
        assertThat(scopes.pop()).isEqualTo(choiceNode1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldReturnEmptyScopeWhenNodeRightAfterScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, componentNode2);
        choiceNode1.addToScope(componentNode1);

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
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);

        choiceNode1.addToScope(componentNode1);
        choiceNode2.addToScope(componentNode2);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, componentNode2);

        // Then
        assertThat(scopes).containsExactly(choiceNode2);
    }

    @Test
    void shouldReturnScopeItselfWhenNodeIsScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);

        // When
        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, choiceNode1);

        // Then
        assertThat(scopes).containsExactly(choiceNode1);
    }
}