package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.awt.*;
import java.awt.image.ImageObserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HintIsGraphNodeTest extends AbstractGraphTest {

    @Mock
    private ImageObserver imageObserver;
    @Spy
    private HintIsGraphNode strategy;

    @Test
    void shouldApplicableReturnTrue() {
        // Given
        FlowGraph graph = provider.createGraph();
        HintResult result = HintResult.from(componentNode1, new Point(32, 43));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldApplicableReturnFalse() {
        // Given
        FlowGraph graph = provider.createGraph();
        HintResult result = HintResult.from(routerNode1, new Point(32, 43));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldDrawNodeHintIfNodeDoesNotBelongToAnyScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        HintResult result = HintResult.from(componentNode1, new Point(32, 43));

        // When
        strategy.draw(graph, result, graphics, imageObserver);

        // Then
        verify(strategy).drawNodeHintAfter(graphics, componentNode1);
    }

    @Test
    void shouldDrawEndOfScopeHintWhenNodeBelongsToScopeAndHintPointOutsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(365, 155);

        routerNode1.addToScope(componentNode1);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);

        Point hintPoint = new Point(475, 140);
        HintResult result = HintResult.from(componentNode1, hintPoint);

        // When
        strategy.draw(graph, result, graphics, imageObserver);

        // Then
        verify(strategy).drawEndOfScopeHint(eq(graphics), any(ScopeBoundaries.class));
    }

    @Test
    void shouldDrawNodeHintWhenNodeBelongsToScopeAndHintPointInsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(365, 155);

        routerNode1.addToScope(componentNode1);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);

        Point hintPoint = new Point(370, 140);
        HintResult result = HintResult.from(componentNode1, hintPoint);

        // When
        strategy.draw(graph, result, graphics, imageObserver);

        // Then
        verify(strategy).drawNodeHintAfter(graphics, componentNode1);
    }

    @Test
    void shouldDrawNodeHintWhenScopedNodeBelongsToScopeAndHintPointInsideScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        root.setPosition(65, 155);
        routerNode1.setPosition(215, 155);
        componentNode1.setPosition(365, 155);

        routerNode1.addToScope(componentNode1);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);

        Point hintPoint = new Point(370, 140);
        HintResult result = HintResult.from(componentNode1, hintPoint);

        // When
        strategy.draw(graph, result, graphics, imageObserver);

        // Then
        verify(strategy).drawNodeHintAfter(graphics, componentNode1);
    }
}