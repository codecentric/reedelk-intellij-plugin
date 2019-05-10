package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsLastScopeBeforeNodeTest extends AbstractGraphTest {

    @Test
    void shouldIsLastScopeBeforeNodeReturnTrueWhenLastScope() {
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
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, choiceNode1, componentNode3);

        // Then
        assertThat(isLastScope).isTrue();
    }

    @Test
    void shouldIsLastScopeBeforeNodeReturnTrueWhenNestedScope() {
        // Given
        FlowGraph graph = graphProvider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);

        choiceNode2.addToScope(componentNode2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, choiceNode1, componentNode3);

        // Then
        assertThat(isLastScope).isTrue();
    }

    @Test
    void shouldIsLastScopeBeforeNodeReturnFalseWhenInnermostNestedScope() {
        // Given
        FlowGraph graph = graphProvider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);

        choiceNode2.addToScope(componentNode2);

        // When
        boolean isLastScope = IsLastScopeBeforeNode.of(graph, choiceNode2, componentNode3);

        // Then
        assertThat(isLastScope).isFalse();
    }

}