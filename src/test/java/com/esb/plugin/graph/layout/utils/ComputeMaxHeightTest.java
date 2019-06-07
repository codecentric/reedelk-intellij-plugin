package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.type.choice.ChoiceNode;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.editor.designer.AbstractGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Compute Max Height Tests")
class ComputeMaxHeightTest extends AbstractGraphTest {

    @Mock
    Graphics2D graphics;

    @Test
    void shouldComputeMaxHeightCorrectlyForRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(AbstractGraphNode.HEIGHT);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRootFollowedByOneNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(AbstractGraphNode.HEIGHT);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForChoiceSubtree() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, componentNode2);
        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode2);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, choiceNode1);

        // Then
        // Two nodes on top of each other plus need to take into account
        // the vertical top and bottom padding of the scope.
        assertThat(actual).isEqualTo(
                AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRouterWithoutSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(choiceNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, choiceNode1);

        // Then
        // scoped graph node has top and bottom padding
        assertThat(actual).isEqualTo(
                ChoiceNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForRouterWithSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(choiceNode1);
        graph.add(choiceNode1, componentNode1);
        choiceNode1.addToScope(componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, choiceNode1);

        // Then
        // scoped graph node has top and bottom padding, the following
        // componentNode1 has height lower than the choice.
        assertThat(actual).isEqualTo(
                ChoiceNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForForkWithoutSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(forkNode1);
        graph.add(forkNode1, componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, forkNode1);

        // Then
        assertThat(actual).isEqualTo(
                ForkNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForForkWithSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(forkNode1);
        graph.add(forkNode1, componentNode1);
        forkNode1.addToScope(componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, forkNode1);

        // Then
        assertThat(actual).isEqualTo(
                ForkNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForNestedRouter() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode2);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, choiceNode1);

        // Then
        // Nexted Router height is the height of the node + the 2 vertical
        // padding for the innermost router and the outermost router.
        assertThat(actual).isEqualTo(ChoiceNode.HEIGHT +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeMaxHeightCorrectlyForDisjointSubsequentRouters() {
        // Given
        FlowGraph graph = provider.createGraph();
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
        int actual = ComputeMaxHeight.of(graph, graphics, choiceNode1);

        // Then
        assertThat(actual).isEqualTo(
                AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightForNestedChoiceCorrectlyWhenEndNodeOutsideScopeIsProvided() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.add(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);

        choiceNode1.addToScope(choiceNode2);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, choiceNode1, componentNode1);

        // Then
        assertThat(actual).isEqualTo(ChoiceNode.HEIGHT +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING +
                ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightForMultipleNestedChoices() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode1, choiceNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(choiceNode3);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then: 2 choices on top of each other + padding/s for 2 choices
        assertThat(actual).isEqualTo(
                ChoiceNode.HEIGHT +
                        ChoiceNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightForDisjointChoicesOnSameLevel() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, componentNode2);
        graph.add(choiceNode1, componentNode3);
        graph.add(componentNode2, choiceNode2);
        graph.add(choiceNode2, componentNode4);
        graph.add(choiceNode2, componentNode5);
        graph.add(choiceNode2, componentNode6);
        graph.add(componentNode1, componentNode7);
        graph.add(componentNode4, componentNode7);
        graph.add(componentNode5, componentNode7);
        graph.add(componentNode6, componentNode7);
        graph.add(componentNode3, componentNode7);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode4);
        choiceNode2.addToScope(componentNode5);
        choiceNode2.addToScope(componentNode6);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(
                AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        AbstractGraphNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightCorrectlyForFollowingChoicesWithOneSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode3);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(
                ChoiceNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }

    @Test
    void shouldComputeSubTreeHeightCorrectlyForChoiceNestedWithFork() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, forkNode1);
        graph.add(forkNode1, componentNode1);

        choiceNode1.addToScope(forkNode1);
        forkNode1.addToScope(componentNode1);

        // When
        int actual = ComputeMaxHeight.of(graph, graphics, root);

        // Then
        assertThat(actual).isEqualTo(
                ForkNode.HEIGHT +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING +
                        ScopedGraphNode.VERTICAL_PADDING);
    }
}
