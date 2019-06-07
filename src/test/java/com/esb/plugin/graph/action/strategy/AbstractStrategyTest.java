package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.commons.Half;
import com.esb.plugin.component.type.choice.ChoiceNode;
import com.esb.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractStrategyTest extends AbstractGraphTest {

    @Mock
    private Graphics2D graphics;

    @Test
    void shouldCorrectlyReturnMaxScopeBoundWhenNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
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

        root.setPosition(55, 140);
        choiceNode1.setPosition(165, 140);
        choiceNode2.setPosition(390, 75);
        componentNode1.setPosition(275, 75);
        componentNode2.setPosition(505, 75);
        componentNode3.setPosition(275, 210);

        // When
        int maxScopeXBound = AbstractStrategy.getScopeMaxXBound(graph, choiceNode1, graphics);

        // Then
        assertThat(maxScopeXBound).isEqualTo(565);
    }

    @Test
    void shouldCorrectlyReturnMaxScopeBoundWhenScopeDoesNotContainAnyDrawable() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);

        root.setPosition(55, 140);
        choiceNode1.setPosition(165, 140);

        // When
        int maxScopeXBound = AbstractStrategy.getScopeMaxXBound(graph, choiceNode1, graphics);

        // Then
        int maxX = choiceNode1.x() + Half.of(ChoiceNode.WIDTH);
        assertThat(maxScopeXBound).isEqualTo(maxX);
    }

    @Test
    void shouldCorrectlyReturnMaxScopeBoundWhenScopeContainsOnlyOneElement() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);

        root.setPosition(55, 140);
        choiceNode1.setPosition(165, 140);
        componentNode1.setPosition(275, 140);

        choiceNode1.addToScope(componentNode1);

        // When
        int maxScopeXBound = AbstractStrategy.getScopeMaxXBound(graph, choiceNode1, graphics);

        // Then
        assertThat(maxScopeXBound).isEqualTo(330);
    }
}