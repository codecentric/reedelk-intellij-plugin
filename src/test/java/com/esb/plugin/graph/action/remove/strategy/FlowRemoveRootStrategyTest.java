package com.esb.plugin.graph.action.remove.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.Strategy;
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

    class TestPlaceholderProvider implements PlaceholderProvider {
        @Override
        public PlaceholderNode get() {
            return mockPlaceholder;
        }
    }
}