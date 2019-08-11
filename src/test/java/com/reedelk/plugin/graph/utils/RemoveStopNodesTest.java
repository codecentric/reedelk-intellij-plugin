package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import org.junit.jupiter.api.Test;

class RemoveStopNodesTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyRemoveStopNodesWhenTwoNestedScopeNodes() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(componentNode1);
        graph.add(componentNode1, routerNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, flowReferenceNode1);
        graph.add(routerNode1, forkNode1);
        graph.add(componentNode2, stopNode1);
        graph.add(flowReferenceNode1, stopNode1);

        graph.add(forkNode1, componentNode3);
        graph.add(forkNode1, componentNode4);
        graph.add(componentNode3, stopNode2);
        graph.add(componentNode4, stopNode2);

        graph.add(stopNode2, stopNode1);

        graph.add(stopNode1, componentNode5);

        routerNode1.addToScope(componentNode2);
        routerNode1.addToScope(flowReferenceNode1);
        routerNode1.addToScope(forkNode1);

        forkNode1.addToScope(componentNode3);
        forkNode1.addToScope(componentNode4);

        // When
        FlowGraph graphWithoutStopNodes = RemoveStopNodes.from(graph);

        // Then
        PluginAssertion.assertThat(graphWithoutStopNodes)
                .successorsOf(componentNode3).isOnly(componentNode5)
                .and().successorsOf(componentNode4).isOnly(componentNode5)
                .and().successorsOf(componentNode2).isOnly(componentNode5)
                .and().successorsOf(flowReferenceNode1).isOnly(componentNode5);
    }
}
