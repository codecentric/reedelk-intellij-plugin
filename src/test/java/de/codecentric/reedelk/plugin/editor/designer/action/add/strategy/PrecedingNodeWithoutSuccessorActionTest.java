package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

class PrecedingNodeWithoutSuccessorActionTest extends AbstractGraphTest {

    @DisplayName("Scoped Node without successors")
    @Nested
    class WithPrecedingScopedNodeAction {
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

            PrecedingNodeWithoutSuccessorAction strategy =
                    new PrecedingNodeWithoutSuccessorAction(graph, componentNode1DropPoint, forkNode1, graphics, placeholderProvider);

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

            PrecedingNodeWithoutSuccessorAction strategy =
                    new PrecedingNodeWithoutSuccessorAction(graph, componentNode1DropPoint, forkNode1, graphics, placeholderProvider);

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
