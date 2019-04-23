package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

class FindScopesTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReturnStackForTwoNestedScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice2.addToScope(n3);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n3);

        // Then
        assertThat(scopes.pop()).isEqualTo(choice2); // innermost is choice 2
        assertThat(scopes.pop()).isEqualTo(choice1); // outermost is choice 1
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnStackForThreeNestedScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, choice3);
        graph.add(choice3, n3);
        graph.add(n3, n4);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice2.addToScope(n2);
        choice2.addToScope(choice3);
        choice3.addToScope(n3);
        choice3.addToScope(n4);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n4);

        // Then
        assertThat(scopes.pop()).isEqualTo(choice3);
        assertThat(scopes.pop()).isEqualTo(choice2);
        assertThat(scopes.pop()).isEqualTo(choice1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnEmptyStack() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, n1);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n1);

        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnEmptyStackForRoot() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, root);

        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenElementRightOutsideInnermostScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice2.addToScope(n2);
        choice1.addToScope(n3);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n3);

        // Then
        assertThat(scopes.pop()).isEqualTo(choice1); // scope is only choice 1
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenChoiceIsRootWithoutElementsInTheScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, choice1);

        // Then
        assertThat(scopes.pop()).isEqualTo(choice1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenChoiceContainsSingleScopeElement() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        choice1.addToScope(n1);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n1);

        // Then
        assertThat(scopes.pop()).isEqualTo(choice1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnScopeWhenMultipleDisjointNestedScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);

        // Upper layer
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);
        graph.add(n3, n4);
        graph.add(n4, n5);

        // Lower layer
        graph.add(choice1, choice3);
        graph.add(choice3, n6);
        graph.add(n6, choice4);
        graph.add(choice4, n7);
        graph.add(n7, n8);
        graph.add(n8, n11);

        graph.add(choice4, n9);
        graph.add(n9, choice5);
        graph.add(choice5, n10);
        graph.add(n10, n11);

        // Setting up the scopes
        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice1.addToScope(n3);
        choice1.addToScope(n4);
        choice1.addToScope(n5);
        choice1.addToScope(choice3);

        choice2.addToScope(n2);

        choice3.addToScope(n6);
        choice3.addToScope(choice4);
        choice3.addToScope(n11);

        choice4.addToScope(n7);
        choice4.addToScope(n8);
        choice4.addToScope(n9);
        choice4.addToScope(choice5);

        choice5.addToScope(n10);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n10);

        // Then
        assertThat(scopes.pop()).isEqualTo(choice5);
        assertThat(scopes.pop()).isEqualTo(choice4);
        assertThat(scopes.pop()).isEqualTo(choice3);
        assertThat(scopes.pop()).isEqualTo(choice1);
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldReturnEmptyScopeWhenNodeRightAfterScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, n2);
        choice1.addToScope(n1);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n2);

        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void shouldReturnCorrectScopeWhenTwoAdjacentScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);

        choice1.addToScope(n1);
        choice2.addToScope(n2);

        // When
        Stack<ScopedDrawable> scopes = FindScopes.of(graph, n2);

        // Then
        assertThat(scopes).containsExactly(choice2);
    }

}