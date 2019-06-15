package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CountScopesBetweenTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroWhenDrawableIsInTheSameeScope() {
        // Given
        routerNode1.addToScope(componentNode1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(routerNode1, componentNode1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneElementInsideNestedScope() {
        // Given
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(routerNode1, componentNode1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);
    }

    @Test
    void shouldReturnZeroWhenSameScopedDrawableElementAsTarget() {
        // Given
        routerNode1.addToScope(componentNode1);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(routerNode1, routerNode1);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneNestedScope() {
        // Given
        routerNode1.addToScope(routerNode2);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(routerNode1, routerNode2);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenTwoNestedScopes() {
        // Given
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(routerNode3);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(routerNode1, routerNode3);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(2);
    }

    @Test
    void shouldReturnCorrectScopesBetweenWhenMultipleLevels() {
        // Given
        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode3);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode3);

        routerNode2.addToScope(componentNode5);
        routerNode2.addToScope(componentNode6);
        routerNode2.addToScope(componentNode7);

        routerNode3.addToScope(componentNode2);

        // When
        Optional<Integer> actualScopesBetween = CountScopesBetween.them(routerNode1, componentNode3);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(0);

        // When
        actualScopesBetween = CountScopesBetween.them(routerNode1, componentNode7);

        // Then
        assertThat(actualScopesBetween.get()).isEqualTo(1);

    }

}