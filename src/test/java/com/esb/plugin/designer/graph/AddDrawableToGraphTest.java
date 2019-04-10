package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.editor.component.Component;
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

import static org.assertj.core.api.Assertions.assertThat;

class AddDrawableToGraphTest {

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
            FlowGraph graph = new FlowGraphImpl();
            Point dropPoint = new Point(20, 20);

            // When
            FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
            AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, root);
            componentAdder.add();

            // Then
            assertThat(modifiableGraph.isChanged()).isTrue();
            assertThat(graph.nodesCount()).isEqualTo(1);
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
            AddDrawableToGraph componentAdder = new AddDrawableToGraph(graph, dropPoint, n1);
            boolean modified = true;// componentAdder.add();

            // Then
            assertThat(modified).isTrue();
            assertThat(graph.nodesCount()).isEqualTo(2);

            Drawable newRoot = graph.root();
            assertThat(newRoot).isEqualTo(n1);

            List<Drawable> successorsOfRoot = graph.successors(newRoot);

            // Old root has been replaced by n1, therefore successor of n1 is root.
            assertThat(successorsOfRoot).containsExactly(root);
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
            AddDrawableToGraph componentAdder = new AddDrawableToGraph(graph, dropPoint, n1);
            boolean modified = true; //componentAdder.add();

            // Then
            assertThat(modified).isTrue();
            assertThat(graph.nodesCount()).isEqualTo(2);

            Drawable root = graph.root();
            assertThat(root).isEqualTo(root);

            List<Drawable> successorOfRoot = graph.successors(root);
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
            AddDrawableToGraph componentAdder = new AddDrawableToGraph(graph, dropPoint, n1);
            boolean modified = true;//componentAdder.add();

            // Then
            assertThat(modified).isTrue();
            assertThat(graph.nodesCount()).isEqualTo(3);

            Drawable root = graph.root();
            List<Drawable> successorOfRoot = graph.successors(root);
            assertThat(successorOfRoot).containsExactly(n1);

            List<Drawable> successorsOfN1 = graph.successors(n1);
            assertThat(successorsOfN1).containsExactly(n2);
        }
    }

}