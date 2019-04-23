package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ListLastNodeOfScopeTest extends AbstractGraphTest {

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
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choice2);

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
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choice1);

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
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choice1);

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
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choice1);

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
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choice1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(n1, n2, n3);
    }

    @Test
    void shouldReturnCorrectlyLastDrawableOfScopeWhenMultipleLevelScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(choice1, choice2);
        graph.add(n1, choice3);
        graph.add(choice3, n2);
        graph.add(n2, n3);
        graph.add(n3, n4);
        graph.add(choice2, n5);
        graph.add(n5, n6);
        graph.add(n6, n7);
        graph.add(n7, n4);

        choice1.addToScope(n1);
        choice1.addToScope(n3);
        choice1.addToScope(choice2);
        choice1.addToScope(choice3);

        choice2.addToScope(n5);
        choice2.addToScope(n6);
        choice2.addToScope(n7);

        choice3.addToScope(n2);

        // When
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choice1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(n3, n7);
    }

}