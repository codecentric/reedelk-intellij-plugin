package de.codecentric.reedelk.plugin.editor.designer.action.remove;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphChangeAware;
import org.junit.jupiter.api.Test;

class FlowActionNodeRemoveTest extends AbstractGraphTest {

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
        FlowActionNodeRemove action = new FlowActionNodeRemove(componentNode1, placeholderProvider);
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
        FlowActionNodeRemove action = new FlowActionNodeRemove(forkNode2, placeholderProvider);
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

    @Test
    void shouldRemoveOnlySuccessorOfRouterAndAddPlaceholder() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        routerNode1.addToScope(componentNode1);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        FlowActionNodeRemove action = new FlowActionNodeRemove(componentNode1, placeholderProvider);
        action.execute(modifiableGraph);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(3)
                .root().is(root).and()
                .successorsOf(root).isOnly(routerNode1).and()
                .successorsOf(routerNode1).isOnly(placeholderNode1).and()
                .node(routerNode1).scopeContainsExactly(placeholderNode1).and()
                .successorsOf(placeholderNode1).isEmpty();
    }

    @Test
    void shouldRemoveForkNestedIntoTryCatchAsFirstChildAndAddPlaceholder() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, tryCatchNode1);
        graph.add(tryCatchNode1, forkNode1);
        graph.add(tryCatchNode1, componentNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        tryCatchNode1.addToScope(forkNode1);
        tryCatchNode1.addToScope(componentNode1);
        forkNode1.addToScope(componentNode2);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        FlowActionNodeRemove action = new FlowActionNodeRemove(forkNode1, placeholderProvider);
        action.execute(modifiableGraph);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(5)
                .root().is(root).and()
                .successorsOf(root).isOnly(tryCatchNode1).and()
                .successorsOf(tryCatchNode1).areExactly(placeholderNode1, componentNode1).and()
                .node(tryCatchNode1).scopeContainsExactly(placeholderNode1, componentNode1).and()
                .successorsOf(placeholderNode1).isOnly(componentNode3).and()
                .successorsOf(componentNode1).isOnly(componentNode3);
    }
}