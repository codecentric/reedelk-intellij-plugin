package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;
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
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode2.addToScope(componentNode2);

        // When
        List<ScopedGraphNode> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).containsExactlyInAnyOrder(choiceNode1, choiceNode2);
    }

    @Test
    void shouldListContainThreeScopesWhenScopesAreNested() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, choiceNode3);
        graph.add(choiceNode3, componentNode3);
        graph.add(componentNode3, componentNode4);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);

        choiceNode2.addToScope(componentNode2);
        choiceNode2.addToScope(choiceNode3);

        choiceNode3.addToScope(componentNode3);

        // When
        List<ScopedGraphNode> allScopes = ListAllScopes.of(graph);

        // Then
        assertThat(allScopes).containsExactlyInAnyOrder(choiceNode1, choiceNode2, choiceNode3);
    }

}