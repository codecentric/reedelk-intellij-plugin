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
import static org.mockito.Mockito.*;

class HintIsPredecessorOfPlaceholderNodeTest extends AbstractGraphTest {

    private final Point testPoint = new Point(123, 123);

    @Mock
    private ImageObserver imageObserver;
    @Spy
    private HintIsPredecessorOfPlaceholderNode strategy;


    @Test
    void shouldApplicableReturnTrue() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, placeholderNode1);

        doReturn(true).when(placeholderNode1).contains(eq(imageObserver), anyInt(), anyInt());

        HintResult hintResult = HintResult.from(routerNode1, testPoint);

        // When
        boolean applicable = strategy.applicable(graph, hintResult, graphics, imageObserver);

        // Then
        assertThat(applicable).isTrue();
    }

    @Test
    void shouldApplicableReturnFalse() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, placeholderNode1);

        doReturn(false).when(placeholderNode1).contains(eq(imageObserver), anyInt(), anyInt());

        HintResult hintResult = HintResult.from(routerNode1, testPoint);

        // When
        boolean applicable = strategy.applicable(graph, hintResult, graphics, imageObserver);

        // Then
        assertThat(applicable).isFalse();
    }

    @Test
    void shouldApplicableReturnFalseWhenHintNodeNotFollowedByPlaceholder() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        HintResult hintResult = HintResult.from(routerNode1, testPoint);

        // When
        boolean applicable = strategy.applicable(graph, hintResult, graphics, imageObserver);

        // Then
        assertThat(applicable).isFalse();
        verify(componentNode1, never()).contains(any(ImageObserver.class), anyInt(), anyInt());
    }

    @Test
    void shouldDrawCorrectlyPlaceholderHint() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, placeholderNode1);
        graph.add(routerNode1, placeholderNode2);

        doReturn(false).when(placeholderNode1).contains(eq(imageObserver), anyInt(), anyInt());
        doReturn(true).when(placeholderNode2).contains(eq(imageObserver), anyInt(), anyInt());

        HintResult hintResult = HintResult.from(routerNode1, testPoint);

        // When
        strategy.draw(graph, hintResult, graphics, imageObserver);

        // Then
        verify(strategy).drawPlaceholderHint(graphics, placeholderNode2, imageObserver);
    }
}