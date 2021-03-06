package de.codecentric.reedelk.plugin.editor.designer.hint.strategy;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.editor.designer.hint.HintResult;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.awt.*;
import java.awt.image.ImageObserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class HintIsRootTest extends AbstractGraphTest {

    @Mock
    private ImageObserver imageObserver;
    @Spy
    private HintIsRoot strategy;

    @Test
    void shouldApplicableReturnTrue() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        HintResult rootHint = HintResult.from(root, new Point(2, 2));

        // When
        boolean actual = strategy.applicable(graph, rootHint, graphics, imageObserver);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldApplicableReturnFalse() {
        // Given
        FlowGraph graph = provider.createGraph();

        // When
        boolean actual = strategy.applicable(graph, HintResult.EMPTY, graphics, imageObserver);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldDrawPlaceholderHintIfRootIsPlaceholder() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(placeholderNode1);
        HintResult rootHint = HintResult.from(placeholderNode1, new Point(2, 2));

        // When
        strategy.draw(graph, rootHint, graphics, imageObserver);

        // Then
        verify(strategy).drawPlaceholderHint(graphics, placeholderNode1, imageObserver);
    }

    @Test
    void shouldDrawNodeHintBeforeIfRootIsNormalNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        HintResult rootHint = HintResult.from(root, new Point(2, 2));

        // When
        strategy.draw(graph, rootHint, graphics, imageObserver);

        // Then
        verify(strategy).drawNodeHintBefore(graphics, root);
    }

    @Test
    void shouldDrawNodeHintEndIfRootIsNormalNodeAndHintPointAfterRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        HintResult rootHint = HintResult.from(root, new Point(154, 128));

        // When
        strategy.draw(graph, rootHint, graphics, imageObserver);

        // Then
        verify(strategy).drawNodeHintEnd(graphics, root);
    }
}