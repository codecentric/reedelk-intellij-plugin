package com.esb.plugin.graph.action;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.DefaultNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

class ActionNodeAddTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;

    @Nested
    @DisplayName("Root tests")
    class RootComponent {

        @Test
        void shouldCorrectlyAddRootComponent() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(root);
            root.setPosition(20, 20);

            Point dropPoint = new Point(10, 20); // x drop point smaller than the root x coordinate.

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, n1, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(2)
                    .root().is(n1)
                    .and().successorsOf(n1).isOnly(root);
        }
    }

    @Nested
    @DisplayName("Adding a component after root")
    class ActionNodeAddAfterRoot {

        @Test
        void shouldAddComponentAfterRootAsLast() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(root);
            root.setPosition(20, 20);

            Point dropPoint = new Point(25, 23);  // a little bit after root center x coordinate

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, n1, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(2)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(n1);
        }

        @Test
        void shouldAddComponentBetweenRootAndSuccessor() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();

            graph.root(root);
            root.setPosition(20, 20);

            graph.add(root, n2);
            n2.setPosition(40, 20);

            Point dropPoint = new Point(30, 20); // drop it between root and n2

            // When
            FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, n1, dropPoint);

            // Then
            PluginAssertion.assertThat(modifiableGraph)
                    .isChanged()
                    .nodesCountIs(3)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(n1)
                    .and().successorsOf(n1).isOnly(n2)
                    .and().successorsOf(n2).isEmpty();
        }
    }

    @Nested
    @DisplayName("Scope tests")
    class ScopeTests {

        @Test
        void shouldAddDrawableAtTheEndOfScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, n3);
            choice1.addToScope(n1);

            root.setPosition(50, 100);
            choice1.setPosition(100, 100);
            n1.setPosition(150, 50);
            n3.setPosition(200, 100); // not in choice1 node's scope

            Point dropPoint = new Point(170, 55); // n2 gets moved next to n1

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n2, dropPoint);

            // Then

            // We verify that the choice does not get connected to the successor
            // of the moved element because it is outside the scope.
            // By definition a Choice component cannot connect drawables outside the scope.
            PluginAssertion.assertThat(updatedGraph)
                    .nodesCountIs(5)
                    .successorsOf(choice1).isOnly(n1)
                    .and().successorsOf(n1).isOnly(n2)
                    .and().successorsOf(n2).isOnly(n3)
                    .and().successorsOf(n3).isEmpty()
                    .and().node(choice1).scopeContainsExactly(n1, n2);
        }

        @Test
        void shouldCorrectlyAddLowerNodeToFirstChoiceWithoutConnectingToSecondChoiceLastElement() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);

            choice1.addToScope(n1);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);

            root.setPosition(55, 75);
            choice1.setPosition(165, 75);
            n1.setPosition(275, 75);
            choice2.setPosition(390, 75);
            n2.setPosition(505, 75);

            Point dropPoint = new Point(238, 128);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n3, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).areExactly(choice1)
                    .and().successorsOf(choice1).areExactly(n1, n3)
                    .and().successorsOf(n1).areExactly(choice2)
                    .and().successorsOf(choice2).areExactly(n2)
                    .and().successorsOf(n3).isEmpty()
                    .and().successorsOf(n2).isEmpty()
                    .and().node(choice1).scopeContainsExactly(n1, n3, choice2)
                    .and().node(choice2).scopeContainsExactly(n2);
        }

        @Test
        void shouldCorrectlyAddFollowingLowerNodeToFirstChoiceWithoutConnectingToSecondChoiceLastElement() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(choice1, n3);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(n3, n4);

            choice1.addToScope(n1);
            choice1.addToScope(n3);
            choice1.addToScope(n4);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);

            root.setPosition(55, 140);
            choice1.setPosition(165, 140);
            choice2.setPosition(390, 75);
            n1.setPosition(275, 75);
            n2.setPosition(505, 75);
            n3.setPosition(275, 210);
            n4.setPosition(390, 210);

            Point dropPoint = new Point(479, 196);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n5, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choice1)
                    .and().successorsOf(choice1).areExactly(n1, n3)
                    .and().successorsOf(n3).isOnly(n4)
                    .and().successorsOf(n4).isOnly(n5)
                    .and().successorsOf(n5).isEmpty()
                    .and().successorsOf(n1).isOnly(choice2)
                    .and().successorsOf(choice2).isOnly(n2)
                    .and().successorsOf(n2).isEmpty()
                    .and().node(choice1).scopeContainsExactly(n1, n3, n4, n5, choice2)
                    .and().node(choice2).scopeContainsExactly(n2);
        }


        @Test
        void shouldCorrectlyAddFirstNodeOutsideNestedScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(choice1, n3);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(n3, n4);
            graph.add(n4, n5);
            graph.add(n5, n6);

            choice1.addToScope(n1);
            choice1.addToScope(n3);
            choice1.addToScope(n4);
            choice1.addToScope(n5);
            choice1.addToScope(n6);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);

            root.setPosition(55, 140);
            choice1.setPosition(165, 140);
            n1.setPosition(275, 75);
            n3.setPosition(275, 210);
            choice2.setPosition(390, 75);
            n2.setPosition(505, 75);
            n4.setPosition(390, 210);
            n5.setPosition(505, 210);
            n6.setPosition(625, 210);

            Point dropPoint = new Point(621, 60);


            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n7, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choice1)
                    .and().successorsOf(choice1).areExactly(n1, n3)
                    .and().successorsOf(n1).isOnly(choice2)
                    .and().successorsOf(choice2).isOnly(n2)
                    .and().successorsOf(n2).isOnly(n7)
                    .and().successorsOf(n3).isOnly(n4)
                    .and().successorsOf(n4).isOnly(n5)
                    .and().successorsOf(n5).isOnly(n6)
                    .and().node(choice1).scopeContainsExactly(n1, n3, n4, n5, n6, n7, choice2)
                    .and().node(choice2).scopeContainsExactly(n2);
        }

        @Test
        void shouldAddSecondSuccessorOfNestedChoiceCorrectly() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(choice1, n3);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(n2, n7);
            graph.add(n3, n4);
            graph.add(n4, n5);
            graph.add(n5, n6);

            choice1.addToScope(n1);
            choice1.addToScope(n3);
            choice1.addToScope(n4);
            choice1.addToScope(n5);
            choice1.addToScope(n6);
            choice1.addToScope(n7);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);

            root.setPosition(55, 140);
            choice1.setPosition(165, 140);
            n1.setPosition(275, 75);
            choice2.setPosition(390, 75);
            n2.setPosition(505, 75);
            n3.setPosition(275, 210);
            n4.setPosition(390, 210);
            n5.setPosition(505, 210);
            n6.setPosition(625, 210);
            n7.setPosition(625, 75);

            Point dropPoint = new Point(669, 61);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n8, dropPoint);

            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choice1)
                    .and().successorsOf(choice1).areExactly(n1, n3)
                    .and().successorsOf(n1).isOnly(choice2)
                    .and().successorsOf(choice2).isOnly(n2)
                    .and().successorsOf(n2).isOnly(n7)
                    .and().successorsOf(n7).isOnly(n8)
                    .and().successorsOf(n3).isOnly(n4)
                    .and().successorsOf(n4).isOnly(n5)
                    .and().successorsOf(n5).isOnly(n6)
                    .and().node(choice2).scopeContainsExactly(n2)
                    .and().node(choice1).scopeContainsExactly(n1, n3, n4, n5, n6, n7, n8, choice2);
        }

        @Test
        void shouldAddNodeOutsideScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);

            root.setPosition(55, 70);
            choice1.setPosition(165, 70);
            n1.setPosition(275, 70);
            choice1.addToScope(n1);

            Point dropPoint = new Point(386, 52);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n2, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choice1)
                    .and().successorsOf(choice1).isOnly(n1)
                    .and().successorsOf(n1).isOnly(n2)
                    .and().successorsOf(n2).isEmpty()
                    .and().node(choice1).scopeContainsExactly(n1);
        }

        @Test
        void shouldCorrectlyAddTwoNodesAfterScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, n2);
            choice1.addToScope(n1);

            root.setPosition(55, 70);
            choice1.setPosition(165, 70);
            n1.setPosition(275, 70);
            n2.setPosition(390, 70);

            Point dropPoint = new Point(478, 45);

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n3, dropPoint);

            // Then
            PluginAssertion.assertThat(updatedGraph)
                    .root().is(root)
                    .and().successorsOf(root).isOnly(choice1)
                    .and().successorsOf(choice1).isOnly(n1)
                    .and().successorsOf(n1).isOnly(n2)
                    .and().successorsOf(n2).isOnly(n3)
                    .and().successorsOf(n3).isEmpty()
                    .and().node(choice1).scopeContainsExactly(n1);
        }

        @Nested
        @DisplayName("Preceding node with one successor")
        class PrecedingDrawableWithOneSuccessor {

            @Test
            void shouldAddNodeBetweenPredecessorOutsideScopeAndSuccessorInsideScope() {
                // Given
                FlowGraph graph = new FlowGraphImpl();
                graph.root(root);
                graph.add(root, choice1);
                graph.add(choice1, n1);
                choice1.addToScope(n1);

                root.setPosition(55, 70);
                choice1.setPosition(165, 70);
                n1.setPosition(275, 70);

                Point dropPoint = new Point(103, 56);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, n2, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(n2)
                        .and().successorsOf(n2).isOnly(choice1)
                        .and().successorsOf(choice1).isOnly(n1)
                        .and().successorsOf(n1).isEmpty()
                        .and().node(choice1).scopeContainsExactly(n1);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInScope1AndSuccessorInScope2() {
                // Given
                FlowGraph graph = new FlowGraphImpl();
                graph.root(root);
                graph.add(root, choice1);
                graph.add(choice1, n1);
                graph.add(n1, choice2);
                graph.add(choice2, n2);
                choice1.addToScope(n1);
                choice2.addToScope(n2);

                root.setPosition(55, 75);
                choice1.setPosition(165, 75);
                n1.setPosition(275, 75);
                choice2.setPosition(390, 75);
                n2.setPosition(500, 75);

                Point dropPoint = new Point(333, 60);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, n3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choice1)
                        .and().successorsOf(choice1).isOnly(n1)
                        .and().successorsOf(n1).isOnly(n3)
                        .and().successorsOf(n3).isOnly(choice2)
                        .and().successorsOf(choice2).isOnly(n2)
                        .and().successorsOf(n2).isEmpty()
                        .and().node(choice1).scopeContainsExactly(n1)
                        .and().node(choice2).scopeContainsExactly(n2);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInScopeAndSuccessorOutsideScope() {
                // Given
                FlowGraph graph = new FlowGraphImpl();
                graph.root(root);
                graph.add(root, choice1);
                graph.add(choice1, n1);
                graph.add(n1, n2);

                root.setPosition(55, 70);
                choice1.setPosition(165, 70);
                n1.setPosition(275, 70);
                n2.setPosition(390, 70);

                choice1.addToScope(n1);

                Point dropPoint = new Point(338, 53);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, n3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choice1)
                        .and().successorsOf(choice1).isOnly(n1)
                        .and().successorsOf(n1).isOnly(n3)
                        .and().successorsOf(n3).isOnly(n2)
                        .and().successorsOf(n2).isEmpty()
                        .and().node(choice1).scopeContainsExactly(n1);
            }

            @Test
            void shouldAddNodeBetweenPredecessorAndSuccessorBothOutsideScope() {
                // Given
                FlowGraph graph = new FlowGraphImpl();
                graph.root(root);
                graph.add(root, n1);

                root.setPosition(55, 65);
                n1.setPosition(165, 65);

                Point dropPoint = new Point(106, 45);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, n2, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(n2)
                        .and().successorsOf(n2).isOnly(n1);
            }

            @Test
            void shouldAddNodeBetweenPredecessorInMultipleScopeAndSuccessorOutsideScope() {
                // Given
                FlowGraph graph = new FlowGraphImpl();
                graph.root(root);
                graph.add(root, choice1);
                graph.add(choice1, n1);
                graph.add(n1, choice2);
                graph.add(choice2, n2);
                graph.add(n2, n3);

                choice1.addToScope(n1);
                choice1.addToScope(choice2);
                choice2.addToScope(n2);

                root.setPosition(55, 75);
                choice1.setPosition(165, 75);
                n1.setPosition(275, 75);
                choice2.setPosition(390, 75);
                n2.setPosition(505, 75);
                n3.setPosition(625, 75);

                Point dropPoint = new Point(583, 61);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, n4, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choice1)
                        .and().successorsOf(choice1).isOnly(n1)
                        .and().successorsOf(n1).isOnly(choice2)
                        .and().successorsOf(choice2).isOnly(n2)
                        .and().successorsOf(n2).isOnly(n4)
                        .and().successorsOf(n4).isOnly(n3)
                        .and().node(choice1).scopeContainsExactly(n1, choice2)
                        .and().node(choice2).scopeContainsExactly(n2);
            }
        }

        @Nested
        @DisplayName("Preceding node without successor")
        class PrecedingDrawableWithoutSuccessor {

            @Test
            void shouldAddNodeBetweenPredecessorInMultipleScopeAndDropPointInsideUpperScope() {
                // Given
                FlowGraph graph = new FlowGraphImpl();
                graph.root(root);
                graph.add(root, choice1);
                graph.add(choice1, n1);
                graph.add(n1, choice2);
                graph.add(choice2, n2);

                root.setPosition(55, 75);
                choice1.setPosition(165, 75);
                n1.setPosition(275, 75);
                choice2.setPosition(390, 75);
                n2.setPosition(505, 75);

                choice1.addToScope(n1);
                choice1.addToScope(choice2);
                choice2.addToScope(n2);

                Point dropPoint = new Point(563, 56);

                // When
                FlowGraph updatedGraph = addDrawableToGraph(graph, n3, dropPoint);

                // Then
                PluginAssertion.assertThat(updatedGraph)
                        .root().is(root)
                        .and().successorsOf(root).isOnly(choice1)
                        .and().successorsOf(choice1).isOnly(n1)
                        .and().successorsOf(n1).isOnly(choice2)
                        .and().successorsOf(choice2).isOnly(n2)
                        .and().successorsOf(n2).isOnly(n3)
                        .and().node(choice1).scopeContainsExactly(n1, choice2, n3)
                        .and().node(choice2).scopeContainsExactly(n2);
            }
        }
    }

    private FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        Connector connector = new DefaultNodeConnector(modifiableGraph, dropped);
        ActionNodeAdd action = new ActionNodeAdd(modifiableGraph, dropPoint, connector, graphics);
        action.execute();
        return modifiableGraph;
    }

}
