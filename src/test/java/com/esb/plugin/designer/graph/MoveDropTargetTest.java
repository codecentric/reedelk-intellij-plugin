package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.manager.MoveDropTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MoveDropTargetTest extends AbstractGraphTest {

    private MoveDropTarget delegate;

    @BeforeEach
    void setUp() {
        super.setUp();
        delegate = new MoveDropTarget();
    }

    @Test
    void shouldNotConnectChoiceToDrawableOutsideScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);
        choice1.addToScope(n1);
        choice1.addToScope(n2);

        root.setPosition(50, 100);
        choice1.setPosition(100, 100);
        n1.setPosition(150, 50);
        n2.setPosition(150, 150);
        n3.setPosition(200, 100); // not in choice1 drawable's scope

        // n2 gets moved next to n1
        int x = 170;
        int y = 50;
        Drawable dropped = n2;

        // When
        Optional<FlowGraph> optionalGraph = delegate.drop(x, y, graph, dropped);

        // Then
        assertIsPresentWithNodesCount(optionalGraph, 5);
        FlowGraph updatedGraph = optionalGraph.get();

        // We verify that the choice does not get connected to the successor
        // of the moved element because it is outside the scope.
        // By definition a Choice component cannot connect drawables outside the scope.
        List<Drawable> choice1Successors = updatedGraph.successors(choice1);
        assertThat(choice1Successors).containsExactly(n1);
    }

    private void assertIsPresentWithNodesCount(Optional<FlowGraph> optionalFlowGraph, int nodesCount) {
        assertThat(optionalFlowGraph.isPresent()).isTrue();
        assertThat(optionalFlowGraph.get().nodesCount()).isEqualTo(nodesCount);
    }

}