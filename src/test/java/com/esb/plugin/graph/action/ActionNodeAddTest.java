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

class ActionNodeAddTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;
    @Mock
    private ImageObserver observer;

    private GraphNode rootReplacement;

    @BeforeEach
    public void setUp() {
        super.setUp();
        rootReplacement = createGraphNodeInstance(ComponentRoot.class, GenericComponentNode.class, true);
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
                    .nodesCountIs(2)
                    .root().is(rootReplacement)
                    .and().successorsOf(rootReplacement).isOnly(root);
        }
    }

    @Nested
    @DisplayName("Adding a node after root")
    class ActionNodeAddAfterRoot {

        @Test
        void shouldAddComponentAfterRootAsLast() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            root.setPosition(20, 20);

            Point dropPoint = new Point(25, 23);  // a little bit after root center x coordinate

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
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, componentNode3);
            choiceNode1.addToScope(componentNode1);

            root.setPosition(50, 100);
            choiceNode1.setPosition(100, 100);
            componentNode1.setPosition(150, 50);
            componentNode3.setPosition(200, 100); // not in choiceNode1 node's scope

            Point dropPoint = new Point(170, 55); // componentNode2 gets moved next to componentNode1

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

            // Then

            // We verify that the choice does not get connected to the successor
            // of the moved element because it is outside the scope.
            // By definition a Choice node cannot connect other nodes outside the scope.
            PluginAssertion.assertThat(updatedGraph)
                    .nodesCountIs(5)
                    .successorsOf(choiceNode1).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode3)
                    .and().successorsOf(componentNode3).isEmpty()
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode2);
        }

        @Test
        void shouldCorrectlyAddUpperNodeToFirstChoiceWithoutConnectingToSecondChoiceLastElement() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, choiceNode2);
            graph.add(choiceNode2, componentNode2);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(choiceNode2);
            choiceNode2.addToScope(componentNode2);

            root.setPosition(55, 80);
            choiceNode1.setPosition(195, 80);
            componentNode1.setPosition(335, 80);
            choiceNode2.setPosition(480, 80);
            componentNode2.setPosition(625, 80);

            // We drop the new node on top of the 'otherwise'
            Point dropPoint = new Point(310, 14);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).areExactly(choiceNode1)
                    .and().successorsOf(choiceNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).areExactly(choiceNode2)
                    .and().successorsOf(choiceNode2).areExactly(componentNode2)
                    .and().successorsOf(componentNode3).isEmpty()
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode3, choiceNode2)
                    .and().node(choiceNode2).scopeContainsExactly(componentNode2);
        }

        @Test
        void shouldCorrectlyAddFollowingLowerNodeToFirstChoiceWithoutConnectingToSecondChoiceLastElement() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(choiceNode1, componentNode3);
            graph.add(componentNode1, choiceNode2);
            graph.add(choiceNode2, componentNode2);
            graph.add(componentNode3, componentNode4);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(componentNode3);
            choiceNode1.addToScope(componentNode4);
            choiceNode1.addToScope(choiceNode2);
            choiceNode2.addToScope(componentNode2);

            root.setPosition(55, 140);
            choiceNode1.setPosition(165, 140);
            choiceNode2.setPosition(390, 75);
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
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode3).isOnly(componentNode4)
                    .and().successorsOf(componentNode4).isOnly(componentNode5)
                    .and().successorsOf(componentNode5).isEmpty()
                    .and().successorsOf(componentNode1).isOnly(choiceNode2)
                    .and().successorsOf(choiceNode2).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, choiceNode2)
                    .and().node(choiceNode2).scopeContainsExactly(componentNode2);
        }


        @Test
        void shouldCorrectlyAddFirstNodeOutsideNestedScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(choiceNode1, componentNode3);
            graph.add(componentNode1, choiceNode2);
            graph.add(choiceNode2, componentNode2);
            graph.add(componentNode3, componentNode4);
            graph.add(componentNode4, componentNode5);
            graph.add(componentNode5, componentNode6);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(componentNode3);
            choiceNode1.addToScope(componentNode4);
            choiceNode1.addToScope(componentNode5);
            choiceNode1.addToScope(componentNode6);
            choiceNode1.addToScope(choiceNode2);
            choiceNode2.addToScope(componentNode2);

            root.setPosition(55, 140);
            choiceNode1.setPosition(165, 140);
            componentNode1.setPosition(275, 75);
            componentNode3.setPosition(275, 210);
            choiceNode2.setPosition(390, 75);
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
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).isOnly(choiceNode2)
                    .and().successorsOf(choiceNode2).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode7)
                    .and().successorsOf(componentNode3).isOnly(componentNode4)
                    .and().successorsOf(componentNode4).isOnly(componentNode5)
                    .and().successorsOf(componentNode5).isOnly(componentNode6)
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, componentNode6, componentNode7, choiceNode2)
                    .and().node(choiceNode2).scopeContainsExactly(componentNode2);
        }

        @Test
        void shouldAddSecondSuccessorOfNestedChoiceCorrectly() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(choiceNode1, componentNode3);
            graph.add(componentNode1, choiceNode2);
            graph.add(choiceNode2, componentNode2);
            graph.add(componentNode2, componentNode7);
            graph.add(componentNode3, componentNode4);
            graph.add(componentNode4, componentNode5);
            graph.add(componentNode5, componentNode6);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(componentNode3);
            choiceNode1.addToScope(componentNode4);
            choiceNode1.addToScope(componentNode5);
            choiceNode1.addToScope(componentNode6);
            choiceNode1.addToScope(componentNode7);
            choiceNode1.addToScope(choiceNode2);
            choiceNode2.addToScope(componentNode2);

            root.setPosition(55, 140);
            choiceNode1.setPosition(165, 140);
            componentNode1.setPosition(275, 75);
            choiceNode2.setPosition(390, 75);
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
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).areExactly(componentNode1, componentNode3)
                    .and().successorsOf(componentNode1).isOnly(choiceNode2)
                    .and().successorsOf(choiceNode2).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode7)
                    .and().successorsOf(componentNode7).isOnly(componentNode8)
                    .and().successorsOf(componentNode3).isOnly(componentNode4)
                    .and().successorsOf(componentNode4).isOnly(componentNode5)
                    .and().successorsOf(componentNode5).isOnly(componentNode6)
                    .and().node(choiceNode2).scopeContainsExactly(componentNode2)
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1, componentNode3, componentNode4, componentNode5, componentNode6, componentNode7, componentNode8, choiceNode2);
        }

        @Test
        void shouldAddNodeOutsideScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);

            root.setPosition(55, 70);
            choiceNode1.setPosition(165, 70);
            componentNode1.setPosition(275, 70);
            choiceNode1.addToScope(componentNode1);

            Point dropPoint = new Point(386, 52);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isEmpty()
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1);
        }

        @Test
        void shouldCorrectlyAddTwoNodesAfterScope() {
            // Given
            FlowGraph graph = provider.createGraph();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, componentNode2);
            choiceNode1.addToScope(componentNode1);

            root.setPosition(55, 70);
            choiceNode1.setPosition(165, 70);
            componentNode1.setPosition(275, 70);
            componentNode2.setPosition(390, 70);

            Point dropPoint = new Point(478, 45);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choiceNode1)
                    .and().successorsOf(choiceNode1).isOnly(componentNode1)
                    .and().successorsOf(componentNode1).isOnly(componentNode2)
                    .and().successorsOf(componentNode2).isOnly(componentNode3)
                    .and().successorsOf(componentNode3).isEmpty()
                    .and().node(choiceNode1).scopeContainsExactly(componentNode1);
        }

        @Nested
        @DisplayName("Preceding node with one successor")
        class PrecedingNodeWithOneSuccessor {

            @Test
            void shouldAddNodeBetweenPredecessorOutsideScopeAndSuccessorInsideScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, choiceNode1);
                graph.add(choiceNode1, componentNode1);
                choiceNode1.addToScope(componentNode1);

                root.setPosition(55, 70);
                choiceNode1.setPosition(165, 70);
                componentNode1.setPosition(275, 70);

                Point dropPoint = new Point(103, 56);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode2, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(choiceNode1)
                        .and().successorsOf(choiceNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isEmpty()
                        .and().node(choiceNode1).scopeContainsExactly(componentNode1);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInScope1AndSuccessorInScope2() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, choiceNode1);
                graph.add(choiceNode1, componentNode1);
                graph.add(componentNode1, choiceNode2);
                graph.add(choiceNode2, componentNode2);
                choiceNode1.addToScope(componentNode1);
                choiceNode2.addToScope(componentNode2);

                root.setPosition(55, 75);
                choiceNode1.setPosition(165, 75);
                componentNode1.setPosition(275, 75);
                choiceNode2.setPosition(390, 75);
                componentNode2.setPosition(500, 75);

                Point dropPoint = new Point(333, 60);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choiceNode1)
                        .and().successorsOf(choiceNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(componentNode3)
                        .and().successorsOf(componentNode3).isOnly(choiceNode2)
                        .and().successorsOf(choiceNode2).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty()
                        .and().node(choiceNode1).scopeContainsExactly(componentNode1)
                        .and().node(choiceNode2).scopeContainsExactly(componentNode2);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInScopeAndSuccessorOutsideScope() {
                // Given
                FlowGraph graph = provider.createGraph();
                graph.root(root);
                graph.add(root, choiceNode1);
                graph.add(choiceNode1, componentNode1);
                graph.add(componentNode1, componentNode2);

                root.setPosition(55, 70);
                choiceNode1.setPosition(165, 70);
                componentNode1.setPosition(275, 70);
                componentNode2.setPosition(390, 70);

                choiceNode1.addToScope(componentNode1);

                Point dropPoint = new Point(338, 53);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choiceNode1)
                        .and().successorsOf(choiceNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(componentNode3)
                        .and().successorsOf(componentNode3).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isEmpty()
                        .and().node(choiceNode1).scopeContainsExactly(componentNode1);
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
                graph.add(root, choiceNode1);
                graph.add(choiceNode1, componentNode1);
                graph.add(componentNode1, choiceNode2);
                graph.add(choiceNode2, componentNode2);
                graph.add(componentNode2, componentNode3);

                choiceNode1.addToScope(componentNode1);
                choiceNode1.addToScope(choiceNode2);
                choiceNode2.addToScope(componentNode2);

                root.setPosition(55, 75);
                choiceNode1.setPosition(165, 75);
                componentNode1.setPosition(275, 75);
                choiceNode2.setPosition(390, 75);
                componentNode2.setPosition(505, 75);
                componentNode3.setPosition(625, 75);

                Point dropPoint = new Point(583, 61);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode4, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choiceNode1)
                        .and().successorsOf(choiceNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(choiceNode2)
                        .and().successorsOf(choiceNode2).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(componentNode4)
                        .and().successorsOf(componentNode4).isOnly(componentNode3)
                        .and().node(choiceNode1).scopeContainsExactly(componentNode1, choiceNode2)
                        .and().node(choiceNode2).scopeContainsExactly(componentNode2);
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

                root.setPosition(55, 157);
                forkNode1.setPosition(165, 157);
                forkNode2.setPosition(280, 157);
                componentNode1.setPosition(400, 80);
                forkNode3.setPosition(400, 227);
                componentNode2.setPosition(525, 157);

                // When we drop the node at the end of fork 1 node scope
                Point dropPoint = new Point(463, 221);
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

                root.setPosition(55, 157);
                forkNode1.setPosition(165, 157);
                forkNode2.setPosition(280, 157);
                componentNode1.setPosition(400, 80);
                forkNode3.setPosition(400, 227);
                componentNode2.setPosition(525, 157);


                // When we drop the node at the end of fork 2 node scope
                Point dropPoint = new Point(458, 227);
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

                root.setPosition(55, 157);
                forkNode1.setPosition(165, 157);
                forkNode2.setPosition(280, 157);
                componentNode1.setPosition(400, 80);
                forkNode3.setPosition(400, 227);
                componentNode2.setPosition(525, 157);


                // When we drop the node at the end of fork 3 node scope
                Point dropPoint = new Point(449, 208);
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

                root.setPosition(55, 157);
                forkNode1.setPosition(165, 157);
                forkNode2.setPosition(280, 157);
                componentNode1.setPosition(400, 80);
                forkNode3.setPosition(400, 227);
                componentNode2.setPosition(525, 157);


                // When we drop the node outside any scope
                Point dropPoint = new Point(474, 234);
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
                graph.add(root, choiceNode1);
                graph.add(choiceNode1, componentNode1);
                graph.add(componentNode1, choiceNode2);
                graph.add(choiceNode2, componentNode2);

                root.setPosition(55, 75);
                choiceNode1.setPosition(165, 75);
                componentNode1.setPosition(275, 75);
                choiceNode2.setPosition(390, 75);
                componentNode2.setPosition(505, 75);

                choiceNode1.addToScope(componentNode1);
                choiceNode1.addToScope(choiceNode2);
                choiceNode2.addToScope(componentNode2);

                Point dropPoint = new Point(563, 56);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, componentNode3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choiceNode1)
                        .and().successorsOf(choiceNode1).isOnly(componentNode1)
                        .and().successorsOf(componentNode1).isOnly(choiceNode2)
                        .and().successorsOf(choiceNode2).isOnly(componentNode2)
                        .and().successorsOf(componentNode2).isOnly(componentNode3)
                        .and().node(choiceNode1).scopeContainsExactly(componentNode1, choiceNode2, componentNode3)
                        .and().node(choiceNode2).scopeContainsExactly(componentNode2);
            }
        }
    }

    private FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        ActionNodeAdd action = new ActionNodeAdd(modifiableGraph, dropPoint, dropped, graphics, observer);
        action.execute();
        return modifiableGraph;
    }
}
