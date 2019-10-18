package com.reedelk.plugin.graph.action.add;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class FlowActionNodeAddNodeAfterRootTest extends BaseFlowActionNodeAddTest {

    @Test
    void shouldAddComponentAfterRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        root.setPosition(65, 150);

        // Drop the component next  to the root
        Point dropPoint = new Point(194, 150);

        // When
        FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, componentNode1, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(2)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode1);
    }

    @Test
    void shouldAddComponentBetweenRootAndSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode2);

        root.setPosition(65, 150);
        componentNode2.setPosition(195, 150);

        // Drop it right after root and before component 2
        Point dropPoint = new Point(148, 124);

        // When
        FlowGraphChangeAware modifiableGraph = addDrawableToGraph(graph, componentNode1, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(3)
                .root().is(root)
                .and().successorsOf(root).isOnly(componentNode1)
                .and().successorsOf(componentNode1).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isEmpty();
    }
}
