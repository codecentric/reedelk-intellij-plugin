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

class HintIsPlaceholderNodeTest extends AbstractGraphTest {

    @Mock
    private ImageObserver imageObserver;
    @Spy
    private HintIsPlaceholderNode strategy;

    @Test
    void shouldApplicableReturnTrueWhenHintIsPlaceholderNodeWithinItsBounds() {
        // Given
        FlowGraph graph = provider.createGraph();
        placeholderNode1.setPosition(65, 150);
        mockDefaultNodeHeight(placeholderNode1);
        HintResult result = HintResult.from(placeholderNode1, new Point(60, 140));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldApplicableReturnFalseWhenHintIsPlaceholderNodeOutsideItsBounds() {
        // Given
        FlowGraph graph = provider.createGraph();
        placeholderNode1.setPosition(65, 150);
        mockDefaultNodeHeight(placeholderNode1);

        // The hint point is one pixel right outside the placeholder icon, therefore
        // this strategy should NOT be applicable.
        HintResult result = HintResult.from(placeholderNode1, new Point(96, 140));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldApplicableReturnFalseWhenNodeIsNotPlaceholder() {
        // Given
        FlowGraph graph = provider.createGraph();
        componentNode1.setPosition(65, 150);
        mockDefaultNodeHeight(componentNode1);
        HintResult result = HintResult.from(componentNode1, new Point(60, 140));

        // When
        boolean actual = strategy.applicable(graph, result, graphics, imageObserver);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldDrawCorrectlyPlaceholderHint() {
        // Given
        FlowGraph graph = provider.createGraph();
        placeholderNode1.setPosition(65, 150);
        mockDefaultNodeHeight(placeholderNode1);
        HintResult result = HintResult.from(placeholderNode1, new Point(60, 140));

        // When
        strategy.draw(graph, result, graphics, imageObserver);

        // Then
        verify(strategy).drawPlaceholderHint(graphics, placeholderNode1, imageObserver);
    }
}