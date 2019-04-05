package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    private final ScopedDrawable choice3 = new ChoiceDrawable(new Component("choice3"));

    @Nested
    @DisplayName("Find Scope Tests")
    class FindScope {

        @Test
        void shouldReturnCorrectScope() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice1, n1);

            choice1.addToScope(choice2);
            choice2.addToScope(n1);

            // When
            Optional<ScopedDrawable> actualScope = ScopeUtilities.findScope(graph, n1);

            // Then
            assertThat(actualScope.isPresent()).isTrue();
            assertThat(actualScope.get()).isEqualTo(choice2);
        }
    }

    @Nested
    @DisplayName("Is Last of Scope Tests")
    class IsLastOfScope {

        @Test
        void shouldReturnTrueWhenNoSuccessors() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, n1);

            choice1.addToScope(n1);

            // When
            boolean isBoundary = ScopeUtilities.isLastOfScope(graph, n1);

            // Then
            assertThat(isBoundary).isTrue();
        }

        @Test
        void shouldReturnTrueWhenOneSuccessorInAnotherScope() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, n1);
            graph.add(n1, n2);

            choice2.addToScope(n1);
            choice1.addToScope(choice2);
            choice1.addToScope(n2);

            // When
            boolean isBoundary = ScopeUtilities.isLastOfScope(graph, n1);

            // Then
            assertThat(isBoundary).isTrue();
        }

        @Test
        void shouldReturnTrueWhenOneSuccessorIsNotInAnyScope() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, n1);
            graph.add(n1, n2);
            choice1.addToScope(choice2);
            choice2.addToScope(n1);

            // When
            boolean isBoundary = ScopeUtilities.isLastOfScope(graph, n1);

            // Then
            assertThat(isBoundary).isTrue();
        }

        @Test
        void shouldReturnTrueWhenScopedDrawableWithoutChildren() {
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

    @Nested
    @DisplayName("Get Number of Nexted scopes tests")
    class GetNumberOfNestedScopes {

        @Test
        void shouldReturnZeroWhenNodeDoesNotBelongToAnyScope() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, n1);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, n1);

            // Then
            assertThat(scopesCount).isEqualTo(0);
        }

        @Test
        void shouldReturnOneWhenNodeBelongsToOneScope() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, n1);

            choice1.addToScope(n1);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, n1);

            // Then
            assertThat(scopesCount).isEqualTo(1);
        }

        @Test
        void shouldReturnTwoWhenNodeBelongsToTwoScopes() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, n1);

            choice1.addToScope(choice2);
            choice2.addToScope(n1);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, n1);

            // Then
            assertThat(scopesCount).isEqualTo(2);
        }

        @Test
        void shouldReturnOneWhenNodeIsScopedDrawable() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, choice1);

            // Then
            assertThat(scopesCount).isEqualTo(1);
        }

        @Test
        void shouldReturnTwoWhenNestedScopedDrawables() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            choice1.addToScope(choice2);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, choice2);

            // Then
            assertThat(scopesCount).isEqualTo(2);
        }

        @Test
        void shouldReturnThreeWhenNestedScopedDrawables() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, choice3);

            choice1.addToScope(choice2);
            choice2.addToScope(choice3);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, choice3);

            // Then
            assertThat(scopesCount).isEqualTo(3);
        }

        @Test
        void shouldReturnTwoWhenThreeNestedScopesAndNodeIsOutFromInnermost() {
            // Given
            FlowGraph graph = new FlowGraph();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, choice3);
            graph.add(choice3, n1);

            choice1.addToScope(choice2);
            choice2.addToScope(choice3);
            choice2.addToScope(n1);

            // When
            int scopesCount = ScopeUtilities.countNumberOfNestedScopes(graph, n1);

            // Then
            assertThat(scopesCount).isEqualTo(2);
        }
    }
}