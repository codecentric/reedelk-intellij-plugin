package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.AbstractGraphTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CountScopesBetweenTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroWhenDrawableIsInTheSameeScope() {
        // Given
        choice1.addToScope(n1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choice1, n1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneElementInsideNestedScope() {
        // Given
        choice1.addToScope(choice2);
        choice2.addToScope(n1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choice1, n1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);
    }

    @Test
    void shouldReturnZeroWhenSameScopedDrawableElementAsTarget() {
        // Given
        choice1.addToScope(n1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choice1, choice1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneNestedScope() {
        // Given
        choice1.addToScope(choice2);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choice1, choice2);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenTwoNestedScopes() {
        // Given
        choice1.addToScope(choice2);
        choice2.addToScope(choice3);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choice1, choice3);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(2);
    }

    @Test
    void shouldReturnCorrectScopesBetweenWhenMultipleLevels() {
        // Given
        choice1.addToScope(n1);
        choice1.addToScope(choice3);
        choice1.addToScope(choice2);
        choice1.addToScope(n3);

        choice2.addToScope(n5);
        choice2.addToScope(n6);
        choice2.addToScope(n7);

        choice3.addToScope(n2);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choice1, n3);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);

        // When
        actualScopesBetween = CountScopesBetween.them(choice1, n7);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);

    }

}