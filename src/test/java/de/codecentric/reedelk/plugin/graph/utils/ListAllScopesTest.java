package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListAllScopesTest extends AbstractGraphTest {

    @Test
    void shouldListBeEmpty() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);
        graph.add(componentNode2, componentNode3);

        // When
        List<ScopedGraphNode> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).isEmpty();
    }

    @Test
    void shouldListContainTwoScopesWhenScopesAreDisjoint() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode2.addToScope(componentNode2);

        // When
        List<ScopedGraphNode> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).containsExactlyInAnyOrder(routerNode1, routerNode2);
    }

    @Test
    void shouldListContainThreeScopesWhenScopesAreNested() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, routerNode3);
        graph.add(routerNode3, componentNode3);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);

        routerNode2.addToScope(componentNode2);
        routerNode2.addToScope(routerNode3);

        routerNode3.addToScope(componentNode3);

        // When
        List<ScopedGraphNode> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).containsExactlyInAnyOrder(routerNode1, routerNode2, routerNode3);
    }

}