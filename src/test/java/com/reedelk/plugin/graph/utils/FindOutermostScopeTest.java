package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindOutermostScopeTest extends AbstractGraphTest {

    private FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forkNode1);
        graph.add(forkNode1, forkNode2);
        graph.add(forkNode2, forkNode3);
        graph.add(forkNode2, componentNode1);
        graph.add(forkNode3, componentNode2);

        // Component node 3 is out of any scope, it is the joining node.
        graph.add(componentNode2, componentNode3);
        graph.add(componentNode1, componentNode3);

        forkNode1.addToScope(forkNode2);
        forkNode2.addToScope(forkNode3);
        forkNode2.addToScope(componentNode1);
        forkNode3.addToScope(componentNode2);
    }


    @Test
    void shouldFindCorrectOutermostScope() {
        // When
        Optional<ScopedGraphNode> maybeScopedGraphNode =
                FindOutermostScope.of(graph, Arrays.asList(componentNode1, componentNode2));

        // Then
        assertThat(maybeScopedGraphNode).contains(forkNode1);
    }

    @Test
    void shouldFindCorrectOutermostScopeInverted() {
        // When
        Optional<ScopedGraphNode> maybeScopedGraphNode =
                FindOutermostScope.of(graph, Arrays.asList(componentNode2, componentNode1));

        // Then
        assertThat(maybeScopedGraphNode).contains(forkNode1);
    }

    @Test
    void shouldReturnEmptyWhenNoNodes() {
        // When
        Optional<ScopedGraphNode> maybeScopedGraphNode =
                FindOutermostScope.of(graph, Collections.emptyList());

        // Then
        assertThat(maybeScopedGraphNode).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenTargetDoesNotBelongToAnyScope() {
        // When
        Optional<ScopedGraphNode> maybeScopedGraphNode =
                FindOutermostScope.of(graph, Collections.singletonList(componentNode3));

        // Then
        assertThat(maybeScopedGraphNode).isEmpty();
    }
}
