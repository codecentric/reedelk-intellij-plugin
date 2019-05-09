package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CountScopesBetweenTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroWhenDrawableIsInTheSameeScope() {
        // Given
        choiceNode1.addToScope(componentNode1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choiceNode1, componentNode1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneElementInsideNestedScope() {
        // Given
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(componentNode1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choiceNode1, componentNode1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);
    }

    @Test
    void shouldReturnZeroWhenSameScopedDrawableElementAsTarget() {
        // Given
        choiceNode1.addToScope(componentNode1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choiceNode1, choiceNode1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneNestedScope() {
        // Given
        choiceNode1.addToScope(choiceNode2);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choiceNode1, choiceNode2);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenTwoNestedScopes() {
        // Given
        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(choiceNode3);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choiceNode1, choiceNode3);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(2);
    }

    @Test
    void shouldReturnCorrectScopesBetweenWhenMultipleLevels() {
        // Given
        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode3);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);

        choiceNode2.addToScope(componentNode5);
        choiceNode2.addToScope(componentNode6);
        choiceNode2.addToScope(componentNode7);

        choiceNode3.addToScope(componentNode2);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(choiceNode1, componentNode3);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);

        // When
        actualScopesBetween = CountScopesBetween.them(choiceNode1, componentNode7);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);

    }

}