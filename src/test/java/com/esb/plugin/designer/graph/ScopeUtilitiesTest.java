package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

class ScopeUtilitiesTest extends AbstractGraphTest {

    @Nested
    @DisplayName("Find Scope Tests")
    class FindScope {

        @Test
        void shouldReturnCorrectScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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
            FlowGraph graph = new FlowGraphImpl();
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


    @Nested
    @DisplayName("Get Number of scopes between")
    class ScopesBetween {

        @Test
        void shouldReturnZeroWhenDrawableIsInTheSameeScope() {
            // Given
            choice1.addToScope(n1);

            // When
            Optional<Integer> actualScopesBetween = ScopeUtilities.scopesBetween(choice1, n1);

            // Then
            assertThat(actualScopesBetween.get()).isEqualTo(0);
        }

        @Test
        void shouldReturnOneWhenOneElementInsideNestedScope() {
            // Given
            choice1.addToScope(choice2);
            choice2.addToScope(n1);

            // When
            Optional<Integer> actualScopesBetween = ScopeUtilities.scopesBetween(choice1, n1);

            // Then
            assertThat(actualScopesBetween.get()).isEqualTo(1);
        }

        @Test
        void shouldReturnZeroWhenSameScopedDrawableElementAsTarget() {
            // Given
            choice1.addToScope(n1);

            // When
            Optional<Integer> actualScopesBetween = ScopeUtilities.scopesBetween(choice1, choice1);

            // Then
            assertThat(actualScopesBetween.get()).isEqualTo(0);
        }

        @Test
        void shouldReturnOneWhenOneNestedScope() {
            // Given
            choice1.addToScope(choice2);

            // When
            Optional<Integer> actualScopesBetween = ScopeUtilities.scopesBetween(choice1, choice2);

            // Then
            assertThat(actualScopesBetween.get()).isEqualTo(1);
        }

        @Test
        void shouldReturnTwoWhenTwoNestedScopes() {
            // Given
            choice1.addToScope(choice2);
            choice2.addToScope(choice3);

            // When
            Optional<Integer> actualScopesBetween = ScopeUtilities.scopesBetween(choice1, choice3);

            // Then
            assertThat(actualScopesBetween.get()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("List Last Drawables Of Scope Tests")
    class ListLastDrawablesOfScope {

        @Test
        void shouldReturnCorrectlyLastDrawablesFromInnerScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, n1);
            graph.add(choice2, n2);
            graph.add(n1, n3);
            graph.add(n2, n3);

            choice1.addToScope(choice2);
            choice1.addToScope(n3);
            choice2.addToScope(n1);
            choice2.addToScope(n2);

            // When
            Collection<Drawable> lastDrawablesOfScope = ScopeUtilities.listLastDrawablesOfScope(graph, choice2);

            // Then
            assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(n1, n2);
        }

        @Test
        void shouldReturnCorrectlyLastDrawablesFromOuterScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, n1);
            graph.add(choice2, n2);
            graph.add(n1, n3);
            graph.add(n2, n3);

            choice1.addToScope(choice2);
            choice1.addToScope(n3);
            choice2.addToScope(n1);
            choice2.addToScope(n2);

            // When
            Collection<Drawable> lastDrawablesOfScope = ScopeUtilities.listLastDrawablesOfScope(graph, choice1);

            // Then
            assertThat(lastDrawablesOfScope).containsExactly(n3);
        }

        @Test
        void shouldReturnCorrectlyLastDrawablesWhenInnerDrawableIsScopedDrawable() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            choice1.addToScope(choice2);

            // When
            Collection<Drawable> lastDrawablesOfScope = ScopeUtilities.listLastDrawablesOfScope(graph, choice1);

            // Then
            assertThat(lastDrawablesOfScope).containsExactly(choice2);
        }

        @Test
        void shouldReturnCorrectlyLastDrawableOfScopeWhenThreeNestedScopeDrawables() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.add(null, root);
            graph.add(root, choice1);
            graph.add(choice1, choice2);
            graph.add(choice2, choice3);

            choice1.addToScope(choice2);
            choice2.addToScope(choice3);

            // When
            Collection<Drawable> lastDrawablesOfScope = ScopeUtilities.listLastDrawablesOfScope(graph, choice1);

            // Then
            assertThat(lastDrawablesOfScope).containsExactly(choice3);
        }

        @Test
        void shouldReturnCorrectlyLastDrawableOfScopeWhenNestedContainsNodes() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(choice1, choice2);
            graph.add(choice1, n2);
            graph.add(choice2, n3);

            choice1.addToScope(n1);
            choice1.addToScope(choice2);
            choice1.addToScope(n2);
            choice2.addToScope(n3);

            // When
            Collection<Drawable> lastDrawablesOfScope = ScopeUtilities.listLastDrawablesOfScope(graph, choice1);

            // Then
            assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(n1, n2, n3);
        }
    }

    @Nested
    @DisplayName("Find First Node Outside Scope Tests")
    class FindFirstOutsideScope {

        @Test
        void shouldCorrectlyReturnEmptyWhenTwoLevelsAndOneContainsANestedScopeWithoutSuccessorsOutsideScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(choice1, n3);

            choice1.addToScope(n1);
            choice1.addToScope(n3);
            choice1.addToScope(choice2);

            choice2.addToScope(n2);

            // When
            Collection<Drawable> drawables = ScopeUtilities
                    .listFirstDrawablesOutsideScope(graph, choice1);

            // Then
            assertThat(drawables).isEmpty();
        }
    }

    @Nested
    @DisplayName("Max Scope X Bound Tests")
    class MaxScopeXBound {

        @Test
        void shouldCorrectlyReturnMaxScopeBoundWhenNestedScopes() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(choice1, n3);

            choice1.addToScope(n1);
            choice1.addToScope(n3);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);

            root.setPosition(55, 140);
            choice1.setPosition(165, 140);
            choice2.setPosition(390, 75);
            n1.setPosition(275, 75);
            n2.setPosition(505, 75);
            n3.setPosition(275, 210);

            // When
            int maxScopeXBound = ScopeUtilities.getMaxScopeXBound(graph, choice1);

            // Then
            // Max should be drawable with max x + half drawable's width
            assertThat(maxScopeXBound).isEqualTo(560);
        }

        @Test
        void shouldCorrectlyReturnMaxScopeBoundWhenScopeDoesNotContainAnyDrawable() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);

            root.setPosition(55, 140);
            choice1.setPosition(165, 140);

            // When
            int maxScopeXBound = ScopeUtilities.getMaxScopeXBound(graph, choice1);

            // Then
            assertThat(maxScopeXBound).isEqualTo(220);
        }

        @Test
        void shouldCorrectlyReturnMaxScopeBoundWhenScopeContainsOnlyOneElement() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);

            root.setPosition(55, 140);
            choice1.setPosition(165, 140);
            n1.setPosition(275, 140);

            choice1.addToScope(n1);

            // When
            int maxScopeXBound = ScopeUtilities.getMaxScopeXBound(graph, choice1);

            // Then
            assertThat(maxScopeXBound).isEqualTo(330);
        }
    }

    @Nested
    @DisplayName("Find scopes for node tests")
    class FindScopesForNode {

        @Test
        void shouldCorrectlyReturnStackForTwoNestedScopes() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n3);

            choice1.addToScope(n1);
            choice1.addToScope(choice2);
            choice2.addToScope(n3);

            // When
            Stack<ScopedDrawable> scopes = ScopeUtilities.findTargetScopes(graph, n3);

            // Then
            assertThat(scopes.pop()).isEqualTo(choice2); // innermost is choice 2
            assertThat(scopes.pop()).isEqualTo(choice1); // outermost is choice 1
            assertThat(scopes).isEmpty();
        }

        @Test
        void shouldCorrectlyReturnStackForThreeNestedScopes() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(n2, choice3);
            graph.add(choice3, n3);
            graph.add(n3, n4);

            choice1.addToScope(n1);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);
            choice2.addToScope(choice3);
            choice3.addToScope(n3);
            choice3.addToScope(n4);

            // When
            Stack<ScopedDrawable> scopes = ScopeUtilities.findTargetScopes(graph, n4);

            // Then
            assertThat(scopes.pop()).isEqualTo(choice3);
            assertThat(scopes.pop()).isEqualTo(choice2);
            assertThat(scopes.pop()).isEqualTo(choice1);
            assertThat(scopes).isEmpty();
        }

        @Test
        void shouldCorrectlyReturnEmptyStack() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, n1);

            // When
            Stack<ScopedDrawable> scopes = ScopeUtilities.findTargetScopes(graph, n1);

            // Then
            assertThat(scopes).isEmpty();
        }

        @Test
        void shouldCorrectlyReturnEmptyStackForRoot() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);

            // When
            Stack<ScopedDrawable> scopes = ScopeUtilities.findTargetScopes(graph, root);

            // Then
            assertThat(scopes).isEmpty();
        }

        @Test
        void shouldCorrectReturnScopeWhenElementRightOutsideInnermostScope() {
            // Given
            FlowGraph graph = new FlowGraphImpl();
            graph.root(root);
            graph.add(root, choice1);
            graph.add(choice1, n1);
            graph.add(n1, choice2);
            graph.add(choice2, n2);
            graph.add(n2, n3);

            choice1.addToScope(n1);
            choice1.addToScope(choice2);
            choice2.addToScope(n2);
            choice1.addToScope(n3);

            // When
            Stack<ScopedDrawable> scopes = ScopeUtilities.findTargetScopes(graph, n3);

            // Then
            assertThat(scopes.pop()).isEqualTo(choice1); // scope is only choice 1
            assertThat(scopes).isEmpty();
        }
    }
}