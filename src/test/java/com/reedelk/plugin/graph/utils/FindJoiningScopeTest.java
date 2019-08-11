package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindJoiningScopeTest extends AbstractGraphTest {

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void shouldReturnCorrectJoiningScope() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, componentNode2);

        routerNode1.addToScope(componentNode1);

        // When
        Optional<ScopedGraphNode> joiningScope = FindJoiningScope.of(graph, componentNode2);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(routerNode1);
    }

    @Test
    void shouldReturnCorrectOuterJoiningScopeWhenTwoNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode1.setPosition(20, 90); // the outermost is the one with lowest X

        routerNode2.addToScope(componentNode2);
        routerNode2.setPosition(25, 90);

        // When
        Optional<ScopedGraphNode> joiningScope = FindJoiningScope.of(graph, componentNode3);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(routerNode1);
    }

    @Test
    void shouldReturnCorrectScopeWhenInBetweenTwoScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(componentNode1, routerNode2);
        graph.add(routerNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        routerNode1.addToScope(componentNode1);
        routerNode1.addToScope(routerNode2);
        routerNode1.addToScope(componentNode3);
        routerNode2.addToScope(componentNode2);

        // When
        Optional<ScopedGraphNode> joiningScope = FindJoiningScope.of(graph, componentNode3);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(routerNode2);
    }


}