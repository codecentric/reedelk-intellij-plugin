package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import org.junit.jupiter.api.Test;

class RemoveGraphNodeStrategyTest extends AbstractGraphTest {

    @Test
    void shouldRemoveSuccessorOfRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        Strategy strategy = new RemoveGraphNodeStrategy(graph);
        strategy.execute(componentNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(1)
                .root().is(root)
                .and().successorsOf(root).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithoutSuccessor() {
        // Given

        // When

        // The
    }

    @Test
    void shouldRemoveNodeWithSuccessor() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldRemoveNodeWithScopedNodePredecessor() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldRemoveNodeBelongingToScope() {
        // Given

        // When

        // Then
    }

    @Test
    void shouldRemoveNodeWithMultiplePredecessors() {
        // Given

        // When

        // Then
    }

}