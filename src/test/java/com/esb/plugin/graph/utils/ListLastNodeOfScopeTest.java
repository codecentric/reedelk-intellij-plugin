package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class ListLastNodeOfScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnCorrectlyLastDrawablesFromInnerScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode2.addToScope(componentNode1);
        choiceNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choiceNode2);

        // Then
        assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(componentNode1, componentNode2);
    }

    @Test
    void shouldReturnCorrectlyLastDrawablesFromOuterScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, componentNode1);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode1, componentNode3);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode2.addToScope(componentNode1);
        choiceNode2.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactly(componentNode3);
    }

    @Test
    void shouldReturnCorrectlyLastDrawablesWhenInnerDrawableIsScopedDrawable() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        choiceNode1.addToScope(choiceNode2);

        // When
        Collection<GraphNode> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactly(choiceNode2);
    }

    @Test
    void shouldReturnCorrectlyLastDrawableOfScopeWhenThreeNestedScopeDrawables() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode2, choiceNode3);

        choiceNode1.addToScope(choiceNode2);
        choiceNode2.addToScope(choiceNode3);

        // When
        Collection<GraphNode> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactly(choiceNode3);
    }

    @Test
    void shouldReturnCorrectlyLastDrawableOfScopeWhenNestedContainsNodes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(choiceNode1, componentNode2);
        graph.add(choiceNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode2);
        choiceNode2.addToScope(componentNode3);

        // When
        Collection<GraphNode> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(componentNode1, componentNode2, componentNode3);
    }

    @Test
    void shouldReturnCorrectlyLastDrawableOfScopeWhenMultipleLevelScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(choiceNode1, choiceNode2);
        graph.add(componentNode1, choiceNode3);
        graph.add(choiceNode3, componentNode2);
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode3, componentNode4);
        graph.add(choiceNode2, componentNode5);
        graph.add(componentNode5, componentNode6);
        graph.add(componentNode6, componentNode7);
        graph.add(componentNode7, componentNode4);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(componentNode3);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(choiceNode3);

        choiceNode2.addToScope(componentNode5);
        choiceNode2.addToScope(componentNode6);
        choiceNode2.addToScope(componentNode7);

        choiceNode3.addToScope(componentNode2);

        // When
        Collection<GraphNode> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, choiceNode1);

        // Then
        assertThat(lastDrawablesOfScope).containsExactlyInAnyOrder(componentNode3, componentNode7);
    }

}