package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

class PrecedingScopedNodeTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;


    @Test
    void shouldAddSuccessorInsideScopeWhenNoSuccessors() {
        // Given
        // We drop the node inside the fork node 1 "scope box"
        Point componentNode1DropPoint = new Point(240, 70);

        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);

        root.setPosition(55, 75);
        forkNode1.setPosition(195, 75);

        PrecedingScopedNode strategy =
                new PrecedingScopedNode(graph, componentNode1DropPoint, componentNode1, graphics);

        // When
        strategy.execute(forkNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(componentNode1)
                .and().node(forkNode1).scopeContainsExactly(componentNode1)
                .and().successorsOf(componentNode1).isEmpty();
    }

    @Test
    void shouldAddSuccessorOutsideScopeWhenNoSuccessors() {
        // Given
        // We drop the node right outside the fork node 1 "scope box"
        Point componentNode1DropPoint = new Point(260, 70);

        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);

        root.setPosition(550, 75);
        forkNode1.setPosition(195, 75);

        PrecedingScopedNode strategy =
                new PrecedingScopedNode(graph, componentNode1DropPoint, componentNode1, graphics);

        // When
        strategy.execute(forkNode1);

        // Then
        PluginAssertion.assertThat(graph)
                .root().is(root)
                .and().successorsOf(root).isOnly(forkNode1)
                .and().successorsOf(forkNode1).isOnly(componentNode1)
                .and().node(forkNode1).scopeIsEmpty();

    }
}