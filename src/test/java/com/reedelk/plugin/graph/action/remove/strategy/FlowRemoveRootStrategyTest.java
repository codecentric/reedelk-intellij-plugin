package com.reedelk.plugin.graph.action.remove.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class FlowRemoveRootStrategyTest extends AbstractGraphTest {

    @Mock
    private PlaceholderNode mockPlaceholder;

    @Test
    void shouldRemoveRootAndAddPlaceholderWhenRootFollowedByNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        Strategy strategy = new FlowRemoveRootStrategy(graph, new TestPlaceholderProvider());
        strategy.execute(root);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(2)
                .root().is(mockPlaceholder)
                .and().successorsOf(mockPlaceholder).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isEmpty();
    }

    @Test
    void shouldRemoveRootAndLeaveEmptyGraphWhenRootIsOnlyNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        // When
        Strategy strategy = new FlowRemoveRootStrategy(graph, new TestPlaceholderProvider());
        strategy.execute(root);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(0)
                .isEmpty();
    }

    private class TestPlaceholderProvider implements PlaceholderProvider {
        @Override
        public PlaceholderNode get() {
            return mockPlaceholder;
        }
    }
}