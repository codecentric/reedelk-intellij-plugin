package de.codecentric.reedelk.plugin.component.type.flowreference.discovery;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SubflowReferenceGraphAwareContextTest extends AbstractGraphTest {

    private SubflowReferenceGraphAwareContext context;
    private FlowGraph subflowGraph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        subflowGraph = provider.createGraph();
        subflowGraph.root(root);
        subflowGraph.add(root, componentNode1);
        subflowGraph.add(componentNode1, componentNode2);
        subflowGraph.add(componentNode2, forkNode1);
        subflowGraph.add(forkNode1, componentNode3);
        subflowGraph.add(forkNode1, componentNode4);

        forkNode1.addToScope(componentNode3);
        forkNode1.addToScope(componentNode4);

        context = new SubflowReferenceGraphAwareContext(subflowGraph, flowReferenceNode1);
    }

    @Test
    void shouldReturnEndNodesOfSubflowGraphWhenTargetIsFlowReference() {
        // When
        List<GraphNode> predecessors = context.predecessors(flowReferenceNode1);

        // Then
        assertThat(predecessors).containsExactlyInAnyOrder(componentNode3, componentNode4);
    }

    @Test
    void shouldReturnCorrectPredecessorOfSubflowGraphNode() {
        // When
        List<GraphNode> predecessors = context.predecessors(componentNode2);

        // Then
        assertThat(predecessors).contains(componentNode1);
    }

    @Test
    void shouldReturnCorrectJoiningScopeWhenTargetIsFlowReference() {
        // When
        Optional<ScopedGraphNode> scopedGraphNode = context.joiningScopeOf(flowReferenceNode1);

        // Then
        assertThat(scopedGraphNode).contains(forkNode1);
    }
}
