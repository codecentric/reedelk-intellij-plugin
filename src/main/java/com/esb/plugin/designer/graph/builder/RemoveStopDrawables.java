package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.StopDrawable;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/*
 * This class removes from a given flow graph all the nodes having
 * type 'StopDrawable' and bridges the incoming node to the successor
 * of the 'StopDrawable' node.
 *
 * The graph does not display to the user 'Stop' nodes since they
 * are only used to build the graph.
 */
class RemoveStopDrawables {

    static FlowGraph from(FlowGraph originalGraph) {
        FlowGraph copy = originalGraph.copy();

        originalGraph.breadthFirstTraversal(currentDrawable -> {
            if (successorHasTypeStopDrawable(originalGraph, currentDrawable)) {

                List<Drawable> successorsOfCurrent = copy.successors(currentDrawable);
                checkState(successorsOfCurrent.size() == 1, "Expected only one successor");

                Drawable stop = successorsOfCurrent.get(0); // stop must have only one successor.

                List<Drawable> successorsOfStop = copy.successors(stop);
                checkState(successorsOfStop.size() == 1, "Expected only one successor");
                Drawable successorOfStop = successorsOfStop.get(0);

                // Connect current drawable with the next node after stop
                copy.add(currentDrawable, successorOfStop);

                // Remove edge between current drawable and stop
                copy.remove(currentDrawable, stop);
            }
        });

        // Remove all nodes of type stop:
        // all inbound/outbound edges have been removed above
        originalGraph.breadthFirstTraversal(drawable -> {
            if (drawable instanceof StopDrawable) {
                copy.remove(drawable);
            }
        });

        return copy;
    }

    private static boolean successorHasTypeStopDrawable(FlowGraph graph, Drawable drawable) {
        if (graph.successors(drawable).size() == 1) {
            return graph.successors(drawable)
                    .stream()
                    .anyMatch(drawable1 -> drawable1 instanceof StopDrawable);
        }
        return false;
    }

}
