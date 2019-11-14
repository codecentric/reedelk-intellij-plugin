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

class HintIsScopedGraphNodeTest extends AbstractGraphTest {

    @Mock
    private ImageObserver imageObserver;
    @Spy
    private HintIsScopedGraphNode strategy;

    @Test
    void shouldApplicableReturnTrue() {
        // Given
        FlowGraph graph = provider.createGraph();
        HintResult result = HintResult.from(routerNode1, new Point(32, 43));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldApplicableReturnFalse() {
        // Given
        FlowGraph graph = provider.createGraph();
        HintResult result = HintResult.from(componentNode1, new Point(32, 43));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldDrawEndOfScopeHintWhenScopedNodeBelongsToScopeAndHintPointInsideOuterScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);

        root.setPosition(65, 160);
        forkNode1.setPosition(195, 160);
        forkNode2.setPosition(325, 160);

        forkNode1.addToScope(forkNode2);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(forkNode1);
        mockDefaultNodeHeight(forkNode2);

        Point hintPoint = new Point(399, 150);
        HintResult result = HintResult.from(forkNode2, hintPoint);

        // When
        strategy.draw(graph, result, graphics, imageObserver);

        // Then
        verify(strategy).drawEndOfScopeHint(eq(graphics), any(ScopeBoundaries.class));
    }
}