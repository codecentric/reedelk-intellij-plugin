package com.esb.plugin.designer.graph.action;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class AddDrawableToGraphTest extends AbstractGraphTest {

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

    private FlowGraph addDrawableToGraph(FlowGraph graph, Drawable dropped, Point dropPoint) {
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


}
