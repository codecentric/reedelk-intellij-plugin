package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.AbstractGraphTest;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.ScopedNode;
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
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, n2);

        choice1.addToScope(n1);

        // When
        Optional<ScopedNode> joiningScope = FindJoiningScope.of(graph, n2);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(choice1);
    }

    @Test
    void shouldReturnCorrectOuterJoiningScopeWhenTwoNestedScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice1.setPosition(20, 90); // the outermost is the one with lowest X

        choice2.addToScope(n2);
        choice2.setPosition(25, 90);

        // When
        Optional<ScopedNode> joiningScope = FindJoiningScope.of(graph, n3);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(choice1);
    }

    @Test
    void shouldReturnCorrectScopeWhenInBetweenTwoScopes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(root);
        graph.add(root, choice1);
        graph.add(choice1, n1);
        graph.add(n1, choice2);
        graph.add(choice2, n2);
        graph.add(n2, n3);

        choice1.addToScope(n1);
        choice1.addToScope(choice2);
        choice1.addToScope(n3);
        choice2.addToScope(n2);

        // When
        Optional<ScopedNode> joiningScope = FindJoiningScope.of(graph, n3);

        // Then
        assertThat(joiningScope.isPresent()).isTrue();
        assertThat(joiningScope.get()).isEqualTo(choice2);
    }


}