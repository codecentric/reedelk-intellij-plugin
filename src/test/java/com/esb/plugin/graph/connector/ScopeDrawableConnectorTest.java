package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScopeDrawableConnectorTest extends AbstractGraphTest {

    private FlowGraphChangeAware modifiableGraph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, n1);
        modifiableGraph = new FlowGraphChangeAware(graph);
    }

    @Test
    void shouldNotAddScopeGraphIfNoModificationsAreMade() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        new ScopeDrawableConnector(modifiableGraph, scopeGraph);

        // Then
        assertThat(modifiableGraph.isChanged()).isFalse();
    }

    @Test
    void shouldAddPredecessorCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopeDrawableConnector(modifiableGraph, scopeGraph);
        connector.addPredecessor(n1);

        // Then
        assertThat(modifiableGraph.isChanged()).isTrue();
        assertThatRootIs(modifiableGraph, root);
        assertThatSuccessorsAreExactly(modifiableGraph, root, n1);
        assertThatSuccessorsAreExactly(modifiableGraph, n1, choice1);
        assertThatSuccessorsAreExactly(modifiableGraph, choice1, n2);
        assertThatSuccessorsAreExactly(modifiableGraph, n2);
    }

    @Test
    void shouldAddSuccessorCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopeDrawableConnector(modifiableGraph, scopeGraph);
        connector.addSuccessor(n1);

        // Then
        assertThatRootIs(modifiableGraph, root);
        assertThatSuccessorsAreExactly(modifiableGraph, n2, n1);
        assertThatSuccessorsAreExactly(modifiableGraph, n1);
    }

    @Test
    void shouldCorrectlyAddToScope() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopeDrawableConnector(modifiableGraph, scopeGraph);
        connector.addPredecessor(n1);
        connector.addToScope(choice3);

        // Then
        assertThat(choice3.getScope()).containsExactly(choice1);
    }

    @Test
    void shouldAddScopeGraphCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopeDrawableConnector(modifiableGraph, scopeGraph);
        connector.add();

        // Then
        assertThat(modifiableGraph.isChanged()).isTrue();
        assertThat(modifiableGraph.nodes()).contains(choice1);
        assertThat(modifiableGraph.nodes()).contains(n2);
        assertThatSuccessorsAreExactly(modifiableGraph, choice1, n2);
        assertThatSuccessorsAreExactly(modifiableGraph, n2);
    }
}
