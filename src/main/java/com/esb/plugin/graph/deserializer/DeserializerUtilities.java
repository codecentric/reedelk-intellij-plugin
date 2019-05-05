package com.esb.plugin.graph.deserializer;

import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

class DeserializerUtilities {

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
    static FlowGraph removeStopNodesFrom(FlowGraph graph) {
        FlowGraph copy = graph.copy();

        graph.breadthFirstTraversal(currentDrawable -> {

            if (successorHasTypeStopDrawable(graph, currentDrawable)) {

                List<GraphNode> successorsOfCurrent = copy.successors(currentDrawable);
                checkState(successorsOfCurrent.size() == 1, "Expected only one successor");

                GraphNode stop = successorsOfCurrent.get(0); // stop must have only one successor.

                List<GraphNode> successorsOfStop = copy.successors(stop);
                checkState(successorsOfStop.size() == 1, "Expected only one successor");

                GraphNode successorOfStop = successorsOfStop.get(0);

                // Connect current node with the next node after stop
                copy.add(currentDrawable, successorOfStop);

                // Remove edge between current node and stop
                copy.remove(currentDrawable, stop);
            }
        });

        // Remove all nodes of type stop:
        // all inbound/outbound edges have been removed above
        graph.breadthFirstTraversal(drawable -> {
            if (drawable instanceof StopNode) {
                copy.remove(drawable);
            }
        });

        return copy;
    }

    private static boolean successorHasTypeStopDrawable(FlowGraph graph, GraphNode drawable) {
        if (graph.successors(drawable).size() == 1) {
            return graph.successors(drawable)
                    .stream()
                    .anyMatch(drawable1 -> drawable1 instanceof StopNode);
        }
        return false;
    }

}