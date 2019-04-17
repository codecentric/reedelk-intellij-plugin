package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;
import java.util.Optional;

import static com.esb.plugin.designer.graph.layout.FlowGraphLayoutUtils.computeSubTreeHeight;
import static org.assertj.core.api.Assertions.assertThat;

class FlowGraphLayoutUtilsTest extends AbstractGraphTest {

    @Mock
    Graphics2D graphics;

    @Nested
    @DisplayName("Compute Max Height Tests")
    class ComputeMaxHeight {

        @Test
        void shouldComputeMaxHeightCorrectlyForRoot() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);

            // When
            int actual = FlowGraphLayoutUtils.computeMaxHeight(graphics, graph, root, Optional.empty(), 0);

            // Then
            assertThat(actual).isEqualTo(130);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForRootFollowedByOneNode() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, n1);

            // When
            int actual = FlowGraphLayoutUtils.computeMaxHeight(graphics, graph, root, Optional.empty(), 0);

            // Then
            assertThat(actual).isEqualTo(130);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForChoiceSubtree() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choice1);
            graph.add(choice1, n1);
            graph.add(choice1, n2);
            choice1.addToScope(n1);
            choice1.addToScope(n2);

            // When
            int actual = FlowGraphLayoutUtils.computeMaxHeight(graphics, graph, choice1, Optional.empty(), 0);

            // Then
            assertThat(actual).isEqualTo(260);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choice1);
            graph.add(choice1, n1);

            // When
            int actual = FlowGraphLayoutUtils.computeMaxHeight(graphics, graph, choice1, Optional.empty(), 0);

            // Then
            assertThat(actual).isEqualTo(140);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForNestedChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);

            choice1.addToScope(n1);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);

            // When
            int actual = FlowGraphLayoutUtils.computeMaxHeight(graphics, graph, choice1, Optional.empty(), 0);

            // Then
            assertThat(actual).isEqualTo(150);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForDisjointSubsequentChoice() {
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

            graph.add(n3, choice2);
            graph.add(choice2, n4);
            graph.add(choice2, n5);
            graph.add(choice2, n6);
            graph.add(n4, n7);
            graph.add(n5, n7);
            graph.add(n6, n7);
            choice2.addToScope(n4);
            choice2.addToScope(n5);
            choice2.addToScope(n6);

            // When
            int actual = FlowGraphLayoutUtils.computeMaxHeight(graphics, graph, choice1, Optional.empty(), 0);

            // Then
            assertThat(actual).isEqualTo(390);
        }
    }


    @Test
    void shouldComputeSubTreeHeightForGenericRootCorrectly() {
        // Given
        FlowGraphImpl graph = new FlowGraphImpl();
        graph.root(n1);

        // When
        int height = computeSubTreeHeight(graph, n1, graphics);

        // Then
        assertThat(height).isEqualTo(130);
    }

    @Test
    void shouldComputeSubTreeHeightForScopedDrawableCorrectly() {
        // Given
        FlowGraphImpl graph = new FlowGraphImpl();
        graph.root(choice1);

        // When
        int height = computeSubTreeHeight(graph, choice1, graphics);

        // Then
        assertThat(height).isEqualTo(130 + 5 + 5);
    }

    @Test
    void shouldComputeSubTreeHeightForNestedChoiceCorrectly() {
        // Given
        FlowGraphImpl graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);

        // When
        int height = computeSubTreeHeight(graph, root, graphics);

        // Then: plus 2 padding/s for two choices
        assertThat(height).isEqualTo(130 + 5 + 5 + 5 + 5);
    }

    @Test
    void shouldComputeSubTreeHeightForMultipleNextedChoices() {
        // Given
        FlowGraphImpl graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice1, choice3);

        // When
        int height = computeSubTreeHeight(graph, root, graphics);

        // Then: 2 choices on top of each other + 3 padding/s for 3 choices
        assertThat(height).isEqualTo(130 + 130 + 5 + 5 + 5 + 5 + 5 + 5);
    }

    @Test
    void shouldComputeSubTreeHeightForDisjointChoicesOnSameLevel() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, n2);
        graph.add(n1, n3);
        graph.add(n2, n3);
        graph.add(n3, choice2);
        graph.add(choice2, n4);
        graph.add(choice2, n5);
        graph.add(choice2, n6);
        graph.add(n4, n7);
        graph.add(n5, n7);
        graph.add(n6, n7);

        // When
        int height = computeSubTreeHeight(graph, root, graphics);

        // Then
        assertThat(height).isEqualTo(130 + 5 + 5 + 130 + 5 + 5 + 130 + 5 + 5);
    }

}
