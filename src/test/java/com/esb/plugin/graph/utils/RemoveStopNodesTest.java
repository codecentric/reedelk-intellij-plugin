package com.esb.plugin.graph.utils;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import org.junit.jupiter.api.Test;

class RemoveStopNodesTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyRemoveStopNodesWhenTwoNestedScopeNodes() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.root(componentNode1);
        graph.add(componentNode1, choiceNode1);
        graph.add(choiceNode1, componentNode2);
        graph.add(choiceNode1, flowReferenceNode1);
        graph.add(choiceNode1, forkNode1);
        graph.add(componentNode2, stopNode1);
        graph.add(flowReferenceNode1, stopNode1);

        graph.add(forkNode1, componentNode3);
        graph.add(forkNode1, componentNode4);
        graph.add(componentNode3, stopNode2);
        graph.add(componentNode4, stopNode2);

        graph.add(stopNode2, stopNode1);

        graph.add(stopNode1, componentNode5);

        choiceNode1.addToScope(componentNode2);
        choiceNode1.addToScope(flowReferenceNode1);
        choiceNode1.addToScope(forkNode1);

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
