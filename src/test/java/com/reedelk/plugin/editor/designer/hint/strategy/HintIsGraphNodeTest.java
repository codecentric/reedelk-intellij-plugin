package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.awt.*;
import java.awt.image.ImageObserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

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
        verify(strategy).drawNodeHint(graphics, componentNode1);
    }
}