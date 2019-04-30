package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.ScopedDrawable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListAllScopesTest extends AbstractGraphTest {

    @Test
    void shouldListBeEmpty() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, n1);
        graph.add(n1, n2);
        graph.add(n2, n3);

        // When
        List<ScopedDrawable> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).isEmpty();
    }

    @Test
    void shouldListContainTwoScopesWhenScopesAreDisjoint() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice2.addToScope(n2);

        // When
        List<ScopedDrawable> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).containsExactlyInAnyOrder(choice1, choice2);
    }

    @Test
    void shouldListContainThreeScopesWhenScopesAreNested() {
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

        // When
        List<ScopedDrawable> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).containsExactlyInAnyOrder(choice1, choice2, choice3);
    }

}