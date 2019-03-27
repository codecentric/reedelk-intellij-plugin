package com.esb.plugin.designer.editor.common;


import com.esb.plugin.designer.graph.Drawable;
import com.google.common.graph.MutableGraph;
import org.assertj.core.util.introspection.FieldSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FlowDataStructureTest {

    @Mock
    private Drawable N1;
    @Mock
    private Drawable N2;
    @Mock
    private Drawable N3;

    private FlowDataStructure structure;

    @BeforeEach
    void setUp() {
        structure = new FlowDataStructure();
    }

    @Test
    void shouldAddCorrectlyRoot() {
        // When
        structure.add(null, N1);

        // Then
        MutableGraph<Drawable> graph = extractGraphUsingReflection();
        assertThat(graph.nodes()).containsExactly(N1);
    }

    @Test
    void shouldCorrectlyAddChildOfRoot() {
        // Given
        structure.add(null, N1);

        // When
        structure.add(N1, N2);

        // Then
        MutableGraph<Drawable> graph = extractGraphUsingReflection();
        assertThat(graph.nodes()).containsExactly(N1, N2);

        assertThat(graph.successors(N1)).containsExactly(N2);
    }

    @Test
    void shouldCorrectlyAddIntermediateChildOfRoot() {
        // Given
        structure.add(null, N1);
        structure.add(N1, N2);

        // When
        structure.add(N1, N3);


        // Then
        MutableGraph<Drawable> graph = extractGraphUsingReflection();
        assertThat(graph.nodes()).containsExactly(N1, N2, N3);

        assertThat(graph.successors(N1)).containsExactly(N3);
        assertThat(graph.successors(N3)).containsExactly(N2);
        assertThat(graph.successors(N2)).isEmpty();
    }

    /**
     * N1 -->  N2
     */
    @Test
    void shouldClosestParentOfWorkCorrectlyAfterRoot() {
        // Given
        doReturn(new Point(10, 10)).when(N1).getPosition();
        doReturn(new Point(20, 10)).when(N2).getPosition();

        structure.add(null, N1);


        // When
        Drawable drawable = structure.closestParentOf(N2);

        // Then
        assertThat(drawable).isEqualTo(N1);
    }

    /**
     * N1 --> N2 --> N3
     */
    @Test
    void shouldClosestPrentOfWorkCorrectlyAfterN2() {
        // Given
        doReturn(new Point(10, 10)).when(N1).getPosition();
        doReturn(new Point(20, 10)).when(N2).getPosition();
        doReturn(new Point(30, 10)).when(N3).getPosition();

        structure.add(null, N1);
        structure.add(N1, N2);

        // When
        Drawable drawable = structure.closestParentOf(N3);

        // Then
        assertThat(drawable).isEqualTo(N2);

    }

    private MutableGraph<Drawable> extractGraphUsingReflection() {
        return FieldSupport.EXTRACTION.fieldValue("flowGraph", MutableGraph.class, structure);
    }
}