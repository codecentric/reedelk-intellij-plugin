package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

class PrecedingNodeWithOneSuccessorTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;

    @DisplayName("Scope node with successor outside scope")
    @Nested
    class WithSuccessorOutsideScope {

        @Test
        void shouldAddSuccessorInsideScope() {
            // Given
            // We drop the node inside the fork node 1 "scope box"
            Point componentNode2DropPoint = new Point(210, 80);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, forkNode1);
            graph.add(forkNode1, componentNode1);

            root.setPosition(55, 77);
            forkNode1.setPosition(165, 77);
            componentNode1.setPosition(280, 77);

            PrecedingNodeWithOneSuccessor strategy =
                    new PrecedingNodeWithOneSuccessor(graph, componentNode2DropPoint, forkNode1, graphics);

            // When
            strategy.execute(componentNode2);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(forkNode1)
                    .and().successorsOf(forkNode1).isOnly(componentNode2)
                    .and().node(forkNode1).scopeContainsExactly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isEmpty();
        }

        @Test
        void shouldAddSuccessorOutsideScope() {
            // Given
            // We drop the node right outside the form node 1 "scope box"
            Point componentNode2DropPoint = new Point(230, 50);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, forkNode1);
            graph.add(forkNode1, componentNode1);

            root.setPosition(55, 77);
            forkNode1.setPosition(165, 77);
            componentNode1.setPosition(280, 77);

            PrecedingNodeWithOneSuccessor strategy =
                    new PrecedingNodeWithOneSuccessor(graph, componentNode2DropPoint, forkNode1, graphics);

            // When
            strategy.execute(componentNode2);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(forkNode1)
                    .and().successorsOf(forkNode1).isOnly(componentNode2)
                    .and().node(forkNode1).scopeIsEmpty()
                    .and().successorsOf(componentNode2).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isEmpty();
        }
    }
}