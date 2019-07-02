package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveGraphNodeStrategyTest extends AbstractGraphTest {

    private FlowGraph graph;
    private Strategy strategy;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.graph = provider.createGraph();
        this.graph.root(root);
        this.strategy = new RemoveGraphNodeStrategy(graph);
    }

    @Test
    void shouldRemoveSuccessorOfRoot() {
        // Given
        graph.add(root, componentNode1);

        // When
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
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        strategy.execute(componentNode2);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(2)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithSuccessor() {
        // Given
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        strategy.execute(componentNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(2)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithScopedNodePredecessorWhenInsideScopeAndSuccessorOutside() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);

        // When
        strategy.execute(componentNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(4)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isAtIndex(componentNode2, 0)
                .and().node(routerNode1).scopeContainsExactly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithScopedNodePredecessorWhenInsideScopeAndSuccessorInside() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode1, componentNode4);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);
        routerNode1.addToScope(componentNode3);

        // When
        strategy.execute(componentNode2);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(5)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isAtIndex(componentNode1, 0)
                .and().successorsOf(routerNode1).isAtIndex(componentNode3, 1)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3)
                .and().successorsOf(componentNode1).isOnly(componentNode4)
                .and().successorsOf(componentNode3).isOnly(componentNode4)
                .and().successorsOf(componentNode4).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithScopedNodePredecessorWhenInsideScopeAndNoSuccessor() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);

        // When
        strategy.execute(componentNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(3)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).isAtIndex(componentNode2, 0)
                .and().node(routerNode1).scopeContainsExactly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithScopedNodePredecessorWhenOutsideScopeAndSuccessor() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, componentNode2);

        // When
        strategy.execute(componentNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(3)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().node(routerNode1).scopeIsEmpty()
                .and().successorsOf(routerNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithScopedNodePredecessorWhenOutsideScopeAndNoSuccessor() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        // When
        strategy.execute(componentNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(2)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().node(routerNode1).scopeIsEmpty()
                .and().successorsOf(routerNode1).isEmpty();
    }

    @Test
    void shouldRemoveNodeWithMultiplePredecessors() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode1, componentNode4);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);
        routerNode1.addToScope(componentNode3);

        // When
        strategy.execute(componentNode4);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(5)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode2, componentNode3)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode2, componentNode3)
                .and().successorsOf(componentNode1).isEmpty()
                .and().successorsOf(componentNode2).isEmpty()
                .and().successorsOf(componentNode3).isEmpty();
    }

    @Test
    void shouldRemoveScopedNodeAfterScopedNode() {
        // Given
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(componentNode1, routerNode2);
        graph.add(componentNode2, routerNode2);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(componentNode2);

        // When
        strategy.execute(routerNode2);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(4)
                .root().is(root)
                .and().successorsOf(root).isOnly(routerNode1)
                .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode2)
                .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode2)
                .and().successorsOf(componentNode1).isEmpty()
                .and().successorsOf(componentNode2).isEmpty();
    }
}