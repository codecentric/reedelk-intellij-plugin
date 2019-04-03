package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScopeUtilitiesTest {

    @Mock
    private FlowGraph mockGraph;
    @Mock
    private Drawable drawable1;
    @Mock
    private Drawable drawable2;
    @Mock
    private Drawable drawable3;
    @Mock
    private ScopedDrawable outerScope;
    @Mock
    private ScopedDrawable innerScope;

    @Test
    void shouldFindScopeCorrectly() {
        // Given
        doReturn(asList(drawable1, drawable2, drawable3, outerScope, innerScope)).when(mockGraph).nodes();
        doReturn(false).when(outerScope).contains(drawable3);
        doReturn(true).when(innerScope).contains(drawable3);

        // When
        Optional<ScopedDrawable> actualScope = ScopeUtilities.findScope(mockGraph, drawable3);

        // Then
        assertThat(actualScope.isPresent()).isTrue();
        assertThat(actualScope.get()).isEqualTo(innerScope);
    }


}