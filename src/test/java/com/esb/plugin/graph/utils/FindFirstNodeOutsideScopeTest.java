package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindFirstNodeOutsideScopeTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReturnEmptyWhenTwoLevelsAndOneContainsANestedScopeWithoutSuccessorsOutsideScope() {
        // Given
        FlowGraph graph = graphProvider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(choiceNode1, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(choiceNode2);

        choiceNode2.addToScope(componentNode2);

        // When
        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, choiceNode1);

        // Then
        assertThat(firstNodeOutsideScope.isPresent()).isFalse();
    }

    @Test
    void shouldCorrectlyReturnFirstDrawableOutsideScopeWhenChoiceWithTwoChildren() {
        // Given
        FlowGraph graph = graphProvider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode2);

        // When
        Optional<GraphNode> drawables = FindFirstNodeOutsideScope.of(graph, choiceNode1);

        // Then
        assertThat(drawables.get()).isEqualTo(componentNode3);
    }

}