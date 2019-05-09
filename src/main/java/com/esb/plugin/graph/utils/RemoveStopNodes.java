package com.esb.plugin.graph.utils;

import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.esb.plugin.graph.utils.GraphNodeFilters.ScopedGraphNodes;
import static com.esb.plugin.graph.utils.GraphNodeFilters.StopNodes;
import static com.google.common.base.Preconditions.checkState;

public class RemoveStopNodes {

    /**
     * This class removes from a given flow graph all the nodes having
     * type 'StopNode' and bridges the incoming node to the successor
     * of the 'StopNode' node.
     * <p>
     * The graph does not display to the user 'Stop' nodes since they
     * are only used to build the graph.
     *
     * @param graph the graph from which we want to remove stop nodes.
     * @return the original graph without stop nodes. Preceding nodes of stop nodes
     * are connected to the successors of the removed stop nodes.
     */
    public static FlowGraph from(FlowGraph graph) {
        FlowGraph copy = graph.copy();

        graph.breadthFirstTraversal(currentNode ->

                getSuccessorStopNode(graph, currentNode).ifPresent(stopNode ->

                        getFirstNonStopSuccessor(copy, stopNode).ifPresent(nonStopSuccessor -> {

                            // Connect current node with the next node after stop
                            copy.add(currentNode, nonStopSuccessor);

                            // Remove edge between current node and stop
                            copy.remove(currentNode, stopNode);

                        })));

        Collection<StopNode> stopNodes = StopNodes.from(graph.nodes());

        // Remove all nodes of type stop:
        // all inbound/outbound edges have been removed above
        stopNodes.forEach(copy::remove);

        ScopedGraphNodes.from(graph.nodes())
                .forEach(scopedGraphNode ->
                        stopNodes.forEach(scopedGraphNode::removeFromScope));

        return copy;
    }

    private static Optional<GraphNode> getFirstNonStopSuccessor(FlowGraph graph, StopNode stop) {
        List<GraphNode> successors = graph.successors(stop);
        checkState(successors.isEmpty() || successors.size() == 1, "Expected only zero or one successor");
        if (successors.isEmpty()) {
            return Optional.empty();
        } else {
            GraphNode successor = successors.get(0);
            return successor instanceof StopNode ?
                    getFirstNonStopSuccessor(graph, (StopNode) successor) :
                    Optional.of(successor);
        }
    }

    private static Optional<StopNode> getSuccessorStopNode(FlowGraph graph, GraphNode node) {
        return StopNodes.from(graph.successors(node)).stream().findFirst();
    }

}
