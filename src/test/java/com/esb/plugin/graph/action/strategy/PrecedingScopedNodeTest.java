package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

class PrecedingScopedNodeTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;


    @DisplayName("Scoped Node without successors")
    @Nested
    class WithoutSuccessors {

        @Test
        void shouldAddSuccessorInsideScope() {
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
        void shouldAddSuccessorOutsideScope() {
            // Given
            // We drop the node right outside the fork node 1 "scope box"
            Point componentNode1DropPoint = new Point(260, 70);

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
                    .and().node(forkNode1).scopeIsEmpty();
        }
    }

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

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode2DropPoint, componentNode2, graphics);

            // When
            strategy.execute(forkNode1);

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

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode2DropPoint, componentNode2, graphics);

            // When
            strategy.execute(forkNode1);

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

    @DisplayName("Scope node with successor inside scope")
    @Nested
    class WithSuccessorInsideScope {

        @Test
        void shouldAddSuccessorOnTopAreaOfExistingSuccessor() {
            // Given
            Point componentNode2DropPoint = new Point(315, 20);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            choiceNode1.addToScope(componentNode1);

            root.setPosition(55, 75);
            choiceNode1.setPosition(195, 75);
            componentNode1.setPosition(335, 75);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode2DropPoint, componentNode2, graphics);

            // When
            strategy.execute(choiceNode1);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).areExactly(componentNode1, componentNode2)
                    // component node 2 should have been added to the top (before) component node 1
                    .and().successorsOf(choiceNode1).isAtIndex(componentNode2, 0)
                    .and().successorsOf(choiceNode1).isAtIndex(componentNode1, 1)
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode2)
                    .and().successorsOf(componentNode1).isEmpty()
                    .and().successorsOf(componentNode2).isEmpty();
        }

        @Test
        void shouldAddSuccessorOnTopAreaOfExistingSuccessorAndConnectFirstNodeOutsideScope() {
            // Given
            Point componentNode3DropPoint = new Point(315, 20);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, componentNode2); // component node 2 is the first node outside scope
            choiceNode1.addToScope(componentNode1);

            root.setPosition(55, 75);
            choiceNode1.setPosition(195, 75);
            componentNode1.setPosition(335, 75);
            componentNode2.setPosition(450, 75);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode3DropPoint, componentNode3, graphics);

            // When
            strategy.execute(choiceNode1);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).areExactly(componentNode1, componentNode3)
                    // component node 3 should have been added to the top (before) component node 1
                    .and().successorsOf(choiceNode1).isAtIndex(componentNode3, 0)
                    .and().successorsOf(choiceNode1).isAtIndex(componentNode1, 1)
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode3).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty();
        }

        @Test
        void shouldAddSuccessorOnCenterAreaOfExistingAndReplaceIt() {
            // Given
            Point componentNode2DropPoint = new Point(210, 70);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            choiceNode1.addToScope(componentNode1);

            root.setPosition(55, 75);
            choiceNode1.setPosition(195, 75);
            componentNode1.setPosition(335, 75);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode2DropPoint, componentNode2, graphics);

            // When
            strategy.execute(choiceNode1);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isEmpty()
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode2);
        }

        @Test
        void shouldAddSuccessorOnBottomAreaOfExistingNode() {
            // Given
            // We drop the node below component node 1
            Point componentNode3DropPoint = new Point(254, 137);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, forkNode1);
            graph.add(forkNode1, componentNode1, 0);
            graph.add(forkNode1, componentNode2, 1);

            forkNode1.addToScope(componentNode1);
            forkNode1.addToScope(componentNode2);

            root.setPosition(55, 145);
            forkNode1.setPosition(165, 145);
            componentNode1.setPosition(275, 75);
            componentNode2.setPosition(275, 215);

            // When
            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode3DropPoint, componentNode3, graphics);

            // Then
            strategy.execute(forkNode1);

            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(forkNode1)
                    .and().successorsOf(forkNode1).areExactly(componentNode1, componentNode2, componentNode3)
                    .and().successorsOf(forkNode1).isAtIndex(componentNode1, 0)
                    .and().successorsOf(forkNode1).isAtIndex(componentNode3, 1)
                    .and().successorsOf(forkNode1).isAtIndex(componentNode2, 2)
                    .and().node(forkNode1).scopeContainsExactly(componentNode1, componentNode2, componentNode3)
                    .and().successorsOf(componentNode1).isEmpty()
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().successorsOf(componentNode3).isEmpty();
        }

        @Test
        void shouldAddSuccessorOnBottomAreaOfExistingNodeAndConnectFirstNodeOutsideScope() {
            // Given
            // We drop the node below component node 1
            Point componentNode4DropPoint = new Point(254, 137);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, forkNode1);
            graph.add(forkNode1, componentNode1, 0);
            graph.add(forkNode1, componentNode2, 1);
            graph.add(componentNode1, componentNode3);
            graph.add(componentNode2, componentNode3);

            forkNode1.addToScope(componentNode1);
            forkNode1.addToScope(componentNode2);

            root.setPosition(55, 145);
            forkNode1.setPosition(165, 145);
            componentNode1.setPosition(275, 75);
            componentNode2.setPosition(275, 215);
            componentNode3.setPosition(390, 145);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode4DropPoint, componentNode4, graphics);

            // Then
            strategy.execute(forkNode1);

            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(forkNode1)
                    .and().successorsOf(forkNode1).areExactly(componentNode1, componentNode2, componentNode4)
                    .and().successorsOf(forkNode1).isAtIndex(componentNode1, 0)
                    .and().successorsOf(forkNode1).isAtIndex(componentNode4, 1)
                    .and().successorsOf(forkNode1).isAtIndex(componentNode2, 2)
                    .and().node(forkNode1).scopeContainsExactly(componentNode1, componentNode2, componentNode4)
                    .and().successorsOf(componentNode1).isOnly(componentNode3)
                    .and().successorsOf(componentNode2).isOnly(componentNode3)
                    .and().successorsOf(componentNode4).isOnly(componentNode3)
                    .and().successorsOf(componentNode3).isEmpty();
        }
    }
}