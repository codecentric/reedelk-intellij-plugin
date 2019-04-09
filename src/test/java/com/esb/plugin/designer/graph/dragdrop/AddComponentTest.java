package com.esb.plugin.designer.graph.dragdrop;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AddComponentTest {

    private Drawable root;
    private Drawable n1;
    private Drawable n2;
    private Drawable n3;

    private ScopedDrawable choice1;
    private ScopedDrawable choice2;
    private ScopedDrawable choice3;

    @BeforeEach
    void setUp() {
        root = new GenericComponentDrawable(new Component("root"));
        n1 = new GenericComponentDrawable(new Component("n1"));
        n2 = new GenericComponentDrawable(new Component("n2"));
        n3 = new GenericComponentDrawable(new Component("n3"));
        choice1 = new ChoiceDrawable(new Component("choice1"));
        choice2 = new ChoiceDrawable(new Component("choice2"));
        choice3 = new ChoiceDrawable(new Component("choice3"));
    }

    @Nested
    @DisplayName("Root tests")
    class RootComponent {

        @Test
        void shouldCorrectlyAddRootComponent() {
            // Given
            FlowGraph graph = new FlowGraph();
            Point dropPoint = new Point(20, 20);

            // When
            AddComponent componentAdder = new AddComponent(graph, dropPoint, root);
            Optional<FlowGraph> optionalGraph = componentAdder.add();

            // Then
            assertThat(optionalGraph.isPresent()).isTrue();

            FlowGraph modifiedGraph = optionalGraph.get();
            assertThat(modifiedGraph).isNotEqualTo(graph);

            assertThat(modifiedGraph.nodesCount()).isEqualTo(1);
            assertThat(modifiedGraph.root()).isEqualTo(root);
        }

        @Test
        void shouldCorrectlyReplaceRootComponentWhenXCoordinateIsSmallerThanCurrentRoot() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.root(root);
            root.setPosition(20, 20);

            Point dropPoint = new Point(10, 20); // x drop point smaller than the root x coordinate.

            // When
            AddComponent componentAdder = new AddComponent(graph, dropPoint, n1);
            Optional<FlowGraph> optionalGraph = componentAdder.add();

            // Then
            FlowGraph modifiedGraph = optionalGraph.get();
            assertThat(modifiedGraph.nodesCount()).isEqualTo(2);

            Drawable newRoot = modifiedGraph.root();
            assertThat(newRoot).isEqualTo(n1);

            List<Drawable> successorsOfRoot = modifiedGraph.successors(newRoot);

            // Old root has been replaced by n1, therefore successor of n1 is root.
            assertThat(successorsOfRoot).containsExactly(root);
        }

    }

    @Nested
    @DisplayName("Adding a component after root")
    class AddComponentAfterRoot {

        @Test
        void shouldAddComponentAfterRootAsLast() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.root(root);
            root.setPosition(20, 20);

            Point dropPoint = new Point(25, 23);  // a little bit after root center x coordinate

            // When
            AddComponent componentAdder = new AddComponent(graph, dropPoint, n1);
            Optional<FlowGraph> optionalFlowGraph = componentAdder.add();

            // Then
            FlowGraph modifiedGraph = optionalFlowGraph.get();
            assertThat(modifiedGraph.nodesCount()).isEqualTo(2);

            Drawable root = modifiedGraph.root();
            assertThat(root).isEqualTo(root);

            List<Drawable> successorOfRoot = modifiedGraph.successors(root);
            assertThat(successorOfRoot).containsExactly(n1);
        }

        @Test
        void shouldAddComponentAfterRootAndSuccessor() {
            // Given
            FlowGraph graph = new FlowGraph();

            graph.root(root);
            root.setPosition(20, 20);

            graph.add(root, n2);
            n2.setPosition(40, 20);

            Point dropPoint = new Point(30, 20); // drop it between root and n2

            // When
            AddComponent componentAdder = new AddComponent(graph, dropPoint, n1);
            Optional<FlowGraph> optionalFlowGraph = componentAdder.add();

            // Then
            FlowGraph modifiedGraph = optionalFlowGraph.get();
            assertThat(modifiedGraph.nodesCount()).isEqualTo(3);

            Drawable root = modifiedGraph.root();
            List<Drawable> successorOfRoot = modifiedGraph.successors(root);
            assertThat(successorOfRoot).containsExactly(n1);

            List<Drawable> successorsOfN1 = modifiedGraph.successors(n1);
            assertThat(successorsOfN1).containsExactly(n2);
        }
    }

}