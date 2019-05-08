package com.esb.plugin.graph.connector;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScopeDefaultNodeConnectorTest extends AbstractGraphTest {

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
        new ScopedNodeConnector(modifiableGraph, scopeGraph);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isNotChanged();
    }

    @Test
    void shouldAddPredecessorCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.addPredecessor(n1);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .root().is(root)
                .and().successorsOf(root).isExactly(n1)
                .and().successorsOf(n1).isExactly(choice1)
                .and().successorsOf(choice1).isExactly(n2)
                .and().successorsOf(n2).isEmpty();
    }

    @Test
    void shouldAddSuccessorCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.addSuccessor(n1);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .root().is(root)
                .and().successorsOf(n2).isExactly(n1)
                .and().successorsOf(n1).isEmpty();
    }

    @Test
    void shouldCorrectlyAddToScope() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.addPredecessor(n1);
        connector.addToScope(choice3);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .node(choice3).scopeContainsExactly(choice1);
    }

    @Test
    void shouldAddScopeGraphCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choice1);
        scopeGraph.add(choice1, n2);
        choice1.addToScope(n2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.add();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .contains(choice1)
                .contains(n2)
                .successorsOf(choice1).isExactly(n2)
                .and().successorsOf(n2).isEmpty();
    }
}
