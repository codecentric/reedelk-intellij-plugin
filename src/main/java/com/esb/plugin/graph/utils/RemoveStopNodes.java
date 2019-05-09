package com.esb.plugin.graph.utils;

import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

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

        graph.breadthFirstTraversal(currentNode -> {

            if (isSuccessorStopNode(graph, currentNode)) {

                List<GraphNode> successorsOfCurrent = copy.successors(currentNode);
                checkState(successorsOfCurrent.size() == 1, "Expected only one successor");

                GraphNode stop = successorsOfCurrent.get(0); // stop must have only one successor.

                GraphNode successorOfStop = findFirstNonStopSuccessor(copy, (StopNode) stop);

                if (successorOfStop != null) {
                    // Connect current node with the next node after stop
                    copy.add(currentNode, successorOfStop);

                    // Remove edge between current node and stop
                    copy.remove(currentNode, stop);
                }
            }
        });

        Collection<GraphNode> stopNodes = graph
                .nodes()
                .stream()
                .filter(node -> node instanceof StopNode)
                .collect(toList());

        // Remove all nodes of type stop:
        // all inbound/outbound edges have been removed above
        stopNodes.forEach(copy::remove);


        graph.nodes()
                .stream()
                .filter(node -> node instanceof ScopedGraphNode)
                .map(node -> (ScopedGraphNode) node)
                .forEach(scopedGraphNode -> stopNodes.forEach(scopedGraphNode::removeFromScope));

        return copy;
    }

    private static boolean isSuccessorStopNode(FlowGraph graph, GraphNode node) {
        if (graph.successors(node).size() == 1) {
            return graph.successors(node)
                    .stream()
                    .anyMatch(n -> n instanceof StopNode);
        }
        return false;
    }

    private static GraphNode findFirstNonStopSuccessor(FlowGraph graph, StopNode stop) {
        List<GraphNode> successors = graph.successors(stop);
        checkState(successors.isEmpty() || successors.size() == 1,
                "Expected only zero or one successor");
        if (successors.isEmpty()) return null;

        GraphNode successor = successors.get(0);
        if (successor instanceof StopNode) {
            return findFirstNonStopSuccessor(graph, (StopNode) successor);
        } else {
            return successor;
        }
    }
}
