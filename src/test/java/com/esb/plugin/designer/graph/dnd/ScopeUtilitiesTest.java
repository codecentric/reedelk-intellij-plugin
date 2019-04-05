package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScopeUtilitiesTest {

    private final Drawable root = new GenericComponentDrawable(new Component("root"));
    private final Drawable n1 = new GenericComponentDrawable(new Component("n1"));
    private final Drawable n2 = new GenericComponentDrawable(new Component("n2"));

    private final ScopedDrawable choice1 = new ChoiceDrawable(new Component("choice1"));
    private final ScopedDrawable choice2 = new ChoiceDrawable(new Component("choice2"));


    @Test
    void shouldFindScopeReturnCorrectScope() {
        // Given
        FlowGraph graph = new FlowGraph();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice1, n1);

        choice1.addInScope(choice2);
        choice2.addInScope(n1);

        // When
        Optional<ScopedDrawable> actualScope = ScopeUtilities.findScope(graph, n1);

        // Then
        assertThat(actualScope.isPresent()).isTrue();
        assertThat(actualScope.get()).isEqualTo(choice2);
    }

    @Test
    void shouldIsLastOfScopeReturnTrueWhenNoSuccessors() {
        // Given
        FlowGraph graph = new FlowGraph();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, n1);

        choice1.addInScope(n1);

        // When
        boolean isBoundary = ScopeUtilities.isLastOfScope(graph, n1);

        // Then
        assertThat(isBoundary).isTrue();
    }

    @Test
    void shouldIsLastOfScopeReturnTrueWhenOneSuccessorInAnotherScope() {
        // Given
        FlowGraph graph = new FlowGraph();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice2, n1);
        graph.add(n1, n2);

        choice2.addInScope(n1);
        choice1.addInScope(choice2);
        choice1.addInScope(n2);

        // When
        boolean isBoundary = ScopeUtilities.isLastOfScope(graph, n1);

        // Then
        assertThat(isBoundary).isTrue();
    }

    @Test
    void shouldIsLastOfScopeReturnTrueWhenOneSuccessorIsNotInAnyScope() {
        // Given
        FlowGraph graph = new FlowGraph();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice2, n1);
        graph.add(n1, n2);
        choice1.addInScope(choice2);
        choice2.addInScope(n1);

        // When
        boolean isBoundary = ScopeUtilities.isLastOfScope(graph, n1);

        // Then
        assertThat(isBoundary).isTrue();
    }

    @Test
    void shouldIsLastOfScopeReturnTrueWhenScopedDrawableWithoutChildren() {
        // Given
        FlowGraph graph = new FlowGraph();
        graph.add(null, root);
        graph.add(root, choice1);

        // When
        boolean isBoundary = ScopeUtilities.isLastOfScope(graph, choice1);

        // Then
        assertThat(isBoundary).isTrue();
    }

}