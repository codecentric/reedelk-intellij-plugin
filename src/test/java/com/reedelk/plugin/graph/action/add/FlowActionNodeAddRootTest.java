package com.reedelk.plugin.graph.action.add;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.type.generic.GenericComponentNode;
import com.reedelk.plugin.fixture.ComponentRoot;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static com.reedelk.plugin.component.domain.ComponentClass.INBOUND;

class FlowActionNodeAddRootTest extends BaseFlowActionNodeAddTest {

    private GraphNode rootReplacement;

    @BeforeEach
    public void setUp() {
        super.setUp();
        rootReplacement = createGraphNodeInstance(ComponentRoot.class, GenericComponentNode.class, INBOUND);
    }

    @Test
    void shouldCorrectlyAddRootComponent() {
        // Given
        FlowGraph graph = provider.createGraph();
        Point dropPoint = new Point(20, 20);

        // When
        FlowGraphChangeAware modifiableGraph = addNodeToGraph(graph, root, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(1)
                .root().is(root)
                .and().successorsOf(root).isEmpty();
    }

    @Test
    void shouldCorrectlyReplaceRootComponentWhenXCoordinateIsSmallerThanCurrentRoot() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        root.setPosition(20, 20);

        Point dropPoint = new Point(10, 20); // x drop point smaller than the root x coordinate.

        // When
        FlowGraphChangeAware modifiableGraph = addNodeToGraph(graph, rootReplacement, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(1)
                .root().is(rootReplacement)
                .and().successorsOf(rootReplacement).isEmpty();
    }

    @Test
    void shouldReplaceRootWhenDroppedComponentOverlapsRootNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);

        root.setPosition(65, 150);
        componentNode1.setPosition(195, 150);

        // Drop the root component on top of the existing root
        Point dropPoint = new Point(56, 129);

        // When
        FlowGraphChangeAware modifiableGraph = addNodeToGraph(graph, rootReplacement, dropPoint);

        // Then
        PluginAssertion.assertThat(modifiableGraph)
                .isChanged()
                .nodesCountIs(2)
                .root().is(rootReplacement)
                .and().successorsOf(rootReplacement).isOnly(componentNode1);
    }
}
