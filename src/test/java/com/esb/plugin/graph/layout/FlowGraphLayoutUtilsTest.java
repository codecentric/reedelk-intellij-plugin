package com.esb.plugin.graph.layout;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

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
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

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
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

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
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choice1);

            // Then
            assertThat(actual).isEqualTo(130 + 130 + 5 + 5);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choice1);
            graph.add(choice1, n1);
            choice1.addToScope(n1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choice1);

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
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choice1);

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
            graph.add(n3, choice2);
            graph.add(choice2, n4);
            graph.add(choice2, n5);
            graph.add(choice2, n6);
            graph.add(n4, n7);
            graph.add(n5, n7);
            graph.add(n6, n7);

            choice1.addToScope(n1);
            choice1.addToScope(n2);

            choice2.addToScope(n4);
            choice2.addToScope(n5);
            choice2.addToScope(n6);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choice1);

            // Then
            assertThat(actual).isEqualTo(130 + 130 + 130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightForGenericRootCorrectly() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(n1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, n1);

            // Then
            assertThat(actual).isEqualTo(130);
        }

        @Test
        void shouldComputeSubTreeHeightForScopedDrawableCorrectly() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(choice1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choice1);

            // Then
            assertThat(actual).isEqualTo(130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightForNestedChoiceCorrectly() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);

            choice1.addToScope(choice2);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then: plus 2 padding/s for two choices
            assertThat(actual).isEqualTo(130 + 5 + 5 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightForMultipleNextedChoices() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice1, choice3);

            choice1.addToScope(choice2);
            choice1.addToScope(choice3);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then: 2 choices on top of each other + padding/s for 2 choices
            assertThat(actual).isEqualTo(130 + 5 + 5 + 130 + 5 + 5 + 5 + 5);
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

            choice1.addToScope(n1);
            choice1.addToScope(n2);
            choice2.addToScope(n4);
            choice2.addToScope(n5);
            choice2.addToScope(n6);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then
            // n4, n5, n6
            assertThat(actual).isEqualTo(130 + 130 + 130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightCorrectlyForFollowingChoicesWithOneSuccessor() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, n2);
            graph.add(n2, choice2);
            graph.add(choice2, n3);

            choice1.addToScope(n1);
            choice2.addToScope(n3);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then
            assertThat(actual).isEqualTo(130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightCorrectlyForChoiceFollowedByChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);

            choice1.addToScope(n1);
            choice2.addToScope(n2);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then
            assertThat(actual).isEqualTo(130 + 5 + 5);
        }

    }
}
