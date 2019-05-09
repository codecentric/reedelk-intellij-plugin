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
            graph.add(root, componentNode1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then
            assertThat(actual).isEqualTo(130);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForChoiceSubtree() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(choiceNode1, componentNode2);
            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(componentNode2);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choiceNode1);

            // Then
            assertThat(actual).isEqualTo(130 + 130 + 5 + 5);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choiceNode1);
            graph.add(choiceNode1, componentNode1);
            choiceNode1.addToScope(componentNode1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choiceNode1);

            // Then
            assertThat(actual).isEqualTo(140);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForNestedChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, choiceNode2);
            graph.add(choiceNode2, componentNode2);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(choiceNode2);
            choiceNode2.addToScope(componentNode2);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choiceNode1);

            // Then
            assertThat(actual).isEqualTo(150);
        }

        @Test
        void shouldComputeMaxHeightCorrectlyForDisjointSubsequentChoice() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(choiceNode1, componentNode2);
            graph.add(componentNode1, componentNode3);
            graph.add(componentNode2, componentNode3);
            graph.add(componentNode3, choiceNode2);
            graph.add(choiceNode2, componentNode4);
            graph.add(choiceNode2, componentNode5);
            graph.add(choiceNode2, componentNode6);
            graph.add(componentNode4, componentNode7);
            graph.add(componentNode5, componentNode7);
            graph.add(componentNode6, componentNode7);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(componentNode2);

            choiceNode2.addToScope(componentNode4);
            choiceNode2.addToScope(componentNode5);
            choiceNode2.addToScope(componentNode6);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choiceNode1);

            // Then
            assertThat(actual).isEqualTo(130 + 130 + 130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightForGenericRootCorrectly() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(componentNode1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, componentNode1);

            // Then
            assertThat(actual).isEqualTo(130);
        }

        @Test
        void shouldComputeSubTreeHeightForScopedDrawableCorrectly() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(choiceNode1);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, choiceNode1);

            // Then
            assertThat(actual).isEqualTo(130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightForNestedChoiceCorrectly() {
            // Given
            FlowGraphImpl graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, choiceNode2);

            choiceNode1.addToScope(choiceNode2);

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
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, choiceNode2);
            graph.add(choiceNode1, choiceNode3);

            choiceNode1.addToScope(choiceNode2);
            choiceNode1.addToScope(choiceNode3);

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
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(choiceNode1, componentNode2);
            graph.add(componentNode1, componentNode3);
            graph.add(componentNode2, componentNode3);
            graph.add(componentNode3, choiceNode2);
            graph.add(choiceNode2, componentNode4);
            graph.add(choiceNode2, componentNode5);
            graph.add(choiceNode2, componentNode6);
            graph.add(componentNode4, componentNode7);
            graph.add(componentNode5, componentNode7);
            graph.add(componentNode6, componentNode7);

            choiceNode1.addToScope(componentNode1);
            choiceNode1.addToScope(componentNode2);
            choiceNode2.addToScope(componentNode4);
            choiceNode2.addToScope(componentNode5);
            choiceNode2.addToScope(componentNode6);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then
            // componentNode4, componentNode5, componentNode6
            assertThat(actual).isEqualTo(130 + 130 + 130 + 5 + 5);
        }

        @Test
        void shouldComputeSubTreeHeightCorrectlyForFollowingChoicesWithOneSuccessor() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, componentNode2);
            graph.add(componentNode2, choiceNode2);
            graph.add(choiceNode2, componentNode3);

            choiceNode1.addToScope(componentNode1);
            choiceNode2.addToScope(componentNode3);

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
            graph.add(root, choiceNode1);
            graph.add(choiceNode1, componentNode1);
            graph.add(componentNode1, choiceNode2);
            graph.add(choiceNode2, componentNode2);

            choiceNode1.addToScope(componentNode1);
            choiceNode2.addToScope(componentNode2);

            // When
            int actual = FlowGraphLayoutUtils.maxHeight(graph, graphics, root);

            // Then
            assertThat(actual).isEqualTo(130 + 5 + 5);
        }

    }
}
