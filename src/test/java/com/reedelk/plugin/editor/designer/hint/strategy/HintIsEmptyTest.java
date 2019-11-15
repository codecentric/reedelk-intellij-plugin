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
import static org.mockito.Mockito.verifyNoMoreInteractions;

class HintIsEmptyTest extends AbstractGraphTest {

    @Mock
    private ImageObserver imageObserver;
    @Spy
    private HintIsEmpty strategy;

    @Test
    void shouldApplicableReturnTrue() {
        // Given
        FlowGraph graph = provider.createGraph();

        // When
        boolean actual = strategy.applicable(graph, HintResult.EMPTY, graphics, imageObserver);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldApplicableReturnFalse() {
        // Given
        FlowGraph graph = provider.createGraph();

        // When
        boolean actual = strategy.applicable(graph, HintResult.from(root, new Point(3, 3)), graphics, imageObserver);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldDrawNothing() {
        // Given
        FlowGraph graph = provider.createGraph();

        // When
        strategy.draw(graph, HintResult.EMPTY, graphics, imageObserver);

        // Then
        verifyNoMoreInteractions(graphics);
    }
}