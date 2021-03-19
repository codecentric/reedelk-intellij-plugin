package de.codecentric.reedelk.plugin.assertion.graph;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SuccessorNodeAssertion {

    private final GraphNode node;
    private final FlowGraph graph;
    private final FlowGraphAssertion parent;

    SuccessorNodeAssertion(FlowGraph graph, GraphNode node, FlowGraphAssertion parent) {
        this.parent = parent;
        this.graph = graph;
        this.node = node;
    }

    public SuccessorNodeAssertion areExactly(GraphNode... expectedNodes) {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).containsExactlyInAnyOrder(expectedNodes);
        return this;
    }

    public SuccessorNodeAssertion isAtIndex(GraphNode expectedNode, int expectedIndex) {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).contains(expectedNode);

        for (int i = 0; i < successors.size(); i++) {
            GraphNode node = successors.get(i);
            if (node == expectedNode) {
                if (i == expectedIndex) {
                    return this;
                }
            }
        }
        fail(format("Expected node %s could not be found at expected index %d", expectedNode, expectedIndex));
        return this;
    }

    public SuccessorNodeAssertion isOnly(GraphNode expectedNode) {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).containsExactly(expectedNode);
        return this;
    }

    public SuccessorNodeAssertion isEmpty() {
        List<GraphNode> successors = graph.successors(node);
        assertThat(successors).isEmpty();
        return this;
    }

    public FlowGraphAssertion and() {
        return parent;
    }

}
