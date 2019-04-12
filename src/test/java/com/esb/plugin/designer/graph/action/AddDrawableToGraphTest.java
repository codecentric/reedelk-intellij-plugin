package com.esb.plugin.designer.graph.action;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AddDrawableToGraphTest extends AbstractGraphTest {

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
            assertIsChangedWithNodesCount(modifiableGraph, 1);
            assertThat(graph.root()).isEqualTo(root);
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
            assertIsChangedWithNodesCount(modifiableGraph, 2);

            Drawable newRoot = graph.root();
            assertThat(newRoot).isEqualTo(n1);
            // Old root has been replaced by n1, therefore successor of n1 is root.
            assertThat(graph.successors(newRoot)).containsExactly(root);
        }
    }

    @Nested
    @DisplayName("Adding a component after root")
    class AddDrawableToGraphAfterRoot {

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
            assertIsChangedWithNodesCount(modifiableGraph, 2);

            Drawable root = graph.root();
            assertThat(root).isEqualTo(root);

            java.util.List<Drawable> successorOfRoot = graph.successors(root);
            assertThat(successorOfRoot).containsExactly(n1);
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
            assertIsChangedWithNodesCount(modifiableGraph, 3);

            Drawable root = graph.root();
            java.util.List<Drawable> successorOfRoot = graph.successors(root);
            assertThat(successorOfRoot).containsExactly(n1);

            List<Drawable> successorsOfN1 = graph.successors(n1);
            assertThat(successorsOfN1).containsExactly(n2);
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
            n3.setPosition(200, 100); // not in choice1 drawable's scope

            Point dropPoint = new Point(170, 55); // n2 gets moved next to n1

            // When
            FlowGraph updatedGraph = addDrawableToGraph(graph, n2, dropPoint);

            // Then
            assertNodeCountIs(updatedGraph, 5);

            // We verify that the choice does not get connected to the successor
            // of the moved element because it is outside the scope.
            // By definition a Choice component cannot connect drawables outside the scope.
            assertThat(updatedGraph.successors(choice1)).containsExactly(n1);
            assertThat(updatedGraph.successors(n1)).containsExactly(n2);
            assertThat(updatedGraph.successors(n2)).containsExactly(n3);

            assertThat(choice1.getScope()).containsExactlyInAnyOrder(n1, n2);
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
            assertThatRootIs(updatedGraph, root);
            assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
            assertThatSuccessorsAreExactly(updatedGraph, choice1, n1, n3);
            assertThatSuccessorsAreExactly(updatedGraph, n1, choice2);
            assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);
            assertThatSuccessorsAreExactly(updatedGraph, n3);
            assertThatSuccessorsAreExactly(updatedGraph, n2);

            assertThat(choice1.getScope()).containsExactlyInAnyOrder(n1, n3, choice2);
            assertThat(choice2.getScope()).containsExactly(n2);
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
            assertThatRootIs(updatedGraph, root);
            assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
            assertThatSuccessorsAreExactly(updatedGraph, choice1, n1, n3);
            assertThatSuccessorsAreExactly(updatedGraph, n3, n4);
            assertThatSuccessorsAreExactly(updatedGraph, n4, n5);
            assertThatSuccessorsAreExactly(updatedGraph, n5);
            assertThatSuccessorsAreExactly(updatedGraph, n1, choice2);
            assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);

            assertThat(choice1.getScope()).containsExactlyInAnyOrder(n1, n3, n4, n5, choice2);
            assertThat(choice2.getScope()).containsExactly(n2);
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
            assertThatRootIs(updatedGraph, root);
            assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
            assertThatSuccessorsAreExactly(updatedGraph, choice1, n1, n3);
            assertThatSuccessorsAreExactly(updatedGraph, n1, choice2);
            assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);
            assertThatSuccessorsAreExactly(updatedGraph, n2, n7);
            assertThatSuccessorsAreExactly(updatedGraph, n3, n4);
            assertThatSuccessorsAreExactly(updatedGraph, n4, n5);
            assertThatSuccessorsAreExactly(updatedGraph, n5, n6);

            assertThat(choice2.getScope()).containsExactly(n2);
            assertThat(choice1.getScope()).containsExactlyInAnyOrder(n1, n3, n4, n5, n6, n7, choice2);
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

            assertThatRootIs(updatedGraph, root);
            assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
            assertThatSuccessorsAreExactly(updatedGraph, choice1, n1, n3);
            assertThatSuccessorsAreExactly(updatedGraph, n1, choice2);
            assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);
            assertThatSuccessorsAreExactly(updatedGraph, n2, n7);
            assertThatSuccessorsAreExactly(updatedGraph, n7, n8);
            assertThatSuccessorsAreExactly(updatedGraph, n3, n4);
            assertThatSuccessorsAreExactly(updatedGraph, n4, n5);
            assertThatSuccessorsAreExactly(updatedGraph, n5, n6);

            assertThat(choice2.getScope()).containsExactly(n2);
            assertThat(choice1.getScope()).containsExactlyInAnyOrder(n1, n3, n4, n5, n6, n7, n8, choice2);
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
            assertThatRootIs(updatedGraph, root);
            assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
            assertThatSuccessorsAreExactly(updatedGraph, choice1, n1);
            assertThatSuccessorsAreExactly(updatedGraph, n1, n2);
            assertThatSuccessorsAreExactly(updatedGraph, n2);

            assertThat(choice1.getScope()).containsExactly(n1);
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
            assertThatRootIs(updatedGraph, root);
            assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
            assertThatSuccessorsAreExactly(updatedGraph, choice1, n1);
            assertThatSuccessorsAreExactly(updatedGraph, n1, n2);
            assertThatSuccessorsAreExactly(updatedGraph, n2, n3);

            assertThat(choice1.getScope()).containsExactly(n1);
        }

        @Nested
        @DisplayName("Preceding drawable with one successor")
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
                assertThatRootIs(updatedGraph, root);
                assertThatSuccessorsAreExactly(updatedGraph, root, n2);
                assertThatSuccessorsAreExactly(updatedGraph, n2, choice1);
                assertThatSuccessorsAreExactly(updatedGraph, choice1, n1);
                assertThat(choice1.getScope()).containsExactly(n1);
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
                assertThatRootIs(updatedGraph, root);
                assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
                assertThatSuccessorsAreExactly(updatedGraph, choice1, n1);
                assertThatSuccessorsAreExactly(updatedGraph, n1, n3);
                assertThatSuccessorsAreExactly(updatedGraph, n3, choice2);
                assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);
                assertThat(choice1.getScope()).containsExactly(n1);
                assertThat(choice2.getScope()).containsExactly(n2);
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
                assertThatRootIs(updatedGraph, root);
                assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
                assertThatSuccessorsAreExactly(updatedGraph, choice1, n1);
                assertThatSuccessorsAreExactly(updatedGraph, n1, n3);
                assertThatSuccessorsAreExactly(updatedGraph, n3, n2);
                assertThat(choice1.getScope()).containsExactly(n1);
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
                assertThatRootIs(updatedGraph, root);
                assertThatSuccessorsAreExactly(updatedGraph, root, n2);
                assertThatSuccessorsAreExactly(updatedGraph, n2, n1);
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
                assertThatRootIs(updatedGraph, root);
                assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
                assertThatSuccessorsAreExactly(updatedGraph, choice1, n1);
                assertThatSuccessorsAreExactly(updatedGraph, n1, choice2);
                assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);
                assertThatSuccessorsAreExactly(updatedGraph, n2, n4);
                assertThatSuccessorsAreExactly(updatedGraph, n4, n3);
                assertThat(choice1.getScope()).containsExactly(n1, choice2);
                assertThat(choice2.getScope()).containsExactly(n2);
            }


        }

        @Nested
        @DisplayName("Preceding drawable without successor")
        class PrecedingDrawableWithoutSuccessor {


        }

    }

    private FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, Drawable dropped, Point dropPoint) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        Connector connector = new DrawableConnector(modifiableGraph, dropped);
        AddDrawableToGraph action = new AddDrawableToGraph(modifiableGraph, dropPoint, connector);
        action.add();
        return modifiableGraph;
    }

    private void assertNodeCountIs(FlowGraph flowGraph, int expectedCount) {
        assertThat(flowGraph.nodesCount()).isEqualTo(expectedCount);
    }

    private void assertThatRootIs(FlowGraph graph, Drawable root) {
        assertThat(graph.root()).isEqualTo(root);
    }

    private void assertThatSuccessorsAreExactly(FlowGraph graph, Drawable target, Drawable... successors) {
        if (successors.length == 0) {
            assertThat(graph.successors(target)).isEmpty();
        } else {
            assertThat(graph.successors(target)).containsExactly(successors);
        }
    }

    private void assertIsChangedWithNodesCount(FlowGraphChangeAware graph, int nodesCount) {
        assertThat(graph.isChanged()).isTrue();
        assertThat(graph.nodesCount()).isEqualTo(nodesCount);
    }

}
