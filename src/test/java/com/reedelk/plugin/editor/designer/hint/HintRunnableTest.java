package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

class HintRunnableTest extends AbstractGraphTest {

    private HintRunnable hintRunnable;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.hintRunnable = new HintRunnable(null, graphics, null);
    }

    @Test
    void shouldReturnEmptyWhenRootIsNull() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        Point hintPoint = new Point(60, 130);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, null, hintPoint);

        // Then
        assertThat(hintResult).isEqualTo(HintResult.EMPTY);
    }

    @Test
    void shouldReturnRootWhenHintPointBeforeRootAndWithinTopAndBottomBounds() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        root.setPosition(65, 150);
        mockDefaultNodeHeight(root);
        Point hintPoint = new Point(60, 130);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, root, hintPoint);

        // Then
        assertThat(hintResult.getHintNode()).isEqualTo(root);
    }

    @Test
    void shouldReturnRootWhenHintPointOutsideRootXBoundsAndRootIsOnlyNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        root.setPosition(65, 150);
        mockDefaultNodeHeight(root);
        Point hintPoint = new Point(500, 130);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, root, hintPoint);

        // Then
        assertThat(hintResult.getHintNode()).isEqualTo(root);
    }

    @Test
    void shouldReturnRootEmptyWhenHintPointOutsideRootYBounds() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        root.setPosition(65, 150);
        mockDefaultNodeHeight(root);
        Point hintPoint = new Point(60, 430);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, root, hintPoint);

        // Then
        assertThat(hintResult).isEqualTo(HintResult.EMPTY);
    }

    @Test
    void shouldReturnNodeWhenHintPointAfterNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        root.setPosition(65, 150);
        componentNode1.setPosition(195, 150);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);

        Point hintPoint = new Point(197, 170);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, root, hintPoint);

        // Then
        assertThat(hintResult.getHintNode()).isEqualTo(componentNode1);
        assertThat(hintResult.getHintPoint()).isEqualTo(hintPoint);
    }

    @Test
    void shouldReturnRootWhenHintPointBeforeNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        root.setPosition(65, 150);
        componentNode1.setPosition(195, 150);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);

        Point hintPoint = new Point(190, 170);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, root, hintPoint);

        // Then
        assertThat(hintResult.getHintNode()).isEqualTo(root);
    }

    @Test
    void shouldReturnFirstNodeWhenHintPointBeforeSecondNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        root.setPosition(65, 150);
        componentNode1.setPosition(195, 150);
        componentNode2.setPosition(325, 150);

        mockDefaultNodeHeight(root);
        mockDefaultNodeHeight(componentNode1);
        mockDefaultNodeHeight(componentNode2);

        Point hintPoint = new Point(200, 130);

        // When
        HintResult hintResult = hintRunnable.computeHintResult(graph, root, hintPoint);

        // Then
        assertThat(hintResult.getHintNode()).isEqualTo(componentNode1);
        assertThat(hintResult.getHintPoint()).isEqualTo(hintPoint);
    }
}