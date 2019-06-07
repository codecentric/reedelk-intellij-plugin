package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;
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
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, componentNode2);

        choiceNode1.addToScope(componentNode1);

        // When
        Optional<ScopedGraphNode> joiningScope = FindJoiningScope.of(graph, componentNode2);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(choiceNode1);
    }

    @Test
    void shouldReturnCorrectOuterJoiningScopeWhenTwoNestedScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.setPosition(20, 90); // the outermost is the one with lowest X

        choiceNode2.addToScope(componentNode2);
        choiceNode2.setPosition(25, 90);

        // When
        Optional<ScopedGraphNode> joiningScope = FindJoiningScope.of(graph, componentNode3);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(choiceNode1);
    }

    @Test
    void shouldReturnCorrectScopeWhenInBetweenTwoScopes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, choiceNode1);
        graph.add(choiceNode1, componentNode1);
        graph.add(componentNode1, choiceNode2);
        graph.add(choiceNode2, componentNode2);
        graph.add(componentNode2, componentNode3);

        choiceNode1.addToScope(componentNode1);
        choiceNode1.addToScope(choiceNode2);
        choiceNode1.addToScope(componentNode3);
        choiceNode2.addToScope(componentNode2);

        // When
        Optional<ScopedGraphNode> joiningScope = FindJoiningScope.of(graph, componentNode3);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(choiceNode2);
    }


}