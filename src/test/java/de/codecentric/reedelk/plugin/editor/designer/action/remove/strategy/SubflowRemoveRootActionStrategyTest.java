package de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

class SubflowRemoveRootActionStrategyTest extends AbstractGraphTest {

    @Test
    void shouldRemoveRootAndSetSuccessorAsNewRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        ActionStrategy strategy = new SubflowRemoveRootActionStrategy(graph, placeholderProvider);
        strategy.execute(root);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(1)
                .root().is(componentNode1)
                .and().successorsOf(componentNode1).isEmpty();
    }

    @Test
    void shouldRemoveLastNodeOfSubflow() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        // When
        ActionStrategy strategy = new SubflowRemoveRootActionStrategy(graph, placeholderProvider);
        strategy.execute(root);

        // Then
        PluginAssertion.assertThat(graph).isEmpty();
    }

    @Test
    void shouldRemoveScopedNodeAndAllNodesInsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode1, componentNode2);

        forkNode1.addToScope(componentNode1);
        forkNode1.addToScope(componentNode2);

        // When
        ActionStrategy strategy = new SubflowRemoveRootActionStrategy(graph, placeholderProvider);
        strategy.execute(forkNode1);

        // Then
        PluginAssertion.assertThat(graph).isEmpty();
    }

    @Test
    void shouldRemoveScopedNodeAndSetNewRootFirstNodeOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(forkNode1);
        graph.add(forkNode1, componentNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        forkNode1.addToScope(componentNode1);
        forkNode1.addToScope(componentNode2);

        // When
        ActionStrategy strategy = new SubflowRemoveRootActionStrategy(graph, placeholderProvider);
        strategy.execute(forkNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(componentNode3)
                .and().successorsOf(componentNode3).isEmpty();
    }
}