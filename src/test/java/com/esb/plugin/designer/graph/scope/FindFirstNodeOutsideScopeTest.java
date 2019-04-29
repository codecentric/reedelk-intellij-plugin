package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindFirstNodeOutsideScopeTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReturnEmptyWhenTwoLevelsAndOneContainsANestedScopeWithoutSuccessorsOutsideScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(choice1, n3);

        choice1.addToScope(n1);
        choice1.addToScope(n3);
        choice1.addToScope(choice2);

        choice2.addToScope(n2);

        // When
        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, choice1);

        // Then
        assertThat(firstNodeOutsideScope.isPresent()).isFalse();
    }

    @Test
    void shouldCorrectlyReturnFirstDrawableOutsideScopeWhenChoiceWithTwoChildren() {
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

        // When
        Optional<GraphNode> drawables = FindFirstNodeOutsideScope.of(graph, choice1);

        // Then
        assertThat(drawables.get()).isEqualTo(n3);
    }

}