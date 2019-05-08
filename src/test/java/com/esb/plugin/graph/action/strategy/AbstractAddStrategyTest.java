package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractAddStrategyTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;

    @Test
    void shouldCorrectlyReturnMaxScopeBoundWhenNestedScopes() {
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

        root.setPosition(55, 140);
        choice1.setPosition(165, 140);
        choice2.setPosition(390, 75);
        n1.setPosition(275, 75);
        n2.setPosition(505, 75);
        n3.setPosition(275, 210);

        // When
        int maxScopeXBound = AbstractAddStrategy.getScopeMaxXBound(graph, choice1, graphics);

        // Then
        assertThat(maxScopeXBound).isEqualTo(565);
    }

    @Test
    void shouldCorrectlyReturnMaxScopeBoundWhenScopeDoesNotContainAnyDrawable() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);

        root.setPosition(55, 140);
        choice1.setPosition(165, 140);

        // When
        int maxScopeXBound = AbstractAddStrategy.getScopeMaxXBound(graph, choice1, graphics);

        // Then
        assertThat(maxScopeXBound).isEqualTo(220);
    }

    @Test
    void shouldCorrectlyReturnMaxScopeBoundWhenScopeContainsOnlyOneElement() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);

        root.setPosition(55, 140);
        choice1.setPosition(165, 140);
        n1.setPosition(275, 140);

        choice1.addToScope(n1);

        // When
        int maxScopeXBound = AbstractAddStrategy.getScopeMaxXBound(graph, choice1, graphics);

        // Then
        assertThat(maxScopeXBound).isEqualTo(330);
    }

}