package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountMaxNestedScopesTest extends AbstractGraphTest {

    @Test
    void shouldReturnZeroNestedScopes() {
        // Given
        routerNode1.addToScope(componentNode1);

        // When
        int actual = CountMaxNestedScopes.of(routerNode1, componentNode1);

        // Then
        assertThat(actual).isEqualTo(0);
    }

    @Test
    void shouldReturnOneWhenOneElementInsideNestedScope() {
        // Given
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode1);

        // When
        int actual = CountMaxNestedScopes.of(routerNode1, componentNode1);

        // Then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void shouldReturnTwoWhenTwoNestedScopes() {
        // Given
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(routerNode3);

        // When
        int actual = CountMaxNestedScopes.of(routerNode1, routerNode3);

        // Then
        assertThat(actual).isEqualTo(2);
    }

    @Test
    void shouldReturnCorrectScopesWhenMultipleLevels() {
        // Given
        routerNode1.addToScope(routerNode3);
        routerNode1.addToScope(routerNode2);
        routerNode2.addToScope(componentNode5);
        routerNode3.addToScope(componentNode2);

        // When
        int actual = CountMaxNestedScopes.of(routerNode1, componentNode5);

        // Then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void shouldReturnCorrectScopesWhenMultipleLevelsWithDifferentScopesDepth() {
        // Given
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(routerNode3);
        routerNode2.addToScope(forkNode1);
        forkNode1.addToScope(forkNode2);

        // When
        int actual = CountMaxNestedScopes.of(routerNode1, forkNode1);

        // Then
        assertThat(actual).isEqualTo(3);
    }

    @Test
    void shouldReturnCorrectScopesWhenNestedScopeFollowedByNode() {
        // Given
        forkNode1.addToScope(forkNode2);
        forkNode1.addToScope(componentNode1);

        // When
        int actual = CountMaxNestedScopes.of(forkNode1, componentNode1);

        // Then
        assertThat(actual).isEqualTo(1);
    }
}