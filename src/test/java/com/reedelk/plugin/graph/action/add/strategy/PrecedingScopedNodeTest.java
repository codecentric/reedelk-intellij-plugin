package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.testutils.AddRouterConditions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;

class PrecedingScopedNodeTest extends AbstractGraphTest {

    @DisplayName("Scope node with successor inside scope")
    @Nested
    class WithSuccessorInsideScope {

        @Test
        void shouldAddSuccessorOnTopAreaOfExistingSuccessor() {
            // Given
            Point componentNode2DropPoint = new Point(315, 20);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            routerNode1.addToScope(componentNode1);

            root.setPosition(55, 75);
            routerNode1.setPosition(195, 75);
            componentNode1.setPosition(335, 75);

            AddRouterConditions.addConditionRoutePairs(routerNode1, "1 != 2", componentNode1, "otherwise", componentNode1);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode2DropPoint, routerNode1, graphics, placeholderProvider);

            // When
            strategy.execute(componentNode2);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode2)
                    // component node 2 should have been added to the top (before) component node 1
                    .and().successorsOf(routerNode1).isAtIndex(componentNode2, 0)
                    .and().successorsOf(routerNode1).isAtIndex(componentNode1, 1)
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode2)
                    .and().successorsOf(componentNode1).isEmpty()
                    .and().successorsOf(componentNode2).isEmpty();
        }

        @Test
        void shouldAddSuccessorOnTopAreaOfExistingSuccessorAndConnectFirstNodeOutsideScope() {
            // Given
            Point componentNode3DropPoint = new Point(315, 20);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(componentNode1, componentNode2); // component node 2 is the first node outside scope
            routerNode1.addToScope(componentNode1);

            root.setPosition(55, 75);
            routerNode1.setPosition(195, 75);
            componentNode1.setPosition(335, 75);
            componentNode2.setPosition(450, 75);

            AddRouterConditions.addConditionRoutePairs(routerNode1, "otherwise", componentNode1);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode3DropPoint, routerNode1, graphics, placeholderProvider);

            // When
            strategy.execute(componentNode3);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                    // component node 3 should have been added to the top (before) component node 1
                    .and().successorsOf(routerNode1).isAtIndex(componentNode3, 0)
                    .and().successorsOf(routerNode1).isAtIndex(componentNode1, 1)
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode3).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty();
        }

        @Test
        void shouldAddSuccessorOnCenterAreaOfExistingAndReplaceIt() {
            // Given
            Point componentNode2DropPoint = new Point(210, 145);

            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            routerNode1.addToScope(componentNode1);

            root.setPosition(65, 155);
            routerNode1.setPosition(215, 155);
            componentNode1.setPosition(365, 155);

            AddRouterConditions.addConditionRoutePairs(routerNode1, "otherwise", componentNode1);

            PrecedingScopedNode strategy =
                    new PrecedingScopedNode(graph, componentNode2DropPoint, routerNode1, graphics, placeholderProvider);

            // When
            strategy.execute(componentNode2);

            // Then
            PluginAssertion.assertThat(graph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isEmpty()
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode2);
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
                    new PrecedingScopedNode(graph, componentNode3DropPoint, forkNode1, graphics, placeholderProvider);

            // Then
            strategy.execute(componentNode3);

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
                    new PrecedingScopedNode(graph, componentNode4DropPoint, forkNode1, graphics, placeholderProvider);

            // Then
            strategy.execute(componentNode4);

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