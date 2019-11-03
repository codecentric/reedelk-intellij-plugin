package com.reedelk.plugin.graph.action.remove.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveScopedGraphNodeStrategyTest extends AbstractGraphTest {

    private FlowGraph graph;
    private Strategy strategy;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.graph = provider.createGraph();
        this.graph.root(root);
        this.strategy = new RemoveScopedGraphNodeStrategy(graph, placeholderProvider);
    }

    @Test
    void shouldRemoveEmptyScopedGraphNode() {
        // Given
        graph.add(root, routerNode1);

        // When
        strategy.execute(routerNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(1)
                .root().is(root)
                .and().successorsOf(root).isEmpty();
    }

    @Test
    void shouldRemoveScopedGraphNodeContainingEmptyScopedNode() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, routerNode2);
        routerNode1.addToScope(routerNode2);

        // When
        strategy.execute(routerNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(1)
                .root().is(root)
                .and().successorsOf(root).isEmpty();
    }

    @Test
    void shouldRemoveScopedGraphNodeContainingNotEmptyScopedNode() {
        // Given
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        forkNode1.addToScope(forkNode2);
        forkNode2.addToScope(componentNode1);
        forkNode2.addToScope(componentNode2);

        // When
        strategy.execute(forkNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(2)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isEmpty();
    }
}