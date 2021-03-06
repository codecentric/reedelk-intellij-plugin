package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubFlowAddRootActionTest extends AbstractGraphTest {

    private SubFlowAddRootAction strategy;
    private FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        strategy = new SubFlowAddRootAction(graph, placeholderProvider);
    }

    @Test
    void shouldAddNewRootRouter() {
        // When
        strategy.execute(routerNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(routerNode1)
                .and().successorsOf(routerNode1).isOnly(placeholderNode1)
                .and().node(routerNode1).scopeContainsExactly(placeholderNode1)
                .and().successorsOf(placeholderNode1).isEmpty();
    }
}
