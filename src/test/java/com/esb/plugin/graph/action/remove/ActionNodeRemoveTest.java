package com.esb.plugin.graph.action.remove;

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
    void shouldRemoveNodeFromScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, componentNode1);

        forkNode1.addToScope(componentNode1);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, componentNode1);
        action.execute(modifiableGraph);

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
        ActionNodeRemove action = new ActionNodeRemove(testPlaceholderProvider, forkNode2);
        action.execute(modifiableGraph);

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