package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

class PrecedingNodeWithoutSuccessorTest extends AbstractGraphTest {

    @DisplayName("Scoped Node without successors")
    @Nested
    class WithPrecedingScopedNode {
        @Test
        void shouldAddSuccessorInsideScope() {
            // Given
            // We drop the node inside the fork node 1 "scope box"
            Point componentNode1DropPoint = new Point(255, 144);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, forkNode1);

            root.setPosition(65, 158);
            forkNode1.setPosition(195, 158);

            PrecedingNodeWithoutSuccessor strategy =
                    new PrecedingNodeWithoutSuccessor(graph, componentNode1DropPoint, forkNode1, graphics);

            // When
            strategy.execute(componentNode1);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(forkNode1)
                    .and().successorsOf(forkNode1).isOnly(componentNode1)
                    .and().node(forkNode1).scopeContainsExactly(componentNode1)
                    .and().successorsOf(componentNode1).isEmpty();
        }

        @Test
        void shouldAddSuccessorOutsideScope() {
            // Given
            // We drop the node right outside the fork node 1 "scope box"
            Point componentNode1DropPoint = new Point(303, 139);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, forkNode1);

            root.setPosition(65, 158);
            forkNode1.setPosition(195, 158);

            PrecedingNodeWithoutSuccessor strategy =
                    new PrecedingNodeWithoutSuccessor(graph, componentNode1DropPoint, forkNode1, graphics);

            // When
            strategy.execute(componentNode1);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(forkNode1)
                    .and().successorsOf(forkNode1).isOnly(componentNode1)
                    .and().node(forkNode1).scopeIsEmpty();
        }
    }
}