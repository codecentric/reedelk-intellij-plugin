package com.esb.plugin.graph.action;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.component.type.generic.GenericComponentNode;
import com.esb.plugin.fixture.ComponentRoot;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;
import java.awt.image.ImageObserver;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;

class FlowActionNodeAddTest extends AbstractGraphTest {

    @Mock
    private ImageObserver observer;

    private GraphNode rootReplacement;

    @BeforeEach
    public void setUp() {
        super.setUp();
        rootReplacement = createGraphNodeInstance(ComponentRoot.class, GenericComponentNode.class, INBOUND);
    }

    @Nested
    @DisplayName("Root tests")
    class RootComponent {

        @Test
        void shouldCorrectlyAddRootComponent() {
            // Given
            FlowGraph graph = provider.createGraph();
            Point dropPoint = new Point(20, 20);

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, root, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(1)
                    .root().is(root)
                    .and().successorsOf(root).isEmpty();
        }

        @Test
        void shouldCorrectlyReplaceRootComponentWhenXCoordinateIsSmallerThanCurrentRoot() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            root.setPosition(20, 20);

            Point dropPoint = new Point(10, 20); // x drop point smaller than the root x coordinate.

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, rootReplacement, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(1)
                    .root().is(rootReplacement)
                    .and().successorsOf(rootReplacement).isEmpty();
        }

        @Test
        void shouldReplaceRootWhenDroppedComponentOverlapsRootNode() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, componentNode1);

            root.setPosition(65, 150);
            componentNode1.setPosition(195, 150);

            // Drop the root component on top of the existing root
            Point dropPoint = new Point(56, 129);

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, rootReplacement, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(2)
                    .root().is(rootReplacement)
                    .and().successorsOf(rootReplacement).isOnly(componentNode1);
        }
    }

    @Nested
    @DisplayName("Adding a node after root")
    class FlowActionNodeAddAfterRoot {

        @Test
        void shouldAddComponentAfterRoot() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            root.setPosition(65, 150);

            // Drop the component next  to the root
            Point dropPoint = new Point(195, 150);

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, componentNode1, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(2)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(componentNode1);
        }

        @Test
        void shouldAddComponentBetweenRootAndSuccessor() {
            // Given
            FlowGraph graph = provider.createGraph();

            graph.root(root);
            root.setPosition(20, 20);

            graph.add(root, componentNode2);
            componentNode2.setPosition(40, 20);

            Point dropPoint = new Point(30, 20); // drop it between root and componentNode2

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, componentNode1, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(3)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty();
        }
    }

    @Nested
    @DisplayName("Scope tests")
    class ScopeTests {

        @Test
        void shouldAddDrawableAtTheEndOfScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(componentNode1, componentNode3);
            routerNode1.addToScope(componentNode1);

            root.setPosition(50, 100);
            routerNode1.setPosition(100, 100);
            componentNode1.setPosition(150, 50);
            componentNode3.setPosition(200, 100); // not in routerNode1 node's scope

            Point dropPoint = new Point(170, 55); // componentNode2 gets moved next to componentNode1

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

            // Then

            // We verify that the router does not get connected to the successor
            // of the moved element because it is outside the scope.
            // By definition a Router node cannot connect other nodes outside the scope.
            PluginAssertion.assertThat(updatedGraph)
                    .nodesCountIs(5)
                    .successorsOf(routerNode1).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode3)
                    .and().successorsOf(componentNode3).isEmpty()
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode2);
        }

        @Test
        void shouldCorrectlyAddUpperNodeToFirstRouterWithoutConnectingToSecondRouterLastElement() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(componentNode1, routerNode2);
            graph.add(routerNode2, componentNode2);

            routerNode1.addToScope(componentNode1);
            routerNode1.addToScope(routerNode2);
            routerNode2.addToScope(componentNode2);

            root.setPosition(55, 80);
            routerNode1.setPosition(195, 80);
            componentNode1.setPosition(335, 80);
            routerNode2.setPosition(480, 80);
            componentNode2.setPosition(625, 80);

            // We drop the new node on top of the 'otherwise'
            Point dropPoint = new Point(310, 14);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).areExactly(routerNode1)
                    .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).areExactly(routerNode2)
                    .and().successorsOf(routerNode2).areExactly(componentNode2)
                    .and().successorsOf(componentNode3).isEmpty()
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, routerNode2)
                    .and().node(routerNode2).scopeContainsExactly(componentNode2);
        }

        @Test
        void shouldCorrectlyAddFollowingLowerNodeToFirstRouterWithoutConnectingToSecondRouterLastElement() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(routerNode1, componentNode3);
            graph.add(componentNode1, routerNode2);
            graph.add(routerNode2, componentNode2);
            graph.add(componentNode3, componentNode4);

            routerNode1.addToScope(componentNode1);
            routerNode1.addToScope(componentNode3);
            routerNode1.addToScope(componentNode4);
            routerNode1.addToScope(routerNode2);
            routerNode2.addToScope(componentNode2);

            root.setPosition(55, 140);
            routerNode1.setPosition(165, 140);
            routerNode2.setPosition(390, 75);
            componentNode1.setPosition(275, 75);
            componentNode2.setPosition(505, 75);
            componentNode3.setPosition(275, 210);
            componentNode4.setPosition(390, 210);

            Point dropPoint = new Point(479, 196);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode5, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode3).isOnly(componentNode4)
                    .and().successorsOf(componentNode4).isOnly(componentNode5)
                    .and().successorsOf(componentNode5).isEmpty()
                    .and().successorsOf(componentNode1).isOnly(routerNode2)
                    .and().successorsOf(routerNode2).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, routerNode2)
                    .and().node(routerNode2).scopeContainsExactly(componentNode2);
        }


        @Test
        void shouldCorrectlyAddFirstNodeOutsideNestedScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(routerNode1, componentNode3);
            graph.add(componentNode1, routerNode2);
            graph.add(routerNode2, componentNode2);
            graph.add(componentNode3, componentNode4);
            graph.add(componentNode4, componentNode5);
            graph.add(componentNode5, componentNode6);

            routerNode1.addToScope(componentNode1);
            routerNode1.addToScope(componentNode3);
            routerNode1.addToScope(componentNode4);
            routerNode1.addToScope(componentNode5);
            routerNode1.addToScope(componentNode6);
            routerNode1.addToScope(routerNode2);
            routerNode2.addToScope(componentNode2);

            root.setPosition(55, 140);
            routerNode1.setPosition(165, 140);
            componentNode1.setPosition(275, 75);
            componentNode3.setPosition(275, 210);
            routerNode2.setPosition(390, 75);
            componentNode2.setPosition(505, 75);
            componentNode4.setPosition(390, 210);
            componentNode5.setPosition(505, 210);
            componentNode6.setPosition(625, 210);

            Point dropPoint = new Point(621, 60);


            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode7, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).isOnly(routerNode2)
                    .and().successorsOf(routerNode2).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode7)
                    .and().successorsOf(componentNode3).isOnly(componentNode4)
                    .and().successorsOf(componentNode4).isOnly(componentNode5)
                    .and().successorsOf(componentNode5).isOnly(componentNode6)
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, componentNode6, componentNode7, routerNode2)
                    .and().node(routerNode2).scopeContainsExactly(componentNode2);
        }

        @Test
        void shouldAddSecondSuccessorOfNestedRouterCorrectly() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(routerNode1, componentNode3);
            graph.add(componentNode1, routerNode2);
            graph.add(routerNode2, componentNode2);
            graph.add(componentNode2, componentNode7);
            graph.add(componentNode3, componentNode4);
            graph.add(componentNode4, componentNode5);
            graph.add(componentNode5, componentNode6);

            routerNode1.addToScope(componentNode1);
            routerNode1.addToScope(componentNode3);
            routerNode1.addToScope(componentNode4);
            routerNode1.addToScope(componentNode5);
            routerNode1.addToScope(componentNode6);
            routerNode1.addToScope(componentNode7);
            routerNode1.addToScope(routerNode2);
            routerNode2.addToScope(componentNode2);

            root.setPosition(55, 140);
            routerNode1.setPosition(165, 140);
            componentNode1.setPosition(275, 75);
            routerNode2.setPosition(390, 75);
            componentNode2.setPosition(505, 75);
            componentNode3.setPosition(275, 210);
            componentNode4.setPosition(390, 210);
            componentNode5.setPosition(505, 210);
            componentNode6.setPosition(625, 210);
            componentNode7.setPosition(625, 75);

            Point dropPoint = new Point(669, 61);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode8, dropPoint);

            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).isOnly(routerNode2)
                    .and().successorsOf(routerNode2).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode7)
                    .and().successorsOf(componentNode7).isOnly(componentNode8)
                    .and().successorsOf(componentNode3).isOnly(componentNode4)
                    .and().successorsOf(componentNode4).isOnly(componentNode5)
                    .and().successorsOf(componentNode5).isOnly(componentNode6)
                    .and().node(routerNode2).scopeContainsExactly(componentNode2)
                    .and().node(routerNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, componentNode6, componentNode7, componentNode8, routerNode2);
        }

        @Test
        void shouldAddNodeOutsideScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);

            root.setPosition(55, 70);
            routerNode1.setPosition(165, 70);
            componentNode1.setPosition(275, 70);
            routerNode1.addToScope(componentNode1);

            Point dropPoint = new Point(386, 52);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().node(routerNode1).scopeContainsExactly(componentNode1);
        }

        @Test
        void shouldCorrectlyAddTwoNodesAfterScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, routerNode1);
            graph.add(routerNode1, componentNode1);
            graph.add(componentNode1, componentNode2);
            routerNode1.addToScope(componentNode1);

            root.setPosition(55, 70);
            routerNode1.setPosition(165, 70);
            componentNode1.setPosition(275, 70);
            componentNode2.setPosition(390, 70);

            Point dropPoint = new Point(478, 45);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(routerNode1)
                    .and().successorsOf(routerNode1).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode3)
                    .and().successorsOf(componentNode3).isEmpty()
                    .and().node(routerNode1).scopeContainsExactly(componentNode1);
        }

        @Nested
        @DisplayName("Preceding node with one successor")
        class PrecedingNodeWithOneSuccessor {

            @Test
            void shouldAddNodeBetweenPredecessorOutsideScopeAndSuccessorInsideScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, routerNode1);
                graph.add(routerNode1, componentNode1);
                routerNode1.addToScope(componentNode1);

                root.setPosition(55, 70);
                routerNode1.setPosition(165, 70);
                componentNode1.setPosition(275, 70);

                Point dropPoint = new Point(103, 56);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(routerNode1)
                        .and().successorsOf(routerNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isEmpty()
                        .and().node(routerNode1).scopeContainsExactly(componentNode1);
            }

            @Test
            void shouldAddNodeBetweenSuccessorInScope1AndBeforeScope2() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, routerNode1);
                graph.add(routerNode1, componentNode1);
                graph.add(componentNode1, routerNode2);
                graph.add(routerNode2, componentNode2);
                routerNode1.addToScope(componentNode1);
                routerNode2.addToScope(componentNode2);

                root.setPosition(65, 155);
                routerNode1.setPosition(215, 155);
                componentNode1.setPosition(370, 155);
                routerNode2.setPosition(525, 155);
                componentNode2.setPosition(680, 155);

                Point dropPoint = new Point(437, 147);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(routerNode1)
                        .and().successorsOf(routerNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(componentNode3)
                        .and().successorsOf(componentNode3).isOnly(routerNode2)
                        .and().successorsOf(routerNode2).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty()
                        .and().node(routerNode1).scopeContainsExactly(componentNode1)
                        .and().node(routerNode2).scopeContainsExactly(componentNode2);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInScopeAndSuccessorOutsideScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, routerNode1);
                graph.add(routerNode1, componentNode1);
                graph.add(componentNode1, componentNode2);

                root.setPosition(65, 155);
                routerNode1.setPosition(215, 155);
                componentNode1.setPosition(370, 155);
                componentNode2.setPosition(505, 155);

                routerNode1.addToScope(componentNode1);

                Point dropPoint = new Point(454, 132);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(routerNode1)
                        .and().successorsOf(routerNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(componentNode3)
                        .and().successorsOf(componentNode3).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty()
                        .and().node(routerNode1).scopeContainsExactly(componentNode1);
            }

            @Test
            void shouldAddNodeBetweenPredecessorAndSuccessorBothOutsideScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, componentNode1);

                root.setPosition(55, 65);
                componentNode1.setPosition(165, 65);

                Point dropPoint = new Point(106, 45);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(componentNode1);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInMultipleScopeAndSuccessorOutsideScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, routerNode1);
                graph.add(routerNode1, componentNode1);
                graph.add(componentNode1, routerNode2);
                graph.add(routerNode2, componentNode2);
                graph.add(componentNode2, componentNode3);

                routerNode1.addToScope(componentNode1);
                routerNode1.addToScope(routerNode2);
                routerNode2.addToScope(componentNode2);

                root.setPosition(55, 75);
                routerNode1.setPosition(165, 75);
                componentNode1.setPosition(275, 75);
                routerNode2.setPosition(390, 75);
                componentNode2.setPosition(505, 75);
                componentNode3.setPosition(625, 75);

                Point dropPoint = new Point(583, 61);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode4, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(routerNode1)
                        .and().successorsOf(routerNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(routerNode2)
                        .and().successorsOf(routerNode2).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(componentNode4)
                        .and().successorsOf(componentNode4).isOnly(componentNode3)
                        .and().node(routerNode1).scopeContainsExactly(componentNode1, routerNode2)
                        .and().node(routerNode2).scopeContainsExactly(componentNode2);
            }

            @Test
            void shouldAddNodeAfterNestedForkScopedNodeInsideForkNodeScope1() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, forkNode1);
                graph.add(forkNode1, forkNode2);
                graph.add(forkNode2, componentNode1);
                graph.add(forkNode2, forkNode3);
                graph.add(componentNode1, componentNode2);
                graph.add(forkNode3, componentNode2);

                forkNode1.addToScope(forkNode2);
                forkNode2.addToScope(componentNode1);
                forkNode2.addToScope(forkNode3);

                root.setPosition(65, 223);
                forkNode1.setPosition(195, 223);
                forkNode2.setPosition(330, 223);
                componentNode1.setPosition(470, 160);
                forkNode3.setPosition(470, 278);
                componentNode2.setPosition(615, 223);

                // When we drop the node between fork 2 and fork 1 node scope
                Point dropPoint = new Point(544, 191);
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(forkNode1)
                        .and().successorsOf(forkNode1).isOnly(forkNode2)
                        .and().node(forkNode1).scopeContainsExactly(forkNode2, componentNode3)
                        .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                        .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3)
                        .and().successorsOf(forkNode3).isOnly(componentNode3)
                        .and().successorsOf(componentNode1).isOnly(componentNode3)
                        .and().node(forkNode3).scopeIsEmpty()
                        .and().successorsOf(componentNode3).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty();
            }

            @Test
            void shouldAddNodeAfterNestedForkScopedNodeInsideForkNodeScope2() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, forkNode1);
                graph.add(forkNode1, forkNode2);
                graph.add(forkNode2, componentNode1);
                graph.add(forkNode2, forkNode3);
                graph.add(componentNode1, componentNode2);
                graph.add(forkNode3, componentNode2);

                forkNode1.addToScope(forkNode2);
                forkNode2.addToScope(componentNode1);
                forkNode2.addToScope(forkNode3);

                root.setPosition(65, 223);
                forkNode1.setPosition(195, 223);
                forkNode2.setPosition(330, 223);
                componentNode1.setPosition(470, 160);
                forkNode3.setPosition(470, 278);
                componentNode2.setPosition(615, 223);


                // When we drop the node at the end of fork 2 node scope
                Point dropPoint = new Point(539, 260);
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(forkNode1)
                        .and().successorsOf(forkNode1).isOnly(forkNode2)
                        .and().node(forkNode1).scopeContainsExactly(forkNode2)
                        .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                        .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3, componentNode3)
                        .and().successorsOf(forkNode3).isOnly(componentNode3)
                        .and().successorsOf(componentNode1).isOnly(componentNode2)
                        .and().node(forkNode3).scopeIsEmpty()
                        .and().successorsOf(componentNode3).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty();
            }

            @Test
            void shouldAddNodeAfterNestedForkScopedNodeInsideForkNodeScope3() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, forkNode1);
                graph.add(forkNode1, forkNode2);
                graph.add(forkNode2, componentNode1);
                graph.add(forkNode2, forkNode3);
                graph.add(componentNode1, componentNode2);
                graph.add(forkNode3, componentNode2);

                forkNode1.addToScope(forkNode2);
                forkNode2.addToScope(componentNode1);
                forkNode2.addToScope(forkNode3);

                root.setPosition(65, 223);
                forkNode1.setPosition(195, 223);
                forkNode2.setPosition(330, 223);
                componentNode1.setPosition(470, 160);
                forkNode3.setPosition(470, 278);
                componentNode2.setPosition(615, 223);

                // When we drop the node at the end of fork 3 node scope
                Point dropPoint = new Point(529, 282);
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(forkNode1)
                        .and().successorsOf(forkNode1).isOnly(forkNode2)
                        .and().node(forkNode1).scopeContainsExactly(forkNode2)
                        .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                        .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3)
                        .and().successorsOf(forkNode3).isOnly(componentNode3)
                        .and().successorsOf(componentNode1).isOnly(componentNode2)
                        .and().node(forkNode3).scopeContainsExactly(componentNode3)
                        .and().successorsOf(componentNode3).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty();
            }

            @Test
            void shouldAddNodeAfterNestedForkScopedNodeWhenDroppedOutsideAnyScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, forkNode1);
                graph.add(forkNode1, forkNode2);
                graph.add(forkNode2, componentNode1);
                graph.add(forkNode2, forkNode3);
                graph.add(componentNode1, componentNode2);
                graph.add(forkNode3, componentNode2);

                forkNode1.addToScope(forkNode2);
                forkNode2.addToScope(componentNode1);
                forkNode2.addToScope(forkNode3);

                root.setPosition(65, 223);
                forkNode1.setPosition(195, 223);
                forkNode2.setPosition(330, 223);
                componentNode1.setPosition(470, 160);
                forkNode3.setPosition(470, 278);
                componentNode2.setPosition(615, 223);

                // When we drop the node outside any scope
                Point dropPoint = new Point(566, 185);
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(forkNode1)
                        .and().successorsOf(forkNode1).isOnly(forkNode2)
                        .and().node(forkNode1).scopeContainsExactly(forkNode2)
                        .and().successorsOf(forkNode2).areExactly(componentNode1, forkNode3)
                        .and().node(forkNode2).scopeContainsExactly(componentNode1, forkNode3)
                        .and().successorsOf(forkNode3).isOnly(componentNode3)
                        .and().successorsOf(componentNode1).isOnly(componentNode3)
                        .and().node(forkNode3).scopeIsEmpty()
                        .and().successorsOf(componentNode3).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty();
            }
        }

        @Nested
        @DisplayName("Preceding node without successor")
        class PrecedingNodeWithoutSuccessor {

            @Test
            void shouldAddNodeBetweenPredecessorInMultipleScopeAndDropPointInsideUpperScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, routerNode1);
                graph.add(routerNode1, componentNode1);
                graph.add(componentNode1, routerNode2);
                graph.add(routerNode2, componentNode2);

                root.setPosition(65, 160);
                routerNode1.setPosition(215, 160);
                componentNode1.setPosition(370, 160);
                routerNode2.setPosition(525, 160);
                componentNode2.setPosition(685, 160);

                routerNode1.addToScope(componentNode1);
                routerNode1.addToScope(routerNode2);
                routerNode2.addToScope(componentNode2);

                Point dropPoint = new Point(753, 154);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(routerNode1)
                        .and().successorsOf(routerNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(routerNode2)
                        .and().successorsOf(routerNode2).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(componentNode3)
                        .and().node(routerNode1).scopeContainsExactly(componentNode1, routerNode2, componentNode3)
                        .and().node(routerNode2).scopeContainsExactly(componentNode2);
            }
        }
    }

    private FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        FlowActionNodeAdd action = new FlowActionNodeAdd(dropPoint, dropped, graphics, observer);
        action.execute(modifiableGraph);
        return modifiableGraph;
    }
}
