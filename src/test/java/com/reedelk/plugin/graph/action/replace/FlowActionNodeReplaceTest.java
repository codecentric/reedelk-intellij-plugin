package com.reedelk.plugin.graph.action.replace;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import org.junit.jupiter.api.Test;

class FlowActionNodeReplaceTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyReplaceNodeInBetweenNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        FlowActionNodeReplace action = new FlowActionNodeReplace(componentNode1, placeholderNode);
        action.execute(modifiableGraph);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(3)
                .root().is(root).and()
                .successorsOf(root).isOnly(placeholderNode).and()
                .successorsOf(placeholderNode).isOnly(componentNode2).and()
                .successorsOf(componentNode2).isEmpty();
    }

    @Test
    void shouldCorrectlyReplaceRootNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        // When
        FlowActionNodeReplace action = new FlowActionNodeReplace(root, placeholderNode);
        action.execute(modifiableGraph);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(2)
                .root().is(placeholderNode).and()
                .successorsOf(placeholderNode).isOnly(componentNode1).and()
                .successorsOf(componentNode1).isEmpty();
    }
}