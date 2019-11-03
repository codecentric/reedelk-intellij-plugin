package com.reedelk.plugin.graph.action.remove.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

class SubflowRemoveRootStrategyTest extends AbstractGraphTest {

    @Mock
    private PlaceholderNode mockPlaceholder;

    @Test
    void shouldRemoveRootAndSetSuccessorAsNewRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        Strategy strategy = new SubflowRemoveRootStrategy(graph);
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
        Strategy strategy = new SubflowRemoveRootStrategy(graph);
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
        Strategy strategy = new SubflowRemoveRootStrategy(graph);
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
        Strategy strategy = new SubflowRemoveRootStrategy(graph);
        strategy.execute(forkNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(componentNode3)
                .and().successorsOf(componentNode3).isEmpty();
    }

    private class TestPlaceholderProvider implements PlaceholderProvider {
        @Override
        public Optional<PlaceholderNode> get() {
            return Optional.of(mockPlaceholder);
        }
    }
}