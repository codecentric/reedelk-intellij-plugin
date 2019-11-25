package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import com.reedelk.plugin.editor.designer.action.ActionStrategy;
import com.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

class FlowRemoveRootActionStrategyTest extends AbstractGraphTest {

    @Mock
    private PlaceholderNode mockPlaceholder;

    @Test
    void shouldRemoveRootAndAddPlaceholderWhenRootFollowedByNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        ActionStrategy strategy = new FlowRemoveRootActionStrategy(graph, new TestPlaceholderProvider());
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
        ActionStrategy strategy = new FlowRemoveRootActionStrategy(graph, new TestPlaceholderProvider());
        strategy.execute(root);

        // Then
        PluginAssertion.assertThat(graph)
                .nodesCountIs(0)
                .isEmpty();
    }

    private class TestPlaceholderProvider implements PlaceholderProvider {
        @Override
        public Optional<PlaceholderNode> get() {
            return Optional.of(mockPlaceholder);
        }

        @Override
        public Optional<PlaceholderNode> get(String description) {
            return Optional.of(mockPlaceholder);
        }
    }
}