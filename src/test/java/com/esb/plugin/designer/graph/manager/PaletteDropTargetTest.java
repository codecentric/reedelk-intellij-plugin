package com.esb.plugin.designer.graph.manager;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PaletteDropTargetTest extends AbstractGraphTest {

    private PaletteDropTarget delegate;

    @Mock
    private Graphics2D graphics;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        delegate = spy(new PaletteDropTarget());
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

        doReturn(Optional.of("TestComponent"))
                .when(delegate)
                .extractComponentName(any(DropTargetDropEvent.class));


        doAnswer(invocation -> {
            FlowGraph flowGraph = invocation.getArgument(1);
            return new DrawableConnector(flowGraph, n3);
        }).when(delegate).createComponentConnector(anyString(), any(FlowGraph.class));


        // When
        DropTargetDropEvent event = mock(DropTargetDropEvent.class);
        doReturn(new Point(238, 128)).when(event).getLocation(); // right below n1

        Optional<FlowGraph> optionalUpdatedGraph = delegate.drop(event, graph);

        // Then
        assertThat(optionalUpdatedGraph.isPresent()).isTrue();
        FlowGraph updatedGraph = optionalUpdatedGraph.get();

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

        doReturn(Optional.of("TestComponent"))
                .when(delegate)
                .extractComponentName(any(DropTargetDropEvent.class));


        doAnswer(invocation -> {
            FlowGraph flowGraph = invocation.getArgument(1);
            return new DrawableConnector(flowGraph, n5);
        }).when(delegate).createComponentConnector(anyString(), any(FlowGraph.class));


        // When
        DropTargetDropEvent event = mock(DropTargetDropEvent.class);
        doReturn(new Point(479, 196)).when(event).getLocation(); // right below n1

        Optional<FlowGraph> optionalUpdatedGraph = delegate.drop(event, graph);

        // Then
        FlowGraph updatedGraph = optionalUpdatedGraph.get();

        assertThatRootIs(updatedGraph, root);
        assertThatSuccessorsAreExactly(updatedGraph, root, choice1);
        assertThatSuccessorsAreExactly(updatedGraph, choice1, n1, n3);
        assertThatSuccessorsAreExactly(updatedGraph, n3, n4);
        assertThatSuccessorsAreExactly(updatedGraph, n4, n5);
        assertThatSuccessorsAreExactly(updatedGraph, n5);
        assertThatSuccessorsAreExactly(updatedGraph, n1, choice2);
        assertThatSuccessorsAreExactly(updatedGraph, choice2, n2);
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
