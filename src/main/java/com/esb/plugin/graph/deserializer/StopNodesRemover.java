package com.esb.plugin.graph.deserializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.drawable.StopGraphNode;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/*
 * This class removes from a given flow graph all the nodes having
 * type 'StopGraphNode' and bridges the incoming node to the successor
 * of the 'StopGraphNode' node.
 *
 * The graph does not display to the user 'Stop' nodes since they
 * are only used to build the graph.
 */
class StopNodesRemover {

    static FlowGraph from(FlowGraph originalGraph) {
        FlowGraph copy = originalGraph.copy();

        originalGraph.breadthFirstTraversal(currentDrawable -> {

            if (successorHasTypeStopDrawable(originalGraph, currentDrawable)) {

                List<GraphNode> successorsOfCurrent = copy.successors(currentDrawable);
                checkState(successorsOfCurrent.size() == 1, "Expected only one successor");

                GraphNode stop = successorsOfCurrent.get(0); // stop must have only one successor.

                List<GraphNode> successorsOfStop = copy.successors(stop);
                checkState(successorsOfStop.size() == 1, "Expected only one successor");

                GraphNode successorOfStop = successorsOfStop.get(0);

                // Connect current drawable with the next node after stop
                copy.add(currentDrawable, successorOfStop);

                // Remove edge between current drawable and stop
                copy.remove(currentDrawable, stop);
            }
        });

        // Remove all nodes of type stop:
        // all inbound/outbound edges have been removed above
        originalGraph.breadthFirstTraversal(drawable -> {
            if (drawable instanceof StopGraphNode) {
                copy.remove(drawable);
            }
        });

        return copy;
    }

    private static boolean successorHasTypeStopDrawable(FlowGraph graph, GraphNode drawable) {
        if (graph.successors(drawable).size() == 1) {
            return graph.successors(drawable)
                    .stream()
                    .anyMatch(drawable1 -> drawable1 instanceof StopGraphNode);
        }
        return false;
    }

}
