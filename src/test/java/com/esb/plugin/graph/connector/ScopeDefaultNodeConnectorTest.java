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
        graph.add(root, componentNode1);
        modifiableGraph = new FlowGraphChangeAware(graph);
    }

    @Test
    void shouldNotAddScopeGraphIfNoModificationsAreMade() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choiceNode1);
        scopeGraph.add(choiceNode1, componentNode2);
        choiceNode1.addToScope(componentNode2);

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
        scopeGraph.root(choiceNode1);
        scopeGraph.add(choiceNode1, componentNode2);
        choiceNode1.addToScope(componentNode2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.addPredecessor(componentNode1);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(choiceNode1)
                .and().successorsOf(choiceNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldAddSuccessorCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choiceNode1);
        scopeGraph.add(choiceNode1, componentNode2);
        choiceNode1.addToScope(componentNode2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.addSuccessor(componentNode1);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .root().is(root)
                .and().successorsOf(componentNode2).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isEmpty();
    }

    @Test
    void shouldCorrectlyAddToScope() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choiceNode1);
        scopeGraph.add(choiceNode1, componentNode2);
        choiceNode1.addToScope(componentNode2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.addPredecessor(componentNode1);
        connector.addToScope(choiceNode3);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .node(choiceNode3).scopeContainsExactly(choiceNode1);
    }

    @Test
    void shouldAddScopeGraphCorrectly() {
        // Given
        FlowGraph scopeGraph = new FlowGraphImpl();
        scopeGraph.root(choiceNode1);
        scopeGraph.add(choiceNode1, componentNode2);
        choiceNode1.addToScope(componentNode2);

        // When
        Connector connector = new ScopedNodeConnector(modifiableGraph, scopeGraph);
        connector.add();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .contains(choiceNode1)
                .contains(componentNode2)
                .successorsOf(choiceNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }
}
