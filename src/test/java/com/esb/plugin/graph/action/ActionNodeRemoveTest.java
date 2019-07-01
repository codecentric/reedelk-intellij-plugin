package com.esb.plugin.graph.action;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ActionNodeRemoveTest extends AbstractGraphTest {

    @Mock
    private PlaceholderNode mockPlaceholder;

    private TestPlaceholderProvider testPlaceholderProvider;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.testPlaceholderProvider = new TestPlaceholderProvider();
    }

    @Test
    void shouldRemoveSuccessorOfRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(componentNode1);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, modifiableGraph, componentNode1);
        action.remove();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(1)
                .root().is(root)
                .and().successorsOf(root).isEmpty();
    }

    @Test
    void shouldRemoveRootAndAddPlaceholderWhenRootFollowedByNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, modifiableGraph, root);
        action.remove();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
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

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, modifiableGraph, root);
        action.remove();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(0)
                .isEmpty();
    }

    @Test
    void shouldRemoveNodeFromScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);

        forkNode1.addToScope(componentNode1);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, modifiableGraph, componentNode1);
        action.remove();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(2)
                .root().is(root).and()
                .successorsOf(root).isOnly(forkNode1).and()
                .node(forkNode1).scopeIsEmpty().and()
                .successorsOf(forkNode1).isEmpty();
    }

    @Test
    void shouldRemoveNestedScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);

        forkNode1.addToScope(forkNode2);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, modifiableGraph, forkNode2);
        action.remove();

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(2)
                .root().is(root).and()
                .successorsOf(root).isOnly(forkNode1).and()
                .successorsOf(forkNode1).isEmpty().and()
                .node(forkNode1).scopeIsEmpty();
    }

    class TestPlaceholderProvider implements ActionNodeRemove.PlaceholderProvider {
        @Override
        public PlaceholderNode get() {
            return mockPlaceholder;
        }
    }
}